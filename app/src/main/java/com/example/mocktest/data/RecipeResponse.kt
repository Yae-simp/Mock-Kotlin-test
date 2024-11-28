package com.example.mocktest.data

import com.google.gson.annotations.SerializedName

data class RecipeResponse (
    @SerializedName ("recipes") val recipes: List<Recipe>
)

data class Recipe (
    @SerializedName("id") val id:Int,
    @SerializedName("name") override val name:String,
    @SerializedName("ingredients") val ingredients:List<String>,
    @SerializedName("instructions") val instructions:List<String>,
    @SerializedName("image") val image: String,
    @SerializedName("prepTimeMinutes") val prepTimeMinutes: Int,
    @SerializedName("cookTimeMinutes") val cookTimeMinutes: Int,
    @SerializedName("servings") val servings: Int,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("caloriesPerServing") val caloriesPerServing: Int,
    @SerializedName("cuisine") val cuisine: String,
) : Searchable

