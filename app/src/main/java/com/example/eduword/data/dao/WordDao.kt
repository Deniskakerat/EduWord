package com.example.eduword.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.entity.WordProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("""
    SELECT * FROM words
    WHERE (:topic IS NULL OR topic = :topic)
    ORDER BY RANDOM()
    LIMIT 1
    """)
    suspend fun getRandomByTopic(topic: String?): WordEntity?
    @Query("SELECT DISTINCT topic FROM words ORDER BY topic")
    suspend fun getTopics(): List<String>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(items: List<WordEntity>): List<Long>
    @Insert
    suspend fun insert(word: WordEntity): Long

    @Query("SELECT * FROM word_progress WHERE wordId = :wordId LIMIT 1")
    suspend fun getProgress(wordId: Long): WordProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(p: WordProgressEntity)

    @Query("SELECT * FROM words ORDER BY id ASC")
    fun observeAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandom(): WordEntity?

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WordEntity>)
}
