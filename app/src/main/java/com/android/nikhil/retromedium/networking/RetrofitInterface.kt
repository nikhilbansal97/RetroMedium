package com.android.nikhil.retromedium.networking

import com.android.nikhil.retromedium.DataWrapper
import com.android.nikhil.retromedium.LongAccessToken
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @FormUrlEncoded
    @Headers("Host: api.medium.com",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: application/json",
            "Accept-Charset: utf-8")
    @POST("v1/tokens")
    fun getLongToken(@Field("code") token: String,
                     @Field("client_id") client_id: String,
                     @Field("client_secret") client_secret: String,
                     @Field("grant_type") type: String,
                     @Field("redirect_uri") uri: String): Call<LongAccessToken>

    @GET("me")
    @Headers("Host: api.medium.com",
            "Content-Type: application/json",
            "Accept: application/json",
            "Accept-Charset: utf-8")
    fun getUserInfo(@Header("Authorization") auth: String): Call<DataWrapper>

}