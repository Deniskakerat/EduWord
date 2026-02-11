package com.example.eduword.ui.table

import android.util.Log
import com.example.eduword.data.entity.WordRowUi
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.data.wiktionary.TranslationEnricher
import java.util.UUID

suspend fun buildRowsWithAutoFill(repo: WordRepository, words: List<String>): List<WordRowUi> {
    val cleaned = words
        .map { it.trim() }
        .filter { it.length >= 2 }
        .distinct()

    // cache: lemma -> (article, plural)
    val wikiCache = mutableMapOf<String, Pair<String?, String?>>()

    // cache: lemma -> (uk, en) to avoid double calls
    val trCache = mutableMapOf<String, Pair<String, String>>()

    return cleaned.map { w ->
        val lemma = w.replaceFirstChar(Char::titlecase)
        val fromDb = repo.findGermanByLemma(lemma)

        // ---- Article / Plural from DB or Wiktionary ----
        var article = fromDb?.article
        var plural = fromDb?.plural

        if (article.isNullOrBlank() || plural.isNullOrBlank()) {
            val cached = wikiCache[lemma]
            if (cached != null) {
                if (article.isNullOrBlank()) article = cached.first
                if (plural.isNullOrBlank()) plural = cached.second
            } else {
                Log.d("WIKI", "Need wiki for $lemma (article/plural missing)")
                val wiki = WiktionaryEnricher.fetchGermanInfo(lemma)
                wikiCache[lemma] = wiki?.article to wiki?.plural
                if (article.isNullOrBlank()) article = wiki?.article
                if (plural.isNullOrBlank()) plural = wiki?.plural
            }
        }

        // ---- Translations from DB or Translation API ----
        var uk = (fromDb?.ukTranslation ?: "").trim()
        var en = (fromDb?.engTranslation ?: "").trim()
        Log.d("TRANS", "DB for $lemma: uk='${fromDb?.ukTranslation}', en='${fromDb?.engTranslation}' -> ui uk='$uk', en='$en'")
        Log.d("TRANS", "Check translate? $lemma: ukBlank=${uk.isBlank()} enBlank=${en.isBlank()}")

        if (uk.isBlank() || en.isBlank()) {
            Log.d("TRANS", "Need translation for $lemma")
            val newUk = if (uk.isBlank()) TranslationEnricher.translateDeToUk(lemma).orEmpty() else uk
            val newEn = if (en.isBlank()) TranslationEnricher.translateDeToEn(lemma).orEmpty() else en
            uk = newUk
            en = newEn
        }

        WordRowUi(
            id = UUID.randomUUID().toString(),
            original = w,
            selected = true,
            article = article,
            lemma = fromDb?.lemma ?: lemma,
            plural = plural,
            uk = uk,
            en = en,
            topic = fromDb?.topic ?: "",
            level = fromDb?.level ?: "A1"
        )
    }
}