package com.example.mocktest.sessionmanager

import android.content.Context


class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("recipe_session", Context.MODE_PRIVATE)

    //Save the favorite recipe ID
    fun setFavorite(recipeID: String) {
        val editor = prefs.edit()
        editor.putString("favorite_recipe", recipeID)
        editor.apply()
    }

    //Retrieve the favorite recipe ID, or null if not set
    private fun getFavorite(): String? {
        return prefs.getString("favorite_recipe", null)
    }

    //Check if the provided recipe ID is the current favorite
    fun isFavorite(recipeId: String) : Boolean {
        return recipeId == getFavorite()
    }
}