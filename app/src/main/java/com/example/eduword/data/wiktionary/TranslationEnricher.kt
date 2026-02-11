package com.example.eduword.data.wiktionary

import retrofit2.HttpException
import android.util.Log
import com.example.eduword.data.deepl.TranslateRequest
import com.example.eduword.data.wiktionary.mymemory.MyMemoryClient

object TranslationEnricher {

    // Якщо хочеш — можеш поставити свій email (не обов’язково)
    private val EMAIL: String? = null
    // private const val EMAIL: String? = "your@email.com"

    private fun langPair(target: String): String {
        val t = target.lowercase()
        // MyMemory часто очікує uk/ua як "UK", але стабільніше: de|uk та de|en
        return "de|$t"
    }

    suspend fun translateDe(word: String, target: String): String? {
        val tgt = target.lowercase()
        Log.d("TRANS", "MyMemory request $tgt for: $word")

        return try {
            val resp = MyMemoryClient.api.translate(
                q = word,
                langpair = langPair(tgt),
                email = EMAIL
            )

            if (!resp.isSuccessful) {
                Log.e("TRANS", "MyMemory HTTP ${resp.code()} for $word ($tgt)")
                return null
            }

            val body = resp.body()
            if (body?.responseStatus != 200) {
                Log.e("TRANS", "MyMemory status=${body?.responseStatus} details=${body?.responseDetails}")
                return null
            }

            // 1) головне поле
            val main = body.responseData?.translatedText?.trim()
            if (!main.isNullOrBlank()) {
                Log.d("TRANS", "MyMemory OK $tgt for $word => $main")
                return main
            }

            // 2) запасний варіант: matches
            val alt = body.matches
                ?.mapNotNull { it.translation?.trim() }
                ?.firstOrNull { it.isNotBlank() }

            Log.d("TRANS", "MyMemory OK (alt) $tgt for $word => $alt")
            alt
        } catch (t: Throwable) {
            Log.e("TRANS", "MyMemory FAIL $tgt for $word", t)
            null
        }
    }

    suspend fun translateDeToEn(word: String) = translateDe(word, "en")
    suspend fun translateDeToUk(word: String) = translateDe(word, "uk")
}