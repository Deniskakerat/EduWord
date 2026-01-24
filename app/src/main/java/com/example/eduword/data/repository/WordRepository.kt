package com.example.eduword.data.repository


import com.example.eduword.data.dao.WordDao
import com.example.eduword.data.entity.WordEntity
import kotlinx.coroutines.flow.Flow

import com.example.eduword.data.entity.WordProgressEntity
import kotlin.text.insert


class WordRepository(private val dao: WordDao) {

    suspend fun insertWord(word: WordEntity): Long = dao.insert(word)
    fun observeAll(): Flow<List<WordEntity>> = dao.observeAll()
    suspend fun recordAttempt(wordId: Long, isCorrect: Boolean) {
        val old = dao.getProgress(wordId) ?: WordProgressEntity(wordId = wordId)

        val newStreak = if (isCorrect) old.correctStreak + 1 else 0
        val newWrong = if (isCorrect) old.wrongCount else old.wrongCount + 1
        val newTotal = old.totalAttempts + 1
        val learned = newStreak >= 5

        dao.upsertProgress(
            old.copy(
                correctStreak = newStreak,
                wrongCount = newWrong,
                totalAttempts = newTotal,
                isLearned = learned,
                lastSeenAt = System.currentTimeMillis()
            )
        )
    }
    suspend fun seedMerge(seed: List<WordEntity>) {
        dao.insertAllIgnore(seed) // додасть тільки те, чого ще нема
    }
    suspend fun progress(wordId: Long): WordProgressEntity? = dao.getProgress(wordId)
    suspend fun topics(): List<String> = dao.getTopics()
    suspend fun randomWord(topic: String?): WordEntity? = dao.getRandomByTopic(topic)

    suspend fun randomWord(): WordEntity? = dao.getRandom()

    suspend fun seedIfEmpty(seed: List<WordEntity>) {
        if (dao.count() == 0) dao.insertAll(seed)
    }
}
