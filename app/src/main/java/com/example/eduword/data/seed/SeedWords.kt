package com.example.eduword.data.seed

import com.example.eduword.data.entity.WordEntity

object SeedWords {
    val germanA1 = listOf(
        WordEntity(topic="Tiere", level="A1", article="der", lemma="Hund", plural="Hunde", ukTranslation="собака"),
        WordEntity(topic="Tiere", level="A1", article="die", lemma="Katze", plural="Katzen", ukTranslation="кіт/кішка"),
        WordEntity(topic="Essen", level="A1", article="das", lemma="Brot", plural="Brote", ukTranslation="хліб"),
        WordEntity(topic="Alltag", level="A1", article="die", lemma="Schule", plural="Schulen", ukTranslation="школа"),
        WordEntity(topic="Alltag", level="A1", article="der", lemma="Tisch", plural="Tische", ukTranslation="стіл"),
        WordEntity(topic="Alltag", level="A1", article="das", lemma="Buch", plural="Bücher", ukTranslation="книга"),
        WordEntity(topic="Reisen", level="A1", article="der", lemma="Zug", plural="Züge", ukTranslation="потяг"),
        WordEntity(topic="Stadt", level="A1", article="die", lemma="Straße", plural="Straßen", ukTranslation="вулиця")
    )
}
