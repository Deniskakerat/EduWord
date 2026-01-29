package com.example.eduword.data.ocr

data class DetectedWordRow(
    val raw: String,
    var article: String?,     // der/die/das або null
    var lemma: String,
    var isIncluded: Boolean = true,
    var topic: String = "General",
    var level: String = "A1",
    var uk: String = "",
    var en: String = ""
)
