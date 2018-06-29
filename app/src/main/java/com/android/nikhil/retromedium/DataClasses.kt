package com.android.nikhil.retromedium

data class LongAccessToken(
        val token_type: String,
        val access_token: String,
        val refresh_token: String,
        val scope: ArrayList<String>,
        val expires_at: String
)

data class Data(
        val id: String,
        val username: String,
        val name: String,
        val url: String,
        val imageUrl: String
)

data class DataWrapper(val data: Data)