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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recipeDAO = RecipeDAO(this)

        //Gets recipeId from Intent
        recipeId = intent.getLongExtra(EXTRA_RECIPE_ID, -1L)

        //Checks if recipeId is valid for editing
        if (recipeId != -1L) {
            isEditing = true
            //Retrieves recipe from database
            recipe = recipeDAO.findById(recipeId) ?: NewRecipe(-1, "", "", "")
        } else {
            isEditing = false
            //Creates a new recipe if no valid ID is passed
            recipe = NewRecipe(-1, "", "", "")
        }
        loadViews()
        loadData()
    }

    private fun loadViews() {
        //closeButton to finish activity
        binding.closeButton.setOnClickListener { finish() }

        //saveButton to either save or update recipe
        binding.saveButton.setOnClickListener {
            if (validateRecipe()) {
                saveRecipe()
            }
        }

        //TextWatcher for updating recipe title (titleTextField) dynamically
        //TextWatcher is a tool that watches what is typed in a text box.
        binding.titleTextField.editText?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                recipe.title = s.toString()  //Updates title as the user types
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadData() {
        //Updates the title based on whether it's editing or creating.
        binding.titleTextView.text = if (isEditing) {
            "Edit recipe"
        } else {
            "New recipe"
        }

        //Pre-fills the fields with the recipe data if editing.
        binding.titleTextField.editText?.setText(recipe.title)
        binding.ingredientsTextField.editText?.setText(recipe.ingredients)
        binding.instructionsTextField.editText?.setText(recipe.instructions)
    }

    private fun validateRecipe(): Boolean {
        val title = recipe.title.trim()

        //Validates that the title is not empty or too long.
        if (title.isEmpty()) {
            binding.titleTextField.error = "Please write something"
            return false
        }
        if (title.length > 50) {
            binding.titleTextField.error = "Surpassed char limit"
            return false
        }
        binding.titleTextField.error = null  //Clears any previous error.
        return true
    }

    private fun saveRecipe() {
        //Saves the updated recipe from the input fields
        recipe.title = binding.titleTextField.editText?.text.toString()
        recipe.ingredients = binding.ingredientsTextField.editText?.text.toString()
        recipe.instructions = binding.instructionsTextField.editText?.text.toString()

        if (validateRecipe()) {
            if (recipe.id != -1L) {
                //Updates the existing recipe.
                recipeDAO.update(recipe)
            } else {
                //Inserts a new recipe.
                recipeDAO.insert(recipe)
            }

            //Returns the result to previous activity.
            val resultIntent = Intent().apply {
                putExtra("RECIPE", recipe)  //Passes updated recipe back
            }
            setResult(RESULT_OK, resultIntent)  //Sets result back to NewRecipeDetailActivity
            finish()
        }
    }
}
