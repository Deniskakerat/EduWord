package com.example.eduword.ui.table



import com.example.eduword.data.enrichment.WordEnricher
import com.example.eduword.data.entity.WordRowUi
import java.util.UUID

suspend fun buildWordTableRows(
    words: List<String>,
    enricher: WordEnricher
): List<WordRowUi> {
    // унікальні, чисті
    val cleaned = words
        .map { it.trim() }
        .filter { it.length >= 2 }
        .distinct()

    return cleaned.map { raw ->
        val enriched = enricher.enrich(raw)
        WordRowUi(
            id = UUID.randomUUID().toString(),
            original = raw,
            selected = true,
            article = enriched.article,
            lemma = enriched.lemma,
            plural = enriched.plural,
            uk = enriched.uk,
            en = enriched.en
        )
    }
}