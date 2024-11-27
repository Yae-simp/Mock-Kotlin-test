package com.example.mocktest.sqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocktest.adapter.ViewHolder
import com.example.mocktest.databinding.ItemNewRecipeBinding

class NewRecipeAdapter (
    private var items: List<NewRecipe>,
    val onItemClick: (Int) -> Unit,
    val onItemDelete: (Int) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = items[position]
        holder.render(recipe)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.binding.deleteButton.setOnClickListener {
            onItemDelete(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.example.mocktest.sqlite.ViewHolder {
        val binding = ItemNewRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<NewRecipe>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class ViewHolder (private val binding: ItemNewRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(recipe: NewRecipe) {
        binding.newRecipeNameTV.text = recipe.title
    }
}