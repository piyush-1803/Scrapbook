package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.ScrapbookEntry
import com.example.viewmodel.ScrapbookViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// Beautiful visual palette values
val ColorCreamCanvas = Color(0xFFFDFBF7)    // Soft cream canvas base
val ColorOatmealLinen = Color(0xFFF2ECE6)  // Muted oatmeal
val ColorSageGreen = Color(0xFF8A9A86)     // Sage accent
val ColorDustyTerracotta = Color(0xFFC48D75)// Terracotta warmth
val ColorWarmSepia = Color(0xFF534C46)      // Warm sepia charcoal body
val ColorSepiaSubtitle = Color(0xFF8B8076)  // Muted metadata
val ColorLinenLineGold = Color(0xFFC5A059)  // Metallic gold style

@Composable
fun ScrapbookScreen(
    viewModel: ScrapbookViewModel = viewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    
    // UI state for filters or overlays
    val isAmberGlow by viewModel.isAmberGlowEnabled.collectAsState()
    val isLinenFiber by viewModel.isLinenFiberEnabled.collectAsState()
    val isNostalgicGrain by viewModel.isNostalgicGrainEnabled.collectAsState()
    val isPlaying by viewModel.isAudioPlaying.collectAsState()
    val waveHeights by viewModel.audioWaveHeights.collectAsState()
    
    // Add memory dialog visibility
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorCreamCanvas)
            .testTag("app_container")
    ) {
        // Core interactive tactile linen background texture.
        // Draws organic paper fiber lines across the whole screen if Linen Fiber is toggled on.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    if (isLinenFiber) {
                        // Horizontal linen threads
                        val lineGap = 16f
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = Color(0x3B64584E),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                            y += lineGap + Random.nextFloat() * 4f
                        }
                        // Vertical linen threads
                        var x = 0f
                        while (x < size.width) {
                            drawLine(
                                color = Color(0x2F64584E),
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1f
                            )
                            x += lineGap + Random.nextFloat() * 4f
                        }
                    }
                }
        ) {
            // Scaffold setup to accommodate Edge-to-Edge window insets cleanly
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent, // Transparent so the beautiful textured background is displayed
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    BottomJournalNavBar(
                        onAddClick = { showAddDialog = true },
                        onHomeClick = {
                            if (entries.isNotEmpty()) {
                                viewModel.selectEntry(entries.first())
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    
                    // 1. Editorial Header Panel
                    EditorialHeader()

                    // 2. Bento Canvas Layout Scrollable Area
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        
                        // Active Memory Hero block: Polaroid photograph card (Bento Component 1)
                        item {
                            ActivePolaroidBlock(selectedEntry = selectedEntry)
                        }

                        // Mid-Section Bento: Side-by-side cassette & status stacked indicators
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Column 1: Vintage cassette player (weight 1f)
                                Box(modifier = Modifier.weight(1f)) {
                                    VintageCassetteBlock(
                                        isPlaying = isPlaying,
                                        activeLabel = selectedEntry?.audioLabel ?: "Quiet Whispering Woods - 432Hz",
                                        waveHeights = waveHeights,
                                        onPlayToggle = { viewModel.toggleAudioPlayback() }
                                    )
                                }

                                // Column 2: Ambient status stacked cell indicators (weight 1f)
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    MoodBentoCell(mood = selectedEntry?.mood ?: "star_5")
                                    WeatherBentoCell(weather = selectedEntry?.weather ?: "sun")
                                }
                            }
                        }

                        // Tactile Shaders: Debossed Paper Toggles (Bento Component 3)
                        item {
                            TactileShadersBentoBlock(
                                isAmberGlow = isAmberGlow,
                                isLinenFiber = isLinenFiber,
                                isNostalgicGrain = isNostalgicGrain,
                                onAmberToggle = { viewModel.toggleAmberGlow() },
                                onLinenToggle = { viewModel.toggleLinenFiber() },
                                onGrainToggle = { viewModel.toggleNostalgicGrain() }
                            )
                        }

                        // Bottom Entry Snippet highlight
                        item {
                            LatestReflectionBlock(entries = entries)
                        }

                        // Timeline Section Label
                        item {
                            Text(
                                text = "M E M O R Y   T I M E L I N E",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    letterSpacing = 2.sp,
                                    color = ColorSageGreen
                                ),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }

                        // Chronicles List: Vertical timeline chronicle of elements (Bento Component 4)
                        if (entries.isEmpty()) {
                            item {
                                EmptyChronicleHint(onAddClick = { showAddDialog = true })
                            }
                        } else {
                            items(entries) { entry ->
                                TimelineEntryRow(
                                    entry = entry,
                                    isSelected = entry.id == selectedEntry?.id,
                                    onSelect = { viewModel.selectEntry(entry) },
                                    onDelete = { viewModel.deleteEntry(entry) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Lighting overlay options
        // 1. Warm Golden-Hour Ambient Light Leak Glow Overlay
        if (isAmberGlow) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0x3DFADCA8), // Radiant sun wash
                                Color(0x11C28D60),
                                Color.Transparent
                            ),
                            center = Offset(0.9f, 0.1f), // Top right sunbeams
                            radius = 1800f
                        )
                    )
                    .drawBehind {
                        // Gentle ambient bottom glow
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x1AFFB56B), Color(0x18EA9E5B))
                            )
                        )
                    }
                    .testTag("amber_glow_overlay")
            )
        }

        // 2. Realistic Dynamic Film Grain Shader Overlay
        if (isNostalgicGrain) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Drawing microscopic film dots using deterministic random to prevent wild rendering heat
                        val random = Random(42)
                        val dotsCount = 1400
                        for (i in 0 until dotsCount) {
                            val rx = random.nextFloat() * size.width
                            val ry = random.nextFloat() * size.height
                            val rAlpha = random.nextFloat() * 0.14f + 0.02f
                            val rSize = random.nextFloat() * 1.5f + 0.8f
                            drawCircle(
                                color = ColorWarmSepia.copy(alpha = rAlpha),
                                radius = rSize,
                                center = Offset(rx, ry)
                            )
                        }
                    }
                    .testTag("film_grain_overlay")
            )
        }

        // 3. Narrative Add Memory Dialog Modal
        if (showAddDialog) {
            AddScrapbookEntryDialog(
                onDismiss = { showAddDialog = false },
                onAddEntry = { title, snippet, dateStr, mood, weather, audio ->
                    viewModel.addScrapbookEntry(
                        title = title,
                        excerpt = snippet,
                        dateText = dateStr,
                        mood = mood,
                        weather = weather,
                        audioLabel = audio
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

/**
 * Editorial Header aligning to Cozy Cafe and Kinfolk Magazine layouts.
 */
@Composable
fun EditorialHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "JOURNAL VOLUME IV",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 3.sp,
                        color = ColorSageGreen
                    )
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "Autumn Solace",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 28.sp,
                        fontStyle = FontStyle.Italic,
                        color = ColorWarmSepia,
                        letterSpacing = (-0.5).sp
                    )
                )
            }

            // Right side monogram circle "m."
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x11000000)
                    )
                    .clip(CircleShape)
                    .background(Color(0xFFF5F2EA))
                    .border(1.dp, Color(0xFFE8E2D5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "m.",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = ColorWarmSepia
                    ),
                    modifier = Modifier.offset(y = (-1).dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Delicate thin separation line in warm sepia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ColorWarmSepia.copy(alpha = 0.1f))
        )
    }
}

