package com.example.mocktest.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocktest.databinding.ItemNewRecipeBinding
import com.example.mocktest.data.NewRecipe

//Adapter class for displaying a list of NewRecipe objects in a RecyclerView.
class NewRecipeAdapter(
    private var items: List<NewRecipe>,        //List of recipes to be displayed.
    val onItemClick: (Int) -> Unit,            //Lambda function for handling item clicks, passing the position.
    val onItemDelete: (Int) -> Unit            //Lambda function for handling item delete action, passing the position.
) : RecyclerView.Adapter<NewRecipeViewHolder>() {  //Inherit from RecyclerView.Adapter to bind data to the view.


    //Binds data for a single item (i.e., a recipe) to the view.
    override fun onBindViewHolder(holder: NewRecipeViewHolder, position: Int) {
        //Get the recipe at the current position from the items list.
        val recipe = items[position]
        //Use the ViewHolder's render method to populate the view with the recipe data.
        holder.render(recipe)

        // Set an OnClickListener on the entire item (row).
        holder.itemView.setOnClickListener {
            // When the item is clicked, call the onItemClick function with the position of the clicked item.
            onItemClick(position)
        }
        // Set an OnClickListener on the delete button inside the item.
        holder.binding.deleteButton.setOnClickListener {
            // When the delete button is clicked, call the onItemDelete function with the position of the item to delete.
            onItemDelete(position)
        }
    }

    //Creates a new ViewHolder (which holds references to the view elements of each item).
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewRecipeViewHolder {
        //Inflate item layout using the LayoutInflater to create the view for each item.
        val binding = ItemNewRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //Returns a new ViewHolder that wraps the inflated layout (binding).
        return NewRecipeViewHolder(binding)
    }

    //Returns the total number of items in the adapter (size of the recipe list).
    override fun getItemCount(): Int {
        return items.size
    }

    //Updates items list in adapter with a new list of recipes.
    fun updateItems (newItems: List<NewRecipe>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
//ViewHolder class for holding references to the views inside a single item layout.
class NewRecipeViewHolder (val binding: ItemNewRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

    //Populates the views in the item layout with data.
    fun render(recipe: NewRecipe) {
        binding.newRecipeNameTV.text = recipe.title
    }
}