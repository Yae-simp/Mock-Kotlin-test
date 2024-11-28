package com.example.mocktest.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mocktest.databinding.ActivityNewRecipeBinding
import com.example.mocktest.data.NewRecipe
import com.example.mocktest.sqlite.RecipeDAO


class CreateRecipeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }

    private lateinit var binding: ActivityNewRecipeBinding
    private var isEditing: Boolean = false
    private lateinit var recipeDAO: RecipeDAO
    private lateinit var recipe: NewRecipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recipeDAO = RecipeDAO(this)

        val id = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        recipe = if (id != -1L) {
            isEditing = true
            recipeDAO.findById(id)!!
        } else {
            isEditing = false
            NewRecipe(-1, "", "", "")
        }

        loadViews()
        loadData()
    }

    private fun loadViews() {
        binding.closeButton.setOnClickListener { finish() }

        binding.saveButton.setOnClickListener {
            if (validateRecipe()) {
                saveRecipe()
            }
        }

        // Update recipe title as the user types in the EditText
        binding.titleTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Update the recipe title when text changes
                recipe.title = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadData() {
        binding.titleTextView.text = if (isEditing) {
            "Edit recipe"
        } else {
            "New recipe"
        }

        binding.titleTextField.editText?.setText(recipe.title)
        binding.ingredientsTextField.editText?.setText(recipe.ingredients)
        binding.instructionsTextField.editText?.setText(recipe.instructions)
    }

    private fun validateRecipe(): Boolean {
        val title = recipe.title.trim()

        if (title.isEmpty()) {
            binding.titleTextField.error = "Please write something"
            return false
        }
        // Check if title exceeds 50 characters
        if (title.length > 50) {
            binding.titleTextField.error = "Surpassed char limit"
            return false
        }
        // Clear any previous error if validation passed
        binding.titleTextField.error = null

        return true
    }

    private fun saveRecipe() {
        recipe.title = binding.titleTextField.editText?.text.toString()
        recipe.ingredients = binding.ingredientsTextField.editText?.text.toString()
        recipe.instructions = binding.instructionsTextField.editText?.text.toString()

        if (validateRecipe()) {
            if (recipe.id != -1L) {
                recipeDAO.update(recipe)
            } else {
                recipeDAO.insert(recipe)
            }
            val resultIntent = Intent().apply {
                putExtra("RECIPE", recipe)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}