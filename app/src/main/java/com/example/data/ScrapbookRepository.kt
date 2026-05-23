package com.example.data

import kotlinx.coroutines.flow.Flow

class ScrapbookRepository(private val scrapbookDao: ScrapbookDao) {
    val allEntries: Flow<List<ScrapbookEntry>> = scrapbookDao.getAllEntries()

    suspend fun insert(entry: ScrapbookEntry) {
        scrapbookDao.insertEntry(entry)
    }

    suspend fun delete(entry: ScrapbookEntry) {
        scrapbookDao.deleteEntry(entry)
    }

    suspend fun deleteById(id: Int) {
        scrapbookDao.deleteById(id)
    }
}
