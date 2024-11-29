package com.example.mocktest.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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

    private lateinit var newRecipeAdapter: NewRecipeAdapter
    private lateinit var newRecipeList: MutableList<NewRecipe>
    private lateinit var recipeDAO: RecipeDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inflates layout from XML (binds views to activity).
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializes RecipeDAO to interact with the database.
        recipeDAO = RecipeDAO(this)
        //Loads all recipes from the database and store them in a mutable list.
        newRecipeList = recipeDAO.findAll().toMutableList()
        //Initializes adapter for the new recipe list and handle item clicks.
        newRecipeAdapter = NewRecipeAdapter(newRecipeList, { position ->
            val recipe = newRecipeList[position]
            navigateToDetail(recipe)
        }, { position ->
            onItemClickRemoveListener(position)
        })
        //Initializes adapter for the DummyJSON recipe list" in the home screen.
        recipeAdapter = RecipeAdapter(recipeList) { position ->
            val recipe = recipeList[position]
            navigateToDetail(recipe)
        }
        //Sets recycleView adapter to show the recipe list by default.
        binding.recyclerView.adapter = recipeAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 1) //Grid layout with one column.

        //Starts search function
        searchRecipe()

        //Handles bottom navigation menu clicks (switches between 'home' and 'my recipes')
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
        //AddRecipeButton to open CreateRecipeActivity
        binding.addRecipeButton.setOnClickListener {
            val intent = Intent(this, CreateRecipeActivity::class.java)
            startActivityForResult(intent, -1)
        }
    }
    //Called when the activity is resumed (for example, after returning from another activity)
    override fun onResume() {
        super.onResume()
        refreshLocalRecipeList() //Refreshes the list of new recipes
        recipeAdapter.updateItems(recipeList) //Updates recipe adapter with new data
    }

    //Responsible for handling results returned from another activity.
    //requestCode: integer that identifies which activity I started. -1 = CreateRecipeActivity.
    //resultCode: code indicating whether the activity completed successfully or failed.
    //If everything went well in the called activity, the result code is usually RESULT_OK (a standard constant).
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if result is from CreateRecipeActivity and if recipe was saved
        if (requestCode == -1 && resultCode == RESULT_OK) {
            val recipeSaved = data?.getBooleanExtra("RECIPE_SAVED", false) ?: false
            if (recipeSaved) {
                refreshLocalRecipeList()  //Refreshes list if new recipe was saved
            }
        }
    }

    //Refreshes the list of new recipes from the in-memory database
    private fun refreshLocalRecipeList() {
        newRecipeList = recipeDAO.findAll().toMutableList()  //Fetches latest data from DB
        newRecipeAdapter.updateItems(newRecipeList)  //Notifies adapter of the change
    }

    //Navigate to the detail screen of a recipe
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

    //Creates the options menu (for search functionality)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)  //Inflates menu

        val menuItem = menu?.findItem(R.id.menu_search)!!  //Finds the search menu item
        val searchView = menuItem.actionView as SearchView   //Gets the SearchView widget

        //Handles text changes in the search box
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false  //Not used in this case
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Filters the recipe list based on the query text
                val filteredList = recipeList.filter { it.name.contains(newText ?: "", true) }
                recipeAdapter.updateItems(filteredList)  //Updates adapter with the filtered list
                return true
            }
        })

        return true
    }

    //Fetches recipes from the server
    private fun searchRecipe() {
        binding.loadingProgressBar.visibility = View.VISIBLE
        val service = RetrofitProvider.getRetrofit() //Gets Retrofit instance to make API calls

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //Makes the API call to fetch recipes
                val result = service.getRecipes()

                CoroutineScope(Dispatchers.Main).launch {
                    //Once data is fetched, updates the on the main thread
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    recipeList = result.recipes
                    recipeAdapter.updateItems(recipeList)
                }
            } catch (e: Exception) {
                Log.e("API", e.stackTraceToString())

                CoroutineScope(Dispatchers.Main).launch {
                    //If there's an error, shows the empty view and display an error message
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                    binding.noResultsTextView.text = getString(R.string.error)
                }
            }
        }
    }
    //Handles removing a recipe (created by user) from the list
    private fun onItemClickRemoveListener(position: Int) {
        val newRecipe: NewRecipe = newRecipeList[position]
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        //Sets up dialog for confirming delete action
        dialogBuilder.setTitle(R.string.delete_recipe_title)
        dialogBuilder.setMessage(getString(R.string.delete_recipe_confirm_message, newRecipe.name))
        dialogBuilder.setIcon(R.drawable.ic_delete)
        dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            newRecipeAdapter.notifyItemChanged(position)
            dialog.dismiss()
        }
        //Handles positive button (delete recipe)
        dialogBuilder.setPositiveButton(R.string.delete_recipe_button) { dialog, _ ->
            recipeDAO.delete(newRecipe)  //Remove from database

            val newList = newRecipeList.minus(newRecipe)  //Update in-memory list
            newRecipeAdapter.updateItems(newList)  //Notifies the adapter

            newRecipeList = newList.toMutableList()
            dialog.dismiss()
            Toast.makeText(this, R.string.delete_recipe_success_message, Toast.LENGTH_SHORT).show()
        }
        dialogBuilder.show()
    }
}
