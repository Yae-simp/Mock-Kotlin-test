package com.example.mocktest.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocktest.data.Recipe
import com.example.mocktest.databinding.ItemRecipeBinding
import com.example.mocktest.sqlite.NewRecipe
import com.squareup.picasso.Picasso

class RecipeAdapter (private var items: List<Recipe>, val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = items[position]
        holder.render(recipe)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
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

class ViewHolder(internal val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(recipe: NewRecipe) {
        binding.recipeNameTextView.text = recipe.name
        Picasso.get().load(recipe.image).into(binding.recipeImageView)
    }
}
