package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    fun getAsteroids(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): Call<String>
}

/*private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()*/

private val retrofit = Retrofit.Builder()
    // .addConverterFactory(MoshiConverterFactory.create(moshi))
    // .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy { retrofit.create(AsteroidApiService::class.java) }
}

enum class AsteroidApiStatus { LOADING, DONE }
enum class AsteroidApiFilter { TODAY, WEEK, ALL }