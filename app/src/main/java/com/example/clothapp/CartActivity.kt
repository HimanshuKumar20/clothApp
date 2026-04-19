package com.example.clothapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothapp.adapters.CartAdapter
import com.example.clothapp.databinding.ActivityCartBinding
import com.example.clothapp.models.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private var cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadCartItems()
        setupRecyclerView()
        setupCheckoutButton()
        updateTotalAmount()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.cartToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.cartToolbar.setNavigationOnClickListener {
            finish()  // Go back to previous screen
        }
    }

    private fun loadCartItems() {
        val sharedPref = getSharedPreferences("cloth_app", MODE_PRIVATE)
        val json = sharedPref.getString("cart", "[]")
        val gson = Gson()
        val type = object : TypeToken<MutableList<CartItem>>() {}.type
        cartItems = gson.fromJson(json, type)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChange = { item, newQuantity ->
                item.quantity = newQuantity
                saveCart()
                cartAdapter.updateItems(cartItems)
                updateTotalAmount()
            },
            onRemove = { item ->
                cartItems.remove(item)
                saveCart()
                cartAdapter.updateItems(cartItems)
                updateTotalAmount()
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
            }
        )

        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun updateTotalAmount() {
        val total = cartItems.sumOf { it.cloth.price * it.quantity }
        binding.totalAmount.text = "Total: ₹$total"
    }

    private fun setupCheckoutButton() {
        binding.checkoutBtn.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                clearCart()
            }
        }
    }

    private fun saveCart() {
        val sharedPref = getSharedPreferences("cloth_app", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        val json = gson.toJson(cartItems)
        editor.putString("cart", json)
        editor.apply()
    }

    private fun clearCart() {
        cartItems.clear()
        saveCart()
        cartAdapter.updateItems(cartItems)
        updateTotalAmount()
        Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
        finish()  // Close cart activity and go back
    }
}