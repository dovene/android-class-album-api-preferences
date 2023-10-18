package com.dovene.httprequestssharedprefkt

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class AlbumApi {
    private val baseUrl = "https://jsonplaceholder.typicode.com/"
    interface AlbumService {
        // For testing https://jsonplaceholder.typicode.com/photos?albumId=1
        @GET("photos")
        fun getBooks(@Query("albumId") id: Int): Call<List<Album>>
    }
    fun getClient(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}