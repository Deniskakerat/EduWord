package com.example.eduword.data.wiktionary.mymemory

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyMemoryApi {
    @GET("get")
    suspend fun translate(
        @Query("q") q: String,
        @Query("langpair") langpair: String,
        // опціонально: дає кращі ліміти/ідентифікацію, можна не ставити
        @Query("de") email: String? = null
    ): Response<MyMemoryResponse>
}