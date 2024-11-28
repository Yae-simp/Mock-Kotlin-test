package com.example.mocktest.sessionmanager

import android.content.Context


class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("recipe_session", Context.MODE_PRIVATE)

    fun setFavorite(recipeID: Int) {
        val editor = prefs.edit()
        editor.putInt("favorite_recipe", recipeID)
        editor.apply()
    }

    private fun getFavorite(): Int {
        return prefs.getInt("favorite_recipe", -1)
    }

    fun isFavorite(recipeId: Int) : Boolean {
        return recipeId == getFavorite()
    }
}