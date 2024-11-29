package com.example.mocktest.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mocktest.databinding.ActivityAddNewRecipeBinding
import com.example.mocktest.data.NewRecipe
import com.example.mocktest.sqlite.RecipeDAO

class CreateRecipeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RECIPE_ID = "RECIPE_ID"
    }

    private lateinit var binding: ActivityAddNewRecipeBinding
    private var isEditing: Boolean = false
    private lateinit var recipeDAO: RecipeDAO
    private lateinit var recipe: NewRecipe
    private var recipeId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust the window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recipeDAO = RecipeDAO(this)

        // Get recipeId from the Intent
        recipeId = intent.getLongExtra(EXTRA_RECIPE_ID, -1L)

        // Check if the recipeId is valid for editing
        if (recipeId != -1L) {
            isEditing = true
            // Retrieve the recipe from the database
            recipe = recipeDAO.findById(recipeId) ?: NewRecipe(-1, "", "", "")
        } else {
            isEditing = false
            // Create a new recipe if no valid ID is passed
            recipe = NewRecipe(-1, "", "", "")
        }

        // Load views and data
        loadViews()
        loadData()
    }

    private fun loadViews() {
        // Close button to finish the activity
        binding.closeButton.setOnClickListener { finish() }

        // Save button to either save or update the recipe
        binding.saveButton.setOnClickListener {
            if (validateRecipe()) {
                saveRecipe()
            }
        }

        // TextWatcher for updating the recipe title dynamically
        binding.titleTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                recipe.title = s.toString()  // Update title as the user types
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadData() {
        // Update the title based on whether it's editing or creating
        binding.titleTextView.text = if (isEditing) {
            "Edit Recipe"
        } else {
            "New Recipe"
        }

        // Pre-fill the fields with the recipe data if editing
        binding.titleTextField.editText?.setText(recipe.title)
        binding.ingredientsTextField.editText?.setText(recipe.ingredients)
        binding.instructionsTextField.editText?.setText(recipe.instructions)
    }

    private fun validateRecipe(): Boolean {
        val title = recipe.title.trim()

        // Validate that the title is not empty or too long
        if (title.isEmpty()) {
            binding.titleTextField.error = "Please write something"
            return false
        }
        if (title.length > 50) {
            binding.titleTextField.error = "Surpassed char limit"
            return false
        }
        binding.titleTextField.error = null  // Clear any previous error
        return true
    }

    private fun saveRecipe() {
        // Save the updated recipe from the input fields
        recipe.title = binding.titleTextField.editText?.text.toString()
        recipe.ingredients = binding.ingredientsTextField.editText?.text.toString()
        recipe.instructions = binding.instructionsTextField.editText?.text.toString()

        if (validateRecipe()) {
            if (recipe.id != -1L) {
                // Update the existing recipe
                recipeDAO.update(recipe)
            } else {
                // Insert a new recipe
                recipeDAO.insert(recipe)
            }

            // Return the result to the previous activity
            val resultIntent = Intent().apply {
                putExtra("RECIPE", recipe)  // Pass the updated recipe back
            }
            setResult(RESULT_OK, resultIntent)  // Set the result back to NewRecipeDetailActivity
            finish()  // Close the activity and return to the previous one
        }
    }

}
