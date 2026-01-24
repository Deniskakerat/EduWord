package com.example.eduword.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val language: String = "DE",
    val topic: String = "General",
    val level: String = "A1",
    val article: String? = null,   // der/die/das for nouns
    val lemma: String,             // Hund
    val plural: String? = null,    // Hunde
    val ukTranslation: String      // собака
)
