package com.example.eduword.data.repository

import com.example.eduword.data.dao.WordDao
import com.example.eduword.data.entity.WordEntity
import kotlinx.coroutines.flow.Flow

class WordRepository(private val dao: WordDao) {

    fun observeAll(): Flow<List<WordEntity>> = dao.observeAll()

    suspend fun randomWord(): WordEntity? = dao.getRandom()

    suspend fun seedIfEmpty(seed: List<WordEntity>) {
        if (dao.count() == 0) dao.insertAll(seed)
    }
}
