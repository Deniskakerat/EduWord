package com.example.eduword.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    indices = [Index(value = ["language", "article", "lemma"], unique = true)]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val language: String = "DE",
    val topic: String = "General",
    val level: String = "A1",
    val article: String? = null,
    val lemma: String,
    val plural: String? = null,
    val ukTranslation: String,
    val engTranslation: String
)
