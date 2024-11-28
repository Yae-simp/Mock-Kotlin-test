package com.example.mocktest.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocktest.databinding.ItemNewRecipeBinding
import com.example.mocktest.data.NewRecipe

class NewRecipeAdapter(
    private var items: List<NewRecipe>,
    val onItemClick: (Int) -> Unit,
    val onItemDelete: (Int) -> Unit
) : RecyclerView.Adapter<NewRecipeViewHolder>() {

    override fun onBindViewHolder(holder: NewRecipeViewHolder, position: Int) {
        val recipe = items[position]
        holder.render(recipe)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.binding.deleteButton.setOnClickListener {
            onItemDelete(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewRecipeViewHolder {
        val binding = ItemNewRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewRecipeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems (newItems: List<NewRecipe>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

class NewRecipeViewHolder (val binding: ItemNewRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(recipe: NewRecipe) {
        binding.newRecipeNameTV.text = recipe.title
    }
}