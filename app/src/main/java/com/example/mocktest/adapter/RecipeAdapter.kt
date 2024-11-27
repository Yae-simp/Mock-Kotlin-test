package com.example.mocktest.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocktest.data.Recipe
import com.example.mocktest.databinding.ItemRecipeBinding
import com.example.mocktest.sessionmanager.SessionManager
import com.squareup.picasso.Picasso

class RecipeAdapter (private var items: List<Recipe>, val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<RecipeViewHolder>() {
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = items[position]
        holder.render(recipe)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<Recipe>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class RecipeViewHolder(internal val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(recipe: Recipe) {
        binding.recipeNameTextView.text = recipe.name
        Picasso.get().load(recipe.image).into(binding.recipeImageView)

        val sessionManager = SessionManager(itemView.context)
        if (sessionManager.isFavorite(recipe.id)) {
            binding.favoriteImageView.visibility = View.VISIBLE
        } else {
            binding.favoriteImageView.visibility = View.GONE
        }
    }
}
