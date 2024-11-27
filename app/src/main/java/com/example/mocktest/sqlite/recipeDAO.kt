package com.example.mocktest.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class RecipeDAO(private val context: Context) {

    private lateinit var db: SQLiteDatabase

    private fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    private fun close() {
        db.close()
    }

    fun insert(recipe: NewRecipe): Long {
        open()
        val values = ContentValues().apply {
            put(NewRecipe.COLUMN_NAME_TITLE, recipe.title)
            put(NewRecipe.COLUMN_INGREDIENTS, recipe.ingredients)
            put(NewRecipe.COLUMN_INSTRUCTIONS, recipe.instructions)
        }
        var id: Long = -1
        try {
            id = db.insert(NewRecipe.TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e("DB", "Error inserting recipe: ${e.stackTraceToString()}")
        } finally {
            close()
        }
        return id
    }

    fun update (recipe: NewRecipe): Int {
        open()
        val values = ContentValues().apply {
            put(NewRecipe.COLUMN_NAME_TITLE, recipe.title)
            put(NewRecipe.COLUMN_INGREDIENTS, recipe.ingredients)
            put(NewRecipe.COLUMN_INSTRUCTIONS, recipe.instructions)
        }
        var updatedRows = 0
        try {
            updatedRows = db.update(
                NewRecipe.TABLE_NAME,
                values,
                "${NewRecipe.COLUMN_ID} = ?",
                arrayOf(recipe.id.toString())
            )
        } catch (e: Exception) {
            Log.e("DB", "Error updating recipe: ${e.stackTraceToString()}")
        } finally {
            close()
        }
        return updatedRows
    }

    fun delete (recipe: NewRecipe): Int {
        open()
        var deletedRows = 0
        try {
            deletedRows = db.delete(
                NewRecipe.TABLE_NAME,
                "${NewRecipe.COLUMN_ID} = ?",
                arrayOf(recipe.id.toString())
            )
        } catch (e: Exception) {
            Log.e("DB", "Error deleting recipe: ${e.stackTraceToString()}")
        } finally {
            close()
        }
        return deletedRows
    }

    fun findById (id: Long) : NewRecipe? {
        open()
        val projection = arrayOf(NewRecipe.COLUMN_ID,
                                NewRecipe.COLUMN_NAME_TITLE,
                                NewRecipe.COLUMN_INGREDIENTS,
                                NewRecipe.COLUMN_INSTRUCTIONS)
        var recipe: NewRecipe? = null
        try {
            val cursor = db.query(
                NewRecipe.TABLE_NAME,
                projection,
                "${NewRecipe.COLUMN_ID} = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                val recipeId = cursor.getLong(cursor.getColumnIndexOrThrow(NewRecipe.COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(NewRecipe.COLUMN_NAME_TITLE))
                val ingredients = cursor.getString(cursor.getColumnIndexOrThrow(NewRecipe.COLUMN_INGREDIENTS))
                val instructions = cursor.getString(cursor.getColumnIndexOrThrow(NewRecipe.COLUMN_INSTRUCTIONS))

                recipe = NewRecipe(recipeId, name, ingredients, instructions)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
        return recipe
    }

    fun findAll() : List<NewRecipe> {
        open()
        val list: MutableList<NewRecipe> = mutableListOf()

        try {
            val projection = arrayOf(
                NewRecipe.COLUMN_ID,
                NewRecipe.COLUMN_NAME_TITLE,
                NewRecipe.COLUMN_INGREDIENTS,
                NewRecipe.COLUMN_INSTRUCTIONS
            )
            val cursor = db.query(
                NewRecipe.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
            )
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(NewRecipe.COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(NewRecipe.COLUMN_NAME_TITLE))
                val ingredients = cursor.getString(cursor.getColumnIndexOrThrow(NewRecipe.COLUMN_INGREDIENTS))
                val instructions = cursor.getString(cursor.getColumnIndexOrThrow(NewRecipe.COLUMN_INSTRUCTIONS))
                val recipe = NewRecipe(id, name, ingredients, instructions)
                list.add(recipe)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DB", e.stackTraceToString())
        } finally {
            close()
        }
        return list
    }
}