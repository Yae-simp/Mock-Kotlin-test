package com.example.mocktest.sessionmanager

import android.content.Context


class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("recipe_session", Context.MODE_PRIVATE)

    //Stores the recipe ID as the "favorite" recipe.
    fun setFavorite(recipeID: Int) {
        val editor = prefs.edit()
        editor.putInt("favorite_recipe", recipeID)
        editor.apply()
    }

    //Retrieves the current favorite recipe's ID from the shared preferences.
    private fun getFavorite(): Int {
        //Looks inside the toy box (SharedPreferences) for the saved recipe ID.
        //If there is no favorite set, it returns -1 (meaning no favorite).
        return prefs.getInt("favorite_recipe", -1)
    }

    //Checks if the given recipe ID is the same as the favorite recipe.
    fun isFavorite(recipeId: Int) : Boolean {
        //Compares the recipe ID you're asking about with the one saved in the toy box.
        return recipeId == getFavorite() //Returns true if they match, false otherwise.
    }
}