/**
 * Bento Box Component 1: Polaroid-style photograph frame.
 * Displays a slightly grainy warm landscape photo with hand-written cursive date at bottom.
 */
@Composable
fun ActivePolaroidBlock(selectedEntry: ScrapbookEntry?) {
    // Elegant soft dropped shadow container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x3B64584E),
                spotColor = Color(0x2F64584E)
            )
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFF0ECE4), RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("polaroid_card")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Polaroid Photographic Space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.24f) // Organic horizontal polaroid framing
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE8E2D5))
            ) {
                Crossfade(
                    targetState = selectedEntry,
                    animationSpec = tween(durationMillis = 400),
                    label = "polaroid_photo_crossfade"
                ) { active ->
                    if (active == null) {
                        // Empty/Intro art view
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            WatercolorSeaCanvas()
                        }
                    } else {
                        // Load Polaroid image or beautiful fallback art
                        if (active.imageResName == "img_polaroid_landscape") {
                            Image(
                                painter = painterResource(id = R.drawable.img_polaroid_landscape_1779511377105),
                                contentDescription = active.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Synthetic organic landscape drawings corresponding to different climates
                            Box(modifier = Modifier.fillMaxSize()) {
                                when (active.weather) {
                                    "rain" -> WatercolorSeaCanvas(isRainy = true)
                                    "wind" -> WatercolorMeadowCanvas(isWindy = true)
                                    "sun" -> WatercolorSunsetCanvas()
                                    else -> WatercolorMeadowCanvas(isWindy = false)
                                }
                            }
                        }
                    }
                }
                
                // Fine photographic vignette overlay to simulate depth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.Transparent, Color(0x0C000000), Color(0x18000000)),
                                radius = 600f
                            ),
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Handwritten-Style Inscription Block
            Crossfade(
                targetState = selectedEntry,
                animationSpec = tween(durationMillis = 300),
                label = "polaroid_text_crossfade"
            ) { active ->
                if (active != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Cozy narrative title
                        Text(
                            text = active.title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ColorWarmSepia,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Selected diary text
                        Text(
                            text = active.excerpt,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = ColorWarmSepia.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Vintage cursive date
                        Text(
                            text = active.dateText.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = ColorDustyTerracotta,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "A Moment Frozen in Paper",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 17.sp,
                            color = ColorWarmSepia
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please write or tap on a chronicle memory below to leaf through your personal nostalgic scrapbook.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = ColorSepiaSubtitle,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/**
 * Bento Component 2: Vintage Cassette tape label and mini audio-wave visualizer.
 * Colored in soft sage green, responds fluidly to playback with reactive heights.
 */
@Composable
fun VintageCassetteBlock(
    isPlaying: Boolean,
    activeLabel: String,
    waveHeights: List<Float>,
    onPlayToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = ColorSageGreen
            )
            .clip(RoundedCornerShape(20.dp))
            .background(ColorSageGreen)
            .clickable(onClick = onPlayToggle)
            .padding(14.dp)
            .testTag("play_pause_button")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top side: cassette slots & side label row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Micro tape slot visualization
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(16.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.4f))
                    )
                }

                // Side A label
                Text(
                    text = "SIDE A",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    )
                )
            }

            // Bottom side: wave bar visualizer & current track label
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Fluid audio-wave visualizer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Let's draw 14 bars to represent waves
                    val displayHeights = waveHeights.take(14)
                    displayHeights.forEachIndexed { idx, heightMultiplier ->
                        val animatedHeight by animateDpAsState(
                            targetValue = (heightMultiplier * 28).dp,
                            animationSpec = tween(110),
                            label = "wave_bounce_artistic"
                        )
                        
                        val opacity = when {
                            idx % 3 == 0 -> 0.4f
                            idx % 3 == 1 -> 0.7f
                            else -> 1f
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(animatedHeight)
                                .clip(RoundedCornerShape(30))
                                .background(Color.White.copy(alpha = opacity))
                        )
                    }
                }

                // Track label in Mono typewriter typography
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) Color.White else Color.White.copy(alpha = 0.4f))
                    )
                    Text(
                        text = activeLabel,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = Color.White,
                            letterSpacing = (-0.2).sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Mid-Section Bento: Mood Bento Cell (Column 2 upper element)
 */
@Composable
fun MoodBentoCell(mood: String) {
    val moodLabel = when (mood) {
        "star_5" -> "Blessed"
        "star_4" -> "Serene"
        "star_3" -> "Reflective"
        else -> "Soft Vibe"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x11000000)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFF0ECE4), RoundedCornerShape(16.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "✦",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = ColorDustyTerracotta
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = moodLabel.uppercase(),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp,
                    color = ColorSepiaSubtitle,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

/**
 * Mid-Section Bento: Weather Bento Cell (Column 2 lower element)
 */
@Composable
fun WeatherBentoCell(weather: String) {
    val weatherLabel = when (weather) {
        "sun" -> "16° Sunny"
        "rain" -> "11° Rainy"
        "wind" -> "14° Breezy"
        else -> "15° Cloudy"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(Color(0xFFF5F2EA), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE8E2D5), RoundedCornerShape(16.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(ColorDustyTerracotta)
            )
            Text(
                text = weatherLabel,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = ColorWarmSepia
                )
            )
        }
    }
}

