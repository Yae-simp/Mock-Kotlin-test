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
import com.example.mocktest.adapter.RecipeAdapter
import com.example.mocktest.data.Recipe
import com.example.mocktest.databinding.ActivityMainBinding
import com.example.mocktest.retrofit.RetrofitProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecipeAdapter
    private var recipeList: List<Recipe> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //binding.newRecipeNameTVinMainAct.visibility = View.GONE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = RecipeAdapter(recipeList) { position ->
            val recipe = recipeList[position]
            navigateToDetail(recipe)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        searchRecipe()
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_my_recipes -> {
                    startActivity(Intent(this, ListOfCreatedRecipesActivity::class.java))
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
        adapter.updateItems(recipeList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == -1 && resultCode == RESULT_OK) {
            val recipeSaved = data?.getBooleanExtra("RECIPE_SAVED", false) ?: false
            if (recipeSaved) {
                // Show confirmation in MainActivity
                //binding.newRecipeNameTVinNewRecipe.visibility = View.VISIBLE

                // Hide the confirmation message after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    //binding.newRecipeNameTVinNewRecipe.visibility = View.GONE
                }, 2000)
            }
        }
    }


    private fun navigateToDetail(recipe: Recipe) {
        val intent = Intent(this, DetailActivity::class.java)
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
                adapter.updateItems(filteredList)
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
                    adapter.updateItems(recipeList)
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