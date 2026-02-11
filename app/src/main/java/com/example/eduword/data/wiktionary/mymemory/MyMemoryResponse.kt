package com.example.eduword.data.wiktionary.mymemory

data class MyMemoryResponse(
    val responseData: ResponseData?,
    val responseStatus: Int?,
    val responseDetails: String?,
    val matches: List<Match>?
) {
    data class ResponseData(
        val translatedText: String?
    )

    data class Match(
        val translation: String?
    )
}
