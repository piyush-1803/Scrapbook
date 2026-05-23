package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scrapbook_entries")
data class ScrapbookEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dateText: String,
    val excerpt: String,
    val mood: String, // "star_1", "star_2", "star_3", "star_4", "star_5" (Gold abstract star)
    val weather: String, // "sun", "rain", "wind", "cloud"
    val audioLabel: String, // Cassette soundtrack
    val imageResName: String, // Which drawable to load
    val timestamp: Long = System.currentTimeMillis()
)
