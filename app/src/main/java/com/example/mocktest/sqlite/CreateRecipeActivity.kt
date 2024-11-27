package com.example.mocktest.sqlite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mocktest.databinding.ActivityNewRecipeBinding


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
            NewRecipe(-1, "")
        }

        loadViews()
        loadData()
    }

    private fun loadViews() {
        binding.closeButton.setOnClickListener { finish() }

        binding.saveButton.setOnClickListener {
            if (validateRecipe()) {
                saveTask()
            }
        }
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
        if (recipe.title.trim().isEmpty()) {
            binding.titleTextField.error = "Please write something"
            return false
        } else {
            binding.titleTextField.error = null
        }
        if (recipe.title.length > 50) {
            binding.titleTextField.error = "Surpassed char limit"
            return false
        } else {
            binding.titleTextField.error = null
        }
        return true
    }

    private fun saveTask() {
        recipe.title = binding.titleTextField.editText?.text.toString()
        recipe.ingredients = binding.ingredientsTextField.editText?.text.toString()
        recipe.instructions = binding.instructionsTextField.editText?.text.toString()

        if (validateRecipe()) {
            if (recipe.id != -1L) {
                recipeDAO.update(recipe)
            } else {
                recipeDAO.insert(recipe)
            }

            finish()
        }
    }
}