package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrapbookDao {
    @Query("SELECT * FROM scrapbook_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<ScrapbookEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: ScrapbookEntry)

    @Delete
    suspend fun deleteEntry(entry: ScrapbookEntry)

    @Query("DELETE FROM scrapbook_entries WHERE id = :id")
    suspend fun deleteById(id: Int)
}
