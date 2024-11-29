package com.example.mocktest.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mocktest.data.NewRecipe
import com.example.mocktest.databinding.NewRecipeDetailActivityBinding
import com.example.mocktest.sqlite.RecipeDAO

class NewRecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: NewRecipeDetailActivityBinding
    private lateinit var recipeDAO: RecipeDAO
    private var recipeId: Long = -1
    private lateinit var editRecipeActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = NewRecipeDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeDAO = RecipeDAO(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Initializes ActivityResultLauncher for handling the result of the edit.
        //ActivityResultLauncher is a class in Android's Activity Result API, which simplifies the handling of activity results.
        editRecipeActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val updatedRecipe = result.data?.getSerializableExtra("RECIPE") as? NewRecipe
                updatedRecipe?.let {
                    //Refreshes the UI immediately after saving
                    displayRecipeDetails(it.id)
                }
            }
        }

        //Retrieves recipe ID from the intent
        recipeId = intent.getLongExtra(DetailActivity.EXTRA_RECIPE_ID, -1)

        if (recipeId != -1L) {
            displayRecipeDetails(recipeId)
        } else {
            Toast.makeText(this, "Invalid Recipe ID", Toast.LENGTH_SHORT).show()
        }

        binding.editRecipeButton.setOnClickListener {
            //Starts CreateRecipeActivity
            val intent = Intent(this, CreateRecipeActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, recipeId)
            editRecipeActivityResultLauncher.launch(intent)
        }
    }

    private fun displayRecipeDetails(recipeId: Long) {
        //Gets recipe details from the database using the ID
        val recipe = recipeDAO.findById(recipeId)

        if (recipe != null) {
            binding.recipeNameTextView.text = recipe.name
            binding.newRecipeIngredients.text = recipe.ingredients
            binding.newRecipeInstructions.text = recipe.instructions
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()  //Closes the activity and go back to the previous one
                return true
            }
        }
        return true
    }
}




