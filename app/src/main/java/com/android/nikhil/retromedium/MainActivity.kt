package com.android.nikhil.retromedium

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.android.nikhil.retromedium.Constants.Companion.CLIENT_ID
import com.android.nikhil.retromedium.Constants.Companion.CLIENT_SECRET
import com.android.nikhil.retromedium.Constants.Companion.REDIRECT_URI
import com.android.nikhil.retromedium.Constants.Companion.STATE_OK
import com.android.nikhil.retromedium.networking.RetrofitClient
import com.android.nikhil.retromedium.networking.RetrofitInterface
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PrivatePropertyName")
class MainActivity : AppCompatActivity() {

    private val LOG_TAG = MainActivity::class.java.simpleName
    private var publicationsAdapter: PublicationRecyclerAdapter? = null

    companion object {
        private var retrofitInterface: RetrofitInterface? = null
    }

    private lateinit var sharedPrefHelper: SharedPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the shared preference
        sharedPrefHelper = SharedPreferenceHelper(getPreferences(Context.MODE_PRIVATE))

        // Check if temporary token is received
        val uri = intent?.data
        Log.d(LOG_TAG, uri.toString().startsWith(REDIRECT_URI).toString())
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            handleOAuth(uri)
        } else {
            // Create a retrofit interface
            retrofitInterface = RetrofitClient.getRetrofitClient().create(RetrofitInterface::class.java)
            if (sharedPrefHelper.getSharedPreferenceValue("LOGIN_STATE", false) == false) {
                // The user is not logged in!
                val builder = StringBuilder().apply {
                    append("https://medium.com/m/oauth/authorize?")
                    append("client_id=$CLIENT_ID&")
                    append("scope=basicProfile,publishPost,listPublications&")
                    append("state=$STATE_OK&")
                    append("response_type=code&")
                    append("redirect_uri=$REDIRECT_URI")
                }
                // Launch the intent so that the user can login to the OAuth provider
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(builder.toString()))
                startActivity(intent)
            } else {
                // User is logged in.

            }
        }
    }

    private fun handleOAuth(uri: Uri) {
        val state = uri.getQueryParameter("state")
        if (state == STATE_OK) {
            val token = uri.getQueryParameter("code")
            val builder = StringBuilder().apply {
                append("code=$token&")
                append("client_id=$CLIENT_ID&")
                append("client_secret=$CLIENT_SECRET&")
                append("grant_type=authorization_code&")
                append("redirect_uri=$REDIRECT_URI")
            }
            Log.d(LOG_TAG, "STATE_OK")
            val callLongToken = retrofitInterface?.getLongToken(token, CLIENT_ID, CLIENT_SECRET, "authorization_code", REDIRECT_URI)
            Log.d(LOG_TAG, builder.toString())
            callLongToken?.enqueue(object : Callback<LongAccessToken> {
                override fun onResponse(call: Call<LongAccessToken>?, response: Response<LongAccessToken>?) {
                    Log.d(LOG_TAG, "onResponse")
                    val tokenObject = response?.body()
                    if (tokenObject != null) {
                        Log.d("MainActivity", tokenObject.access_token)
                        sharedPrefHelper.saveSharedPreferenceValue("LOGIN_STATE", true)
                        sharedPrefHelper.saveSharedPreferenceValue("ACCESS_TOKEN", tokenObject.access_token)
                        sharedPrefHelper.saveSharedPreferenceValue("EXPIRES_AT", tokenObject.expires_at)
                        getUserInfo()
                    }
                }

                override fun onFailure(call: Call<LongAccessToken>?, t: Throwable?) {
                    Log.d("MainActivity", t.toString())
                }
            })
        } else {
            Log.d(LOG_TAG, state)
        }
    }

    private fun getUserInfo() {
        val accessToken = sharedPrefHelper.getSharedPreferenceValue("ACCESS_TOKEN", "None").toString()
        Log.d(LOG_TAG, "getUserInfo method called")
        val callMe = retrofitInterface?.getUserInfo("Bearer $accessToken",
                "api.medium.com",
                "application/json",
                "application/json",
                "utf-8")

        callMe?.enqueue(object : Callback<DataWrapper> {
            override fun onResponse(call: Call<DataWrapper>?, response: Response<DataWrapper>?) {
                val body = response?.body()
                if (body != null) {
                    val data = body.data
                    sharedPrefHelper.saveSharedPreferenceValue("USER_ID", data.id)
                    Log.d(LOG_TAG, "User id: ${data.id}")
                    Log.d(LOG_TAG, "Access token: $accessToken")
                    Toast.makeText(applicationContext, "Welcome ${data.name}!", Toast.LENGTH_LONG).show()
                    getUserPublications(data.id, accessToken)
                }
            }
            override fun onFailure(call: Call<DataWrapper>?, t: Throwable?) {
                Log.d("MainActivity", t.toString())
            }
        })
    }

    private fun getUserPublications(userId: String, accessToken: String) {
        val publicationCall = retrofitInterface?.getUserPublications("Bearer $accessToken", userId)
        publicationCall?.enqueue(object : Callback<PublicationWrapper> {
            override fun onResponse(call: Call<PublicationWrapper>?, response: Response<PublicationWrapper>?) {
                val body = response?.body()
                if (body != null) {
                    publicationsAdapter = PublicationRecyclerAdapter(applicationContext,  body.data)
                    articlesRecyclerView.adapter = publicationsAdapter
                    articlesRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                }
            }
            override fun onFailure(call: Call<PublicationWrapper>?, t: Throwable?) {
                Log.d(LOG_TAG, t.toString())
            }

        })
    }
}
