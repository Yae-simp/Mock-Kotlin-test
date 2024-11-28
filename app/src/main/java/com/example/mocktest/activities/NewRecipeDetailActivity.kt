package com.example.mocktest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mocktest.databinding.NewRecipeDetailActivityBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: NewRecipeDetailActivityBinding  // ViewBinding for this activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = NewRecipeDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the recipe details from the Intent
        val recipeName = intent.getStringExtra("RECIPE_NAME")
        val recipeDescription = intent.getStringExtra("RECIPE_DESCRIPTION")

        // Set the recipe details in the views
        binding.recipeNameTextView.text = recipeName
        binding.recipeDescriptionTextView.text = recipeDescription
    }
}
