package com.example.mocktest.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mocktest.R
import com.example.mocktest.data.Recipe
import com.example.mocktest.databinding.ActivityDetailBinding
import com.example.mocktest.retrofit.RetrofitProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RECIPE_ID = "RECIPE_ID"
    }

    private lateinit var binding: ActivityDetailBinding

    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navigationBar.setOnItemSelectedListener {
            setSelectedTab(it.itemId)
        }

        binding.navigationBar.selectedItemId = R.id.menu_ingredients

        val id = intent.getIntExtra(EXTRA_RECIPE_ID, -1)

        getRecipe(id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setSelectedTab(itemId: Int) : Boolean {
        when (itemId) {
            R.id.menu_ingredients -> {
                binding.ingredientsContent.root.visibility = View.VISIBLE
                binding.instructionsContent.root.visibility = View.GONE
                binding.miscContent.root.visibility = View.GONE
            }
            R.id.menu_instructions -> {
                binding.ingredientsContent.root.visibility = View.GONE
                binding.instructionsContent.root.visibility = View.VISIBLE
                binding.miscContent.root.visibility = View.GONE
            }
            R.id.menu_misc -> {
                binding.ingredientsContent.root.visibility = View.GONE
                binding.instructionsContent.root.visibility = View.GONE
                binding.miscContent.root.visibility = View.VISIBLE
            }
        }

        return true
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        supportActionBar?.title = recipe.name

        //Ingredients
        binding.ingredientsContent.ingredientsTextView.text = recipe.ingredients.joinToString("\n")

        //Instructions
        binding.instructionsContent.instructionsTextView.text = recipe.instructions.joinToString("\n")

        if (recipe?.misc != null) {
            binding.miscContent.contentTextView.text = recipe.misc.prepTimeMinutes.toString()
        } else {
            // Handle the case where recipe or recipe.misc is null
            Log.e("NullPointer", "Recipe or recipe.misc is null!")
        }
        //Misc
        binding.miscContent.prepTimeMinutesTextView.text = recipe.misc.prepTimeMinutes.toString()
        binding.miscContent.cookTimeMinutesTextView.text = recipe.misc.cookTimeMinutes.toString()
        binding.miscContent.servingsTV.text = recipe.misc.servings.toString()
        binding.miscContent.difficultyTV.text = recipe.misc.difficulty
        binding.miscContent.cuisineTV.text = recipe.misc.cuisine
        binding.miscContent.caloriesPerServingTV.text = recipe.misc.caloriesPerServing.toString()
    }

    private fun getRecipe(id: Int) {
        val service = RetrofitProvider.getRetrofit()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                recipe = service.getRecipe(id)

                CoroutineScope(Dispatchers.Main).launch {
                    loadData()
                }
            } catch (e: Exception) {
                Log.e("API", e.stackTraceToString())
            }
        }
    }
}