/**
 * Bottom Entry Snippet highlight layout
 */
@Composable
fun LatestReflectionBlock(entries: List<ScrapbookEntry>) {
    val latest = entries.firstOrNull()
    val indexText = if (entries.isNotEmpty()) {
        String.format("%02d", entries.size)
    } else {
        "00"
    }
    val excerptText = latest?.excerpt ?: "The tea cup holds the silent secrets of the morning..."
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorCreamCanvas, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE8E2D5), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Index identifier
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F2EA))
                    .border(1.dp, Color(0xFFE8E2D5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = indexText,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF7D6B5D)
                )
            }
            
            // Excerpt column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = excerptText,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = ColorWarmSepia,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "LATEST REFLECTION",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = ColorSageGreen,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

/**
 * Bottom Navigation Bar mimicking highly curated print magazine bars
 */
@Composable
fun BottomJournalNavBar(
    onAddClick: () -> Unit,
    onHomeClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorCreamCanvas)
            .drawBehind {
                drawLine(
                    color = Color(0xFFF0ECE4),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left menu item: Home
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onHomeClick
                    )
                    .width(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(ColorWarmSepia)
                )
                Text(
                    text = "HOME",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = ColorWarmSepia,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            // Center Primary FAB: Terracotta Adding Wheel
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        ambientColor = ColorDustyTerracotta,
                        spotColor = ColorDustyTerracotta
                    )
                    .clip(CircleShape)
                    .background(ColorDustyTerracotta)
                    .clickable(onClick = onAddClick)
                    .testTag("add_memory_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-1).dp)
                )
            }

            // Right menu item: Archive
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .width(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(ColorWarmSepia.copy(alpha = 0.4f))
                )
                Text(
                    text = "ARCHIVE",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = ColorWarmSepia.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

/**
 * Bento Component 3: Subtle, tactile micro-toggles that look like raised or debossed paper buttons.
 * Adds tactile neomorphic dimensions to represent "Warm Amber", "Linen Fibre", "Film Grain" live variables.
 */
@Composable
fun TactileShadersBentoBlock(
    isAmberGlow: Boolean,
    isLinenFiber: Boolean,
    isNostalgicGrain: Boolean,
    onAmberToggle: () -> Unit,
    onLinenToggle: () -> Unit,
    onGrainToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DebossedPaperButton(
            label = "AMBER GLOW",
            isActive = isAmberGlow,
            onClick = onAmberToggle,
            modifier = Modifier.weight(1f).testTag("toggle_amber_glow")
        )
        DebossedPaperButton(
            label = "LINEN FIBER",
            isActive = isLinenFiber,
            onClick = onLinenToggle,
            modifier = Modifier.weight(1f).testTag("toggle_linen_fiber")
        )
        DebossedPaperButton(
            label = "FILM DOSAGE",
            isActive = isNostalgicGrain,
            onClick = onGrainToggle,
            modifier = Modifier.weight(1f).testTag("toggle_film_grain")
        )
    }
}

