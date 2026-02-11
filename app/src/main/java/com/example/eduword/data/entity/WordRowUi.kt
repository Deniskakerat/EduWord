package com.example.eduword.data.entity


data class WordRowUi(
    val id: String,               // тимчасовий id для UI, напр. UUID
    val original: String,         // як прийшло зі скану
    var selected: Boolean = true, // чекбокс включення (для імпорту)

    var article: String? = null,  // der/die/das або null
    var lemma: String = "",
    var plural: String? = null,
    var uk: String = "",
    var en: String = "",

    var topic: String? = null,    // обовʼязково перед імпортом
    var level: String? = null,    // обовʼязково перед імпортом

    // для валідації/підсвітки
    var errors: Set<RowError> = emptySet()
)


enum class RowError { MissingTopic, MissingLevel, MissingLemma, MissingTranslation }
