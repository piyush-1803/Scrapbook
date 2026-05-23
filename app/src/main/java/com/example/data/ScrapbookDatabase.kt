package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ScrapbookEntry::class], version = 1, exportSchema = false)
abstract class ScrapbookDatabase : RoomDatabase() {
    abstract fun scrapbookDao(): ScrapbookDao

    companion object {
        @Volatile
        private var INSTANCE: ScrapbookDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ScrapbookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScrapbookDatabase::class.java,
                    "scrapbook_database"
                )
                .addCallback(ScrapbookDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ScrapbookDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.scrapbookDao())
                }
            }
        }

        suspend fun populateDatabase(dao: ScrapbookDao) {
            // First entry: Afternoon tea at Cozy Cafe
            dao.insertEntry(
                ScrapbookEntry(
                    title = "Afternoon Rain & Warm Tea",
                    dateText = "MAY 23, 2026",
                    excerpt = "The gentle pitter-patter of raindrops against the coffee shop window has created the most comforting canvas. Held a warm ceramic mug of spiced oolong tea. Time slowed down, and the amber light felt like a hug.",
                    mood = "star_5",
                    weather = "rain",
                    audioLabel = "Cozy Cafe Rain lofi - 432Hz",
                    imageResName = "img_polaroid_landscape" // Default generated image!
                )
            )
            // Second entry: Stroll through misty woods
            dao.insertEntry(
                ScrapbookEntry(
                    title = "Misty Forest Wander",
                    dateText = "MAY 19, 2026",
                    excerpt = "Stepped out early into the pine-scented paths. Forest was blanketed in quiet velvet fog. Found a tiny patch of wild sage growing beside a stone wall. Brought a sprig back to press between pages of linen paper.",
                    mood = "star_4",
                    weather = "wind",
                    audioLabel = "Misty Wind Chimes & Crackle",
                    imageResName = "img_misty_woods" // We can draw beautiful vector fallbacks if image isn't loaded or loaded
                )
            )
            // Third entry: Sunbeams in the study
            dao.insertEntry(
                ScrapbookEntry(
                    title = "Golden Sunbeams & Old Pages",
                    dateText = "MAY 12, 2026",
                    excerpt = "A perfect lazy morning in the study. Golden sunbeams illuminated dust motes dancing in the air. The faint smell of aged paper and dried lavender reminded me of childhood summers spent reading under the willow.",
                    mood = "star_3",
                    weather = "sun",
                    audioLabel = "Analog Vinyl Ambient Crackle",
                    imageResName = "img_golden_study"
                )
            )
        }
    }
}
