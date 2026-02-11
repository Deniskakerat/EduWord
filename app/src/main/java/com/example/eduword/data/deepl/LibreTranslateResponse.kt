package com.example.eduword.data.deepl

import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface LibreTranslateApi {
    @POST("translate")
    suspend fun translate(@Body req: TranslateRequest): retrofit2.Response<TranslateResponse>
}

data class LibreTranslateResponse(
    val translatedText: String?
)


@JsonClass(generateAdapter = true)
data class TranslateError(
    val error: String?
)
data class TranslateRequest(
    val q: String,
    val source: String = "de",
    val target: String,
    val format: String = "text",
    val alternatives: Int? = null,
    val api_key: String? = null
)

data class TranslateResponse(
    val translatedText: String?
)

data class LibreTranslateError(
    val error: String?
)
