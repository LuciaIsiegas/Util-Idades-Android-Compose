package com.example.util_idades.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService {

    @GET("api/v4/top-headlines")
    suspend fun getTopHeadlines(
        @Query("topic")    topic: String  = "breaking-news",
        @Query("lang")     lang: String   = "es",
        @Query("country")  country: String = "es",
        @Query("max")      max: Int        = 20,
        @Query("token")    apiKey: String  = NewsApiClient.API_KEY
    ): Response<NewsResponse>

    @GET("api/v4/search")
    suspend fun searchNews(
        @Query("q")        query: String,
        @Query("lang")     lang: String  = "es",
        @Query("max")      max: Int      = 20,
        @Query("token")    apiKey: String = NewsApiClient.API_KEY
    ): Response<NewsResponse>
}

object NewsApiClient {

    const val API_KEY = "38983095ac3dd4d1db12a1289d734730"
    private const val BASE_URL = "https://gnews.io/"

    val service: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}
