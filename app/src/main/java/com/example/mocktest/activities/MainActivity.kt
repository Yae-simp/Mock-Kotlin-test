package com.example.mocktest.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mocktest.R
import com.example.mocktest.adapter.NewRecipeAdapter
import com.example.mocktest.adapter.RecipeAdapter
import com.example.mocktest.data.NewRecipe
import com.example.mocktest.data.Recipe
import com.example.mocktest.databinding.ActivityMainBinding
import com.example.mocktest.retrofit.RetrofitProvider
import com.example.mocktest.sqlite.RecipeDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private var recipeList: List<Recipe> = emptyList()

    private val allRecipes = mutableListOf<Recipe>()
    private lateinit var activeRecipeList: List<Recipe>

    private lateinit var newRecipeAdapter: NewRecipeAdapter
    private lateinit var newRecipeList: MutableList<NewRecipe>
    private lateinit var recipeDAO: RecipeDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        activeRecipeList = allRecipes

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        recipeDAO = RecipeDAO(this)
        newRecipeList = recipeDAO.findAll().toMutableList()
        newRecipeAdapter = NewRecipeAdapter(newRecipeList, { position ->
            val recipe = newRecipeList[position]
            navigateToDetail(recipe)
        }, {

        })

        recipeAdapter = RecipeAdapter(recipeList) { position ->
            val recipe = recipeList[position]
            navigateToDetail(recipe)
        }
        binding.recyclerView.adapter = recipeAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        searchRecipe()
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    binding.recyclerView.adapter = recipeAdapter
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_my_recipes -> {
                    binding.recyclerView.adapter = newRecipeAdapter
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
        binding.addRecipeButton.setOnClickListener {
            val intent = Intent(this, CreateRecipeActivity::class.java)
            startActivityForResult(intent, -1)
        }
    }

    override fun onResume() {
        super.onResume()
        // No need to update items here unless you want to refresh the entire list from the DB
        if (recipeList.isEmpty()) {
            // Update remote recipe list
            recipeAdapter.updateItems(recipeList)
        }
        if (newRecipeList.isNotEmpty()) {
            // Update local recipe list
            newRecipeAdapter.updateItems(newRecipeList)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == -1 && resultCode == RESULT_OK) {
            val recipeSaved = data?.getBooleanExtra("RECIPE_SAVED", false) ?: false
            if (recipeSaved) {
                // After saving, refresh the local recipe list from the database
                refreshLocalRecipeList()
            }
        }
    }

    private fun refreshLocalRecipeList() {
        newRecipeList = recipeDAO.findAll().toMutableList()  // Fetch updated list from DB
        newRecipeAdapter.updateItems(newRecipeList)  // Update adapter with new data
    }

    private fun navigateToDetail(recipe: Recipe) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, recipe.id)
        startActivity(intent)
    }

    private fun navigateToDetail(recipe: NewRecipe) {
        val intent = Intent(this, NewRecipeDetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, recipe.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        val menuItem = menu?.findItem(R.id.menu_search)!!
        val searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredList = recipeList.filter { it.name.contains(newText, true) }
                recipeAdapter.updateItems(filteredList)
                return true
            }
        })

        return true
    }

    private fun searchRecipe() {
        binding.loadingProgressBar.visibility = View.VISIBLE
        val service = RetrofitProvider.getRetrofit()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = service.getRecipes()

                CoroutineScope(Dispatchers.Main).launch {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    recipeList = result.recipes
                    recipeAdapter.updateItems(recipeList)
                }
            } catch (e: Exception) {
                Log.e("API", e.stackTraceToString())

                CoroutineScope(Dispatchers.Main).launch {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                    binding.noResultsTextView.text = getString(R.string.error)
                }
            }
        }
    }
}