package com.example.mocktest.sqlite

data class NewRecipe(
    val id: Long = -1,
    var title: String,
    var ingredients: String,
    var instructions: String
) {
    companion object {
        const val TABLE_NAME = "Recipe"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_INGREDIENTS = "ingredients"
        const val COLUMN_INSTRUCTIONS = "instructions"
    }
}