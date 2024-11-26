package com.example.mocktest.activities

import android.annotation.SuppressLint
import android.content.Intent
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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.mocktest.sessionmanager.SessionManager

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RECIPE_ID = "RECIPE_ID"
    }

    private lateinit var binding: ActivityDetailBinding

    private lateinit var recipe: Recipe
    //Whether horoscope is favorite or not
    private var isFav = false

    //Fav menu option for changing the fav icon
    private lateinit var favMenuItem: MenuItem

    //Session manager, saves fav horoscope
    private lateinit var session: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //Instantiates session object
        session = com.example.mocktest.sessionmanager.SessionManager(this)

        //Revises whether horoscope is fav or not
        isFav = session.isFavorite(recipe.id.toString())

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navigationBar.itemIconTintList = null
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
            R.id.fav_menu -> {
                println("Pressed 'favorite' button.")
                if (isFav) {
                    session.setFavorite("")
                } else {
                    session.setFavorite(recipe.id.toString())
                }
                isFav = !isFav
                setFavoriteIcon()
                return true
            }
            R.id.share_menu -> {
                println("Pressed 'share' button.")
                shareDaily()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun shareDaily() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "urmom is gae")
        sendIntent.type = "text/plain"

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun setFavoriteIcon() {
        if(isFav) {
            favMenuItem.setIcon(R.drawable.ic_selected_fav)
        } else {
            favMenuItem.setIcon(R.drawable.ic_fav)
        }
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
        Picasso.get().load(recipe.image).into(binding.recipeImageView)

        //Ingredients
        binding.ingredientsContent.ingredientsTextView.text = recipe.ingredients.joinToString("\n")

        //Instructions
        binding.instructionsContent.instructionsTextView.text = recipe.instructions.joinToString("\n")

        binding.miscContent.prepTimeMinutesTextView.text = recipe.prepTimeMinutes.toString()
        binding.miscContent.cookTimeMinutesTextView.text = recipe.cookTimeMinutes.toString()
        binding.miscContent.servingsTV.text = recipe.servings.toString()
        binding.miscContent.difficultyTV.text = recipe.difficulty
        binding.miscContent.cuisineTV.text = recipe.cuisine
        binding.miscContent.caloriesPerServingTV.text = recipe.caloriesPerServing.toString()
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