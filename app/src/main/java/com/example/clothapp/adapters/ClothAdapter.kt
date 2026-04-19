package com.example.clothapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothapp.databinding.ItemClothBinding
import com.example.clothapp.models.Cloth

class ClothAdapter(
    private val clothList: List<Cloth>,
    private val onAddToCart: (Cloth) -> Unit
) : RecyclerView.Adapter<ClothAdapter.ClothViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothViewHolder {
        val binding = ItemClothBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClothViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothViewHolder, position: Int) {
        holder.bind(clothList[position])
    }

    override fun getItemCount() = clothList.size

    inner class ClothViewHolder(private val binding: ItemClothBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cloth: Cloth) {
            binding.apply {
                clothName.text = cloth.name
                clothPrice.text = "₹${cloth.price}"
                clothCategory.text = cloth.category

                Glide.with(root.context)
                    .load(cloth.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(clothImage)

                addToCartBtn.setOnClickListener {
                    onAddToCart(cloth)
                }
            }
        }
    }
}