/**
 * Custom debossed neomorphic design modifier simulation.
 * Active state is recessed/sunken into the canvas (darker background + thick inset shadow).
 * Inactive state is elevated (brighter, with drop shadow).
 */
@Composable
fun DebossedPaperButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonBg by animateColorAsState(if (isActive) ColorOatmealLinen else ColorCreamCanvas, label = "toggle_bg")
    val strokeColor by animateColorAsState(if (isActive) ColorDustyTerracotta else ColorWarmSepia.copy(alpha = 0.2f), label = "toggle_stroke")
    val elevationVal = if (isActive) 0.dp else 3.dp
    
    Box(
        modifier = modifier
            .shadow(
                elevation = elevationVal,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x3B64584E),
                spotColor = Color(0x2F64584E)
            )
            .background(buttonBg, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = strokeColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isActive) ColorDustyTerracotta else ColorWarmSepia.copy(alpha = 0.3f))
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = if (isActive) ColorWarmSepia else ColorWarmSepia.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Empty Chronicle screen helper
 */
@Composable
fun EmptyChronicleHint(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorOatmealLinen.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .border(1.dp, ColorWarmSepia.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = ColorDustyTerracotta.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chronicle is Empty",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ColorWarmSepia
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap + in the bottom bar to scribble your very first calming memory card.",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = ColorSepiaSubtitle
            )
        }
    }
}

/**
 * Bento Component 4: Vertical Timeline Row.
 * Utilizes delicate gold line-art icons for weather and mood.
 */
