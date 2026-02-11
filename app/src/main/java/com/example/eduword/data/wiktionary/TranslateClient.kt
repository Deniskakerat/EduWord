package com.example.eduword.data.wiktionary


import com.example.eduword.data.deepl.LibreTranslateApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object TranslateClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://libretranslate.de/") // або інший інстанс, але з /
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: LibreTranslateApi = retrofit.create(LibreTranslateApi::class.java)
}