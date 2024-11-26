package com.example.mocktest.data

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

data class RecipeResponse (
    @SerializedName ("recipes") val recipes: List<Recipe>
)

data class Recipe (
    @SerializedName("id") val id:Int,
    @SerializedName("name") val name:String,
    @SerializedName("ingredients") val ingredients:List<String>,
    @SerializedName("instructions") val instructions:List<String>,
    @SerializedName("recipes") val misc:Misc,
    @SerializedName("image") val image: String
)

data class Misc (
    @JsonAdapter(IntegerAdapter::class) @SerializedName("prepTimeMinutes") val prepTimeMinutes: Int,
    @JsonAdapter(IntegerAdapter::class) @SerializedName("cookTimeMinutes") val cookTimeMinutes: Int,
    @JsonAdapter(IntegerAdapter::class) @SerializedName("servings") val servings: Int,
    @SerializedName("difficulty") val difficulty: String,
    @JsonAdapter(IntegerAdapter::class) @SerializedName("caloriesPerServing") val caloriesPerServing: Int,
    @SerializedName("cuisine") val cuisine: String,
)

class IntegerAdapter : TypeAdapter<Int>() {
    override fun write(out: JsonWriter?, value: Int) {
        out?.value(value)
    }

    override fun read(`in`: JsonReader?): Int {
        if (`in` != null) {
            val value: String = `in`.nextString()
            if (value != "null") {
                return value.toInt()
            }
        }
        return 0
    }

}