@Composable
fun TimelineEntryRow(
    entry: ScrapbookEntry,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val activeBorderColor by animateColorAsState(if (isSelected) ColorDustyTerracotta else Color.Transparent, label = "row_border")
    val activeBgColor by animateColorAsState(if (isSelected) ColorCreamCanvas else ColorOatmealLinen.copy(alpha = 0.7f), label = "row_bg")
    val textStyleWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    val elevationDp = if (isSelected) 3.dp else 0.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevationDp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x3B64584E)
            )
            .background(activeBgColor, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) activeBorderColor else ColorWarmSepia.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelect
            )
            .padding(12.dp)
            .testTag("timeline_item_${entry.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.width(72.dp)
        ) {
            WeatherLineArtIcon(weather = entry.weather)
            MoodLineArtIcon(mood = entry.mood)
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(1.dp)
                .height(28.dp)
                .background(ColorWarmSepia.copy(alpha = 0.15f))
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        ) {
            Text(
                text = entry.title,
                fontFamily = FontFamily.Serif,
                fontWeight = textStyleWeight,
                fontSize = 14.sp,
                color = ColorWarmSepia,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(1.dp))
            
            Text(
                text = entry.excerpt,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = ColorSepiaSubtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Text(
                text = entry.dateText.substringBefore(","),
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 11.sp,
                color = ColorDustyTerracotta
            )
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(24.dp)
                    .testTag("delete_item_button_${entry.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Memory",
                    tint = ColorWarmSepia.copy(alpha = 0.4f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Line art weather icon drawer
 */
@Composable
fun WeatherLineArtIcon(weather: String) {
    Canvas(
        modifier = Modifier
            .size(24.dp)
            .testTag("weather_icon_$weather")
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val strokeWidth = 1.3f
        
        when (weather) {
            "sun" -> {
                // Miniature sun drawing
                drawCircle(
                    color = ColorLinenLineGold,
                    radius = 4.5f,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )
                // radiating rays
                val rayLength = 5.5f
                val rayStart = 7f
                for (i in 0 until 8) {
                    val angle = (i * 45) * Math.PI / 180f
                    val startX = center.x + (Math.cos(angle) * rayStart).toFloat()
                    val startY = center.y + (Math.sin(angle) * rayStart).toFloat()
                    val endX = center.x + (Math.cos(angle) * (rayStart + rayLength)).toFloat()
                    val endY = center.y + (Math.sin(angle) * (rayStart + rayLength)).toFloat()
                    drawLine(
                        color = ColorLinenLineGold,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeWidth
                    )
                }
            }
            "rain" -> {
                // Delicate drops representing cloudburst
                // Draw a cloud arc line
                val cloudWidth = 14f
                val cloudHeight = 8f
                drawArc(
                    color = ColorLinenLineGold,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(center.x - cloudWidth/2, center.y - cloudHeight),
                    size = Size(cloudWidth, cloudHeight),
                    style = Stroke(width = strokeWidth)
                )
                // Draw 2 minimalist raindrop diagonal ticks
                drawLine(
                    color = ColorLinenLineGold,
                    start = Offset(center.x - 3f, center.y + 1f),
                    end = Offset(center.x - 4.5f, center.y + 5f),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = ColorLinenLineGold,
                    start = Offset(center.x + 2f, center.y + 1f),
                    end = Offset(center.x + 0.5f, center.y + 5f),
                    strokeWidth = strokeWidth
                )
            }
            "wind" -> {
                // Subtle blowing breeze ripples
                drawLine(
                    color = ColorLinenLineGold,
                    start = Offset(center.x - 7f, center.y - 3f),
                    end = Offset(center.x + 6f, center.y - 3f),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = ColorLinenLineGold,
                    start = Offset(center.x - 5f, center.y + 2f),
                    end = Offset(center.x + 8f, center.y + 2f),
                    strokeWidth = strokeWidth
                )
            }
            else -> { // clouds
                // A puffy abstract cloudline structure
                drawArc(
                    color = ColorLinenLineGold,
                    startAngle = 160f,
                    sweepAngle = 220f,
                    useCenter = false,
                    topLeft = Offset(center.x - 8f, center.y - 6f),
                    size = Size(10f, 10f),
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = ColorLinenLineGold,
                    startAngle = 200f,
                    sweepAngle = 200f,
                    useCenter = false,
                    topLeft = Offset(center.x - 1f, center.y - 7f),
                    size = Size(9f, 9f),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }
}

/**
 * Line art Gold Mood Icon.
 * Displays abstract stars resembling hand-drawn star motifs.
 */
@Composable
fun MoodLineArtIcon(mood: String) {
    Canvas(
        modifier = Modifier
            .size(24.dp)
            .testTag("mood_icon_$mood")
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val strokeWidth = 1.3f
        
        // Count of star lines/nodes matching mood value
        val points = when (mood) {
            "star_5" -> 5
            "star_4" -> 4
            "star_3" -> 3
            else -> 4 // fallback
        }

        // Draw custom abstract floral star lines
        val radius = 7.5f
        val innerRadius = 3f
        val path = Path()
        
        val angleStep = Math.PI / points
        for (i in 0 until (points * 2)) {
            val r = if (i % 2 == 0) radius else innerRadius
            val term = i * angleStep
            val px = cx + (Math.cos(term) * r).toFloat()
            val py = cy + (Math.sin(term) * r).toFloat()
            if (i == 0) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
            }
        }
        path.close()
        
        drawPath(
            path = path,
            color = ColorLinenLineGold,
            style = Stroke(width = strokeWidth)
        )
    }
}


/**
 * Dialog Box supporting tactile inputs representing mood/weather/cassettes labels to insert memory logs reactively.
 */
@Composable
fun AddScrapbookEntryDialog(
    onDismiss: () -> Unit,
    onAddEntry: (title: String, snippet: String, date: String, mood: String, weather: String, audio: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("star_5") }
    var weather by remember { mutableStateOf("sun") }
    var audio by remember { mutableStateOf("Analog Vinyl Ambient Crackle") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x3B64584E))
                .background(ColorCreamCanvas, RoundedCornerShape(16.dp))
                .border(1.dp, ColorOatmealLinen, RoundedCornerShape(16.dp))
                .padding(20.dp)
                .testTag("add_memory_dialog")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Topic header
                Text(
                    text = "S C R I B B L E   M E M O R Y",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        color = ColorSageGreen
                    )
                )

                // Title Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "MEMORY TITLE",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = ColorWarmSepia.copy(alpha = 0.7f)
                        )
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Vintage Bookstore Find") },
                        modifier = Modifier.fillMaxWidth().testTag("input_title"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = ColorWarmSepia,
                            unfocusedTextColor = ColorWarmSepia,
                            focusedBorderColor = ColorSageGreen,
                            unfocusedBorderColor = ColorOatmealLinen
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }

                // Excerpt Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "JOURNAL DESCRIPTION / EXCERPT",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = ColorWarmSepia.copy(alpha = 0.7f)
                        )
                    )
                    OutlinedTextField(
                        value = snippet,
                        onValueChange = { snippet = it },
                        placeholder = { Text("Scribble your comforting memories into paper...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("input_excerpt"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = ColorWarmSepia,
                            unfocusedTextColor = ColorWarmSepia,
                            focusedBorderColor = ColorSageGreen,
                            unfocusedBorderColor = ColorOatmealLinen
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }

                // Tactile Mood Stars selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "MEMORABLE VIBE / MOOD",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = ColorWarmSepia.copy(alpha = 0.7f)
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("star_3", "star_4", "star_5").forEach { starKey ->
                            val starLabel = "Star " + starKey.substringAfter("_")
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (mood == starKey) ColorSageGreen else ColorOatmealLinen)
                                    .clickable { mood = starKey }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (mood == starKey) ColorCreamCanvas else ColorWarmSepia.copy(alpha = 0.6f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = starLabel,
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = if (mood == starKey) ColorCreamCanvas else ColorWarmSepia
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Tactile Climate / Weather selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "WEATHER ATMOSPHERE",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = ColorWarmSepia.copy(alpha = 0.7f)
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("sun" to "Sunny", "rain" to "Rainy", "wind" to "Breezy", "cloud" to "Cloudy").forEach { (key, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (weather == key) ColorSageGreen else ColorOatmealLinen)
                                    .clickable { weather = key }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = if (weather == key) ColorCreamCanvas else ColorWarmSepia
                                    )
                                )
                            }
                        }
                    }
                }

                // Audio track labeling options
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "CASSETTE TAPE SOUNDTRACK",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = ColorWarmSepia.copy(alpha = 0.7f)
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Rain Cafè", "Wind Chimes", "Vinyl Crackle").forEach { name ->
                            val fullTrack = when (name) {
                                "Rain Cafè" -> "Cozy Cafe Rain lofi - 432Hz"
                                "Wind Chimes" -> "Misty Wind Chimes & Crackle"
                                "Vinyl Crackle" -> "Analog Vinyl Ambient Crackle"
                                else -> name
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (audio == fullTrack) ColorSageGreen else ColorOatmealLinen)
                                    .clickable { audio = fullTrack }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.uppercase(),
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = if (audio == fullTrack) ColorCreamCanvas else ColorWarmSepia
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Dialog Buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // DISMISS
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "DISCARD",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = ColorWarmSepia.copy(alpha = 0.6f)
                            )
                        )
                    }

                    // SAVE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ColorDustyTerracotta)
                            .clickable {
                                if (title.isNotBlank() && snippet.isNotBlank()) {
                                    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date()).uppercase()
                                    onAddEntry(title, snippet, formattedDate, mood, weather, audio)
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PRESS INTO LINEN",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = ColorCreamCanvas,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}


/**
 * -------------------------------------------------------------
 * BEAUTIFUL DYNAMIC WATERCOLOR CANVAS ILLUSTRATIONS
 * -------------------------------------------------------------
 */

@Composable
fun WatercolorSeaCanvas(isRainy: Boolean = false) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Soft blue sea watercolor gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE8ECE9), // Soft sea mist
                    Color(0xFF8FA59C),
                    Color(0xFF6C8980)
                )
            )
        )
        // Misty horizon circle representation
        drawCircle(
            color = Color(0x3BFFF2DF),
            radius = 64f,
            center = Offset(size.width / 2f, size.height * 0.4f)
        )
        // Rainy lines
        if (isRainy) {
            val random = Random(12)
            for (i in 0 until 18) {
                val rx = random.nextFloat() * size.width
                val ry = random.nextFloat() * size.height
                drawLine(
                    color = Color(0x40FFFFFF),
                    start = Offset(rx, ry),
                    end = Offset(rx - 8f, ry + 16f),
                    strokeWidth = 2f
                )
            }
        }
    }
}

