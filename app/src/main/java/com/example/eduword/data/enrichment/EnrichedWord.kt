package com.example.eduword.data.enrichment

data class EnrichedWord(
    val article: String?,
    val lemma: String,
    val plural: String?,
    val uk: String,
    val en: String
)

interface WordEnricher {
    suspend fun enrich(rawWord: String): EnrichedWord
}
