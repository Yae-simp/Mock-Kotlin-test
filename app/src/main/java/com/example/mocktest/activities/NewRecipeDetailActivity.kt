package com.example.mocktest.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mocktest.data.NewRecipe
import com.example.mocktest.databinding.NewRecipeDetailActivityBinding
import com.example.mocktest.sqlite.RecipeDAO

class NewRecipeDetailActivity : AppCompatActivity() {

    companion object {
        const val EDIT_RECIPE_REQUEST_CODE = 1001  // Unique request code for editing
    }

    private lateinit var binding: NewRecipeDetailActivityBinding
    private lateinit var recipeDAO: RecipeDAO
    private var recipeId: Long = -1

    private lateinit var editRecipeActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = NewRecipeDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeDAO = RecipeDAO(this)

        // Initialize ActivityResultLauncher for handling the result of the edit
        editRecipeActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val updatedRecipe = result.data?.getSerializableExtra("RECIPE") as? NewRecipe
                updatedRecipe?.let {
                    // Refresh the UI immediately after saving
                    displayRecipeDetails(it.id)
                }
            }
        }

        // Retrieve the recipe ID from the intent
        recipeId = intent.getLongExtra(DetailActivity.EXTRA_RECIPE_ID, -1)

        if (recipeId != -1L) {
            displayRecipeDetails(recipeId)
        } else {
            // Handle case where no valid ID is passed
            Toast.makeText(this, "Invalid Recipe ID", Toast.LENGTH_SHORT).show()
        }

        binding.editRecipeButton.setOnClickListener {
            // Start CreateRecipeActivity for result
            val intent = Intent(this, CreateRecipeActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, recipeId)
            editRecipeActivityResultLauncher.launch(intent)
        }
    }

    private fun displayRecipeDetails(recipeId: Long) {
        // Get recipe details from the database using the ID
        val recipe = recipeDAO.findById(recipeId)

        if (recipe != null) {
            // Populate your UI with the recipe details using View Binding
            binding.recipeNameTextView.text = recipe.name
            binding.newRecipeIngredients.text = recipe.ingredients
            binding.newRecipeInstructions.text = recipe.instructions
        } else {
            // Handle the case where the recipe is not found
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
        }
    }
}




