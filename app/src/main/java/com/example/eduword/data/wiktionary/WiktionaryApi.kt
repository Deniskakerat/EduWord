package com.example.eduword.data.wiktionary

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WiktionaryApi {
    // MediaWiki API: fetch page wikitext
    @GET("w/api.php")
    suspend fun parse(
        @Query("action") action: String = "parse",
        @Query("page") page: String,
        @Query("prop") prop: String = "wikitext",
        @Query("format") format: String = "json"
    ): WikiParseResponse
}

data class WikiParseResponse(val parse: WikiParse?)
data class WikiParse(val wikitext: WikiWikitext?)
data class WikiWikitext(   @Json(name = "*")
                           val text: String?)

object WiktionaryClient {
    private const val BASE_URL = "https://de.wiktionary.org/"

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val userAgentInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder()
            // Wikimedia recommends an informative UA; include app + contact or repo link
            .header("User-Agent", "EduWord/1.0 (Android; contact: youremail@example.com)")
            // Some Wikimedia docs mention Api-User-Agent for cases where UA can't be set
            .header("Api-User-Agent", "EduWord/1.0 (Android; contact: youremail@example.com)")
            .build()
        chain.proceed(req)
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
        .build()

    val api: WiktionaryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WiktionaryApi::class.java)
    }
}