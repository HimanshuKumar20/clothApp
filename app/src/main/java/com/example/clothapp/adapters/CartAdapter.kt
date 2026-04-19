package com.example.clothapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothapp.databinding.ItemCartBinding
import com.example.clothapp.models.CartItem

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount() = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.apply {
                cartItemName.text = item.cloth.name
                cartItemPrice.text = "₹${item.cloth.price * item.quantity}"
                cartItemDetails.text = "Size: ${item.selectedSize}, Color: ${item.selectedColor}"
                quantityText.text = item.quantity.toString()

                increaseBtn.setOnClickListener {
                    onQuantityChange(item, item.quantity + 1)
                }

                decreaseBtn.setOnClickListener {
                    if (item.quantity > 1) {
                        onQuantityChange(item, item.quantity - 1)
                    }
                }

                removeBtn.setOnClickListener {
                    onRemove(item)
                }
            }
        }
    }
}