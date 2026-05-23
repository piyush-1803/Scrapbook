package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ScrapbookDatabase
import com.example.data.ScrapbookEntry
import com.example.data.ScrapbookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class ScrapbookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScrapbookRepository
    val entries: StateFlow<List<ScrapbookEntry>>

    // Selected entry for the Hero Polaroid display
    private val _selectedEntry = MutableStateFlow<ScrapbookEntry?>(null)
    val selectedEntry: StateFlow<ScrapbookEntry?> = _selectedEntry.asStateFlow()

    // Interactive tactile micro-toggles
    private val _isAmberGlowEnabled = MutableStateFlow(true) // Starts enabled for golden-hour glow
    val isAmberGlowEnabled: StateFlow<Boolean> = _isAmberGlowEnabled.asStateFlow()

    private val _isLinenFiberEnabled = MutableStateFlow(true) // Starts enabled for linen texture overlay
    val isLinenFiberEnabled: StateFlow<Boolean> = _isLinenFiberEnabled.asStateFlow()

    private val _isNostalgicGrainEnabled = MutableStateFlow(true) // Film grain overlay
    val isNostalgicGrainEnabled: StateFlow<Boolean> = _isNostalgicGrainEnabled.asStateFlow()

    // Vintage Cassette Ambient Audio Play/Pause
    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    // Active wave bar heights for the vintage cassette tape visualizer
    private val _audioWaveHeights = MutableStateFlow(List(16) { 0.2f })
    val audioWaveHeights: StateFlow<List<Float>> = _audioWaveHeights.asStateFlow()

    init {
        val database = ScrapbookDatabase.getDatabase(application, viewModelScope)
        repository = ScrapbookRepository(database.scrapbookDao())
        
        entries = repository.allEntries
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Observe entries to select the first one by default if nothing is selected yet
        viewModelScope.launch {
            entries.collect { allEntries ->
                if (_selectedEntry.value == null && allEntries.isNotEmpty()) {
                    _selectedEntry.value = allEntries.first()
                }
            }
        }

        // Coroutine to animate core vintage cassette label wave heights when audio is playing
        viewModelScope.launch {
            while (true) {
                if (_isAudioPlaying.value) {
                    _audioWaveHeights.value = List(16) {
                        // Fluctuate between 0.15f and 1.0f when playing
                        Random.nextFloat().coerceIn(0.15f, 1.0f)
                    }
                } else {
                    // Decay heights slowly into a faint idle heartbeat pulse
                    _audioWaveHeights.value = _audioWaveHeights.value.map { h ->
                        (h * 0.85f).coerceAtLeast(0.08f)
                    }
                }
                delay(120) // Responsive fluid framerate for the wave
            }
        }
    }

    // Toggle ambient play/pause
    fun toggleAudioPlayback() {
        _isAudioPlaying.value = !_isAudioPlaying.value
    }

    // Direct selection of polaroid scrapbook memory
    fun selectEntry(entry: ScrapbookEntry) {
        _selectedEntry.value = entry
    }

    // Micro-toggle handlers
    fun toggleAmberGlow() {
        _isAmberGlowEnabled.value = !_isAmberGlowEnabled.value
    }

    fun toggleLinenFiber() {
        _isLinenFiberEnabled.value = !_isLinenFiberEnabled.value
    }

    fun toggleNostalgicGrain() {
        _isNostalgicGrainEnabled.value = !_isNostalgicGrainEnabled.value
    }

    // Insert new scrapbook entry
    fun addScrapbookEntry(
        title: String,
        excerpt: String,
        dateText: String,
        mood: String,
        weather: String,
        audioLabel: String,
        imageResName: String = "img_polaroid_landscape"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newEntry = ScrapbookEntry(
                title = title,
                dateText = dateText,
                excerpt = excerpt,
                mood = mood,
                weather = weather,
                audioLabel = audioLabel,
                imageResName = imageResName
            )
            repository.insert(newEntry)
            
            // Auto-select the newly added entry after insertion
            viewModelScope.launch(Dispatchers.Main) {
                _selectedEntry.value = newEntry
            }
        }
    }

    // Delete a scrapbook entry
    fun deleteEntry(entry: ScrapbookEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(entry)
            
            // If the deleted entry was active, select the next available one
            viewModelScope.launch(Dispatchers.Main) {
                val currentList = entries.value
                val remainingList = currentList.filter { it.id != entry.id }
                if (_selectedEntry.value?.id == entry.id) {
                    _selectedEntry.value = remainingList.firstOrNull()
                }
            }
        }
    }
}
