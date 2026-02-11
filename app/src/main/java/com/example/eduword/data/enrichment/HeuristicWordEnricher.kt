package com.example.eduword.data.enrichment

class HeuristicWordEnricher : WordEnricher {
    override suspend fun enrich(rawWord: String): EnrichedWord {
        val w = rawWord.trim()
        val lemma = w.replaceFirstChar { it.titlecase() }

        val article = null // без словника/AI краще не вигадувати
        val plural = null
        val uk = ""
        val en = ""

        return EnrichedWord(article, lemma, plural, uk, en)
    }
}
