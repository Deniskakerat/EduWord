package com.example.eduword.data.wiktionary.mymemory

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object MyMemoryClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.mymemory.translated.net/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: MyMemoryApi = retrofit.create(MyMemoryApi::class.java)
}