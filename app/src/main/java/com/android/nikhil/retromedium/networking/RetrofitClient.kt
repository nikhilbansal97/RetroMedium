package com.android.nikhil.retromedium.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("PrivatePropertyName")
class RetrofitClient {

    companion object {
        private const val BASE_URL = "https://api.medium.com/"
        private var retrofit: Retrofit? = null

        fun getRetrofitClient(): Retrofit {
            if (retrofit == null)
                retrofit = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(BASE_URL)
                        .build()
            return retrofit!!
        }
    }

}