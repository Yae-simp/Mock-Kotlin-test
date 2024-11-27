package com.example.mocktest.sessionmanager

import android.content.Context


class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("recipe_session", Context.MODE_PRIVATE)

    //Save the favorite recipe ID
    fun setFavorite(recipeID: Int) {
        val editor = prefs.edit()
        editor.putInt("favorite_recipe", recipeID)
        editor.apply()
    }

    //Retrieve the favorite recipe ID, or null if not set
    private fun getFavorite(): Int {
        return prefs.getInt("favorite_recipe", -1)
    }

    //Check if the provided recipe ID is the current favorite
    fun isFavorite(recipeId: Int) : Boolean {
        return recipeId == getFavorite()
    }
}