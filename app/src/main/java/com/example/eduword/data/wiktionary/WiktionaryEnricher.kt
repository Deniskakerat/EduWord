import android.util.Log
import com.example.eduword.data.wiktionary.WiktionaryClient
import com.example.eduword.data.wiktionary.WiktionaryGermanInfo
import com.example.eduword.data.wiktionary.WiktionaryGermanParser

object WiktionaryEnricher {
    suspend fun fetchGermanInfo(lemma: String): WiktionaryGermanInfo? {
        Log.d("WIKI", "Requesting Wiktionary for: $lemma")

        return try {
            val resp = WiktionaryClient.api.parse(page = lemma)
            val wikitext = resp.parse?.wikitext?.text
            val info = WiktionaryGermanParser.parseNounInfo(wikitext.toString())
            Log.d("WIKI", "Parsed for $lemma → article=${info?.article}, plural=${info?.plural}")
            return info
            if (wikitext == null) {
                Log.w("WIKI", "No wikitext for $lemma")
                return null
            }

            Log.d("WIKI", "Wikitext received for $lemma")
            WiktionaryGermanParser.parseNounInfo(wikitext.toString())
        } catch (t: Throwable) {
            Log.e("WIKI", "Error loading $lemma", t)
            null
        }
    }
}