package com.example.mocktest.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "NewRecipesDatabase.db"


        private const val SQL_CREATE_TABLE = """
            CREATE TABLE ${NewRecipe.TABLE_NAME} (
                ${NewRecipe.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${NewRecipe.COLUMN_NAME_TITLE} TEXT,
                ${NewRecipe.COLUMN_INGREDIENTS} TEXT,
                ${NewRecipe.COLUMN_INSTRUCTIONS} TEXT
            )
        """

        private const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${NewRecipe.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }

    private fun onDestroy(db: SQLiteDatabase) {
        db.execSQL(SQL_DELETE_TABLE)
    }
}