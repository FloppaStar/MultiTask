package ru.floppastar.multitask.utils

import android.content.Context
import android.content.SharedPreferences

object PrefsManager {
    private const val PREFS_NAME = "multitask_prefs"
    private const val KEY_PROFILE_ID = "profile_id"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setProfileId(id: Int) {
        preferences.edit().putInt(KEY_PROFILE_ID, id).apply()
    }

    fun getProfileId(): Int {
        return preferences.getInt(KEY_PROFILE_ID, 0)
    }

    fun clearProfile() {
        setProfileId(0)
    }

    fun saveSortMode(mode: Int) {
        preferences.edit().putInt("sort_mode", mode).apply()
    }

    fun getSortMode(): Int {
        return preferences.getInt("sort_mode", 0)
    }
}