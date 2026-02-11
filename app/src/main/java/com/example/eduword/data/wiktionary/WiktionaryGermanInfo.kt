package com.example.eduword.data.wiktionary


data class WiktionaryGermanInfo(
    val article: String?, // "der" / "die" / "das"
    val plural: String?
)

object WiktionaryGermanParser {

    // Very practical parser for common Wiktionary noun template:
    // |Genus=m/f/n   and  |Nominativ Plural=...
    fun parseNounInfo(wikitext: String): WiktionaryGermanInfo? {
        val genus = Regex("""\|\s*Genus\s*=\s*([mfn])""").find(wikitext)?.groupValues?.get(1)
        val plural = Regex("""\|\s*Nominativ\s+Plural\s*=\s*([^\n|}]+)""")
            .find(wikitext)?.groupValues?.get(1)?.trim()

        val article = when (genus) {
            "m" -> "der"
            "f" -> "die"
            "n" -> "das"
            else -> null
        }

        // Clean plural a bit (remove markup)
        val cleanPlural = plural
            ?.replace(Regex("""\[\[|\]\]"""), "")
            ?.replace(Regex("""<.*?>"""), "")
            ?.trim()
            ?.takeUnless { it.isBlank() }

        // If we found neither, return null
        if (article == null && cleanPlural == null) return null
        return WiktionaryGermanInfo(article, cleanPlural)
    }
}
