package com.example.mocktest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mocktest.R
import com.example.mocktest.adapter.NewRecipeAdapter
import com.example.mocktest.data.NewRecipe
import com.example.mocktest.databinding.ActivityListOfNewRecipesBinding
import com.example.mocktest.sqlite.RecipeDAO

class ListOfCreatedRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfNewRecipesBinding
    private lateinit var adapter: NewRecipeAdapter
    private lateinit var newRecipe: MutableList<NewRecipe>
    private lateinit var recipeDAO: RecipeDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListOfNewRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_new_recipe)

        recipeDAO = RecipeDAO(this)
        newRecipe = recipeDAO.findAll().toMutableList()

        initView()
    }

    override fun onResume() {
        super.onResume()

        loadData()
    }

    private fun initView() {
    }

    private fun loadData() {
        newRecipe = recipeDAO.findAll().toMutableList()
        adapter.updateItems(newRecipe)
    }
}