@Composable
fun WatercolorMeadowCanvas(isWindy: Boolean = false) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Muted sage green mountain & sky wash
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF9EFE6), // Soft beige sky
                    Color(0xFFC7BCAE),
                    Color(0xFF8A9A86)  // Sage meadows
                )
            )
        )
        // Rolling field curves representing mountains
        val path = Path()
        path.moveTo(0f, size.height * 0.7f)
        path.quadraticTo(size.width * 0.4f, size.height * 0.65f, size.width * 0.8f, size.height * 0.85f)
        path.lineTo(size.width, size.height * 0.72f)
        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)
        path.close()
        drawPath(
            path = path,
            color = Color(0xFF758572)
        )

        // Blowing golden wind ripples
        if (isWindy) {
            val windPath = Path()
            windPath.moveTo(size.width * 0.1f, size.height * 0.3f)
            windPath.quadraticTo(size.width * 0.5f, size.height * 0.25f, size.width * 0.9f, size.height * 0.35f)
            drawPath(
                path = windPath,
                color = Color(0x40EFE2C2),
                style = Stroke(width = 3f)
            )
        }
    }
}

@Composable
fun WatercolorSunsetCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Cozy terracotta mountain sunset
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFEADBCE), // Golden sun background sky
                    Color(0xFFDFBA9E),
                    Color(0xFFC48D75)  // Terracotta ground
                )
            )
        )
        // Giant golden sun
        drawCircle(
            color = Color(0x8DFFDFBD),
            radius = 110f,
            center = Offset(size.width / 2f, size.height * 0.62f)
        )
        
        // Abstract organic bird strokes
        val birdPath1 = Path()
        birdPath1.moveTo(size.width * 0.28f, size.height * 0.24f)
        birdPath1.quadraticTo(size.width * 0.31f, size.height * 0.22f, size.width * 0.34f, size.height * 0.25f)
        birdPath1.quadraticTo(size.width * 0.37f, size.height * 0.22f, size.width * 0.40f, size.height * 0.24f)
        drawPath(
            path = birdPath1,
            color = Color(0x4D534C46),
            style = Stroke(width = 1.5f)
        )
    }
}
