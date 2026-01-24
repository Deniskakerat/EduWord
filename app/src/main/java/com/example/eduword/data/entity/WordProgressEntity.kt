package com.example.eduword.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "word_progress")
data class WordProgressEntity(
    @PrimaryKey val wordId: Long,
    val correctStreak: Int = 0,
    val wrongCount: Int = 0,
    val totalAttempts: Int = 0,
    val isLearned: Boolean = false,
    val lastSeenAt: Long = 0L
)