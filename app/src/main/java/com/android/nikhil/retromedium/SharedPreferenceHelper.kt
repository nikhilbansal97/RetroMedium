package com.android.nikhil.retromedium

import android.content.SharedPreferences

class SharedPreferenceHelper(private val preference: SharedPreferences) {

    private val editor = preference.edit()

    fun saveSharedPreferenceValue(key: String, value: Any) {
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.apply()
    }

    fun getSharedPreferenceValue(key: String, type: Any): Any? {
        return when (type) {
            is Int -> preference.getInt(key, 0)
            is String -> preference.getString(key, "None")
            is Float -> preference.getFloat(key, 0F)
            is Long -> preference.getLong(key, 0)
            is Boolean -> preference.getBoolean(key, false)
            else -> null
        }
    }

}