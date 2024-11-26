package com.example.mocktest.service

import com.example.mocktest.data.Recipe
import com.example.mocktest.data.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeService {
    @GET("recipes")
    suspend fun getRecipes(): RecipeResponse

    @GET("recipes/{recipe-id}")
    suspend fun getRecipe(@Path("recipe-id") id: Int): Recipe
}