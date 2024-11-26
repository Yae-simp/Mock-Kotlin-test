package com.example.mocktest.sessionmanager

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("recipe_session", Context.MODE_PRIVATE)

    fun setFavorite(recipeID: String) {
        val editor = prefs.edit()
        editor.putString("favorite_recipe", recipeID)
        editor.apply()
    }

    private fun getFavorite(): String {
        return prefs.getString("favorite_recipe", "")!!
    }

    fun isFavorite(horoscopeId: String) : Boolean {
        return horoscopeId == getFavorite()
    }


}