package com.example.mocktest.service

import com.example.mocktest.data.Recipe
import com.example.mocktest.data.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeService {
    @GET("recipes")
    suspend fun getRecipes(@Query("limit") limit: Int = 0): RecipeResponse

    @GET("recipes/search")
    suspend fun getRecipesByName(@Query("q") query: String): RecipeResponse

    @GET("recipes/{recipe-id}")
    suspend fun getRecipeById(@Path("recipe-id") id: Int): Recipe
}