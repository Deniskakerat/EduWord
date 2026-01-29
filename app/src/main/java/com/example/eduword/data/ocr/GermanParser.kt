package com.example.eduword.data.ocr



object GermanWordParser {

    private val articleRegex =
        Regex("""\b(der|die|das)\s+([A-Za-zÄÖÜäöüß-]{2,})\b""", RegexOption.IGNORE_CASE)

    fun parse(text: String): List<DetectedWordRow> {
        val clean = text.replace("\n", " ")

        val matches = articleRegex.findAll(clean).toList()
        if (matches.isEmpty()) return emptyList()

        // de-duplicate by (article + lemma)
        val map = linkedMapOf<String, DetectedWordRow>()

        for (m in matches) {
            val art = m.groupValues[1].lowercase()
            val lemma = m.groupValues[2].replaceFirstChar { it.uppercase() } // nouns usually
            val key = "$art|$lemma"
            map.putIfAbsent(key, DetectedWordRow(raw = m.value, article = art, lemma = lemma))
        }
        return map.values.toList()
    }
}