package com.example.clothapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clothapp.adapters.ClothAdapter
import com.example.clothapp.databinding.ActivityMainBinding
import com.example.clothapp.models.CartItem
import com.example.clothapp.models.Cloth
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var clothAdapter: ClothAdapter
    private val clothList = mutableListOf<Cloth>()
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadClothData()
        setupNavigationDrawer()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }
    }

    private fun setupRecyclerView() {
        clothAdapter = ClothAdapter(clothList) { cloth ->
            addToCart(cloth)
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = clothAdapter
        }
    }

    private fun loadClothData() {
        // Sample data - replace with API call or local database
        clothList.addAll(listOf(
            Cloth(1, "Cotton T-Shirt", 499.0, "Soft cotton t-shirt",
                "https://example.com/tshirt.jpg", "Men", listOf("S", "M", "L", "XL"),
                listOf("Red", "Blue", "Black")),
            Cloth(2, "Denim Jeans", 1299.0, "Classic denim jeans",
                "https://example.com/jeans.jpg", "Men", listOf("30", "32", "34", "36"),
                listOf("Blue", "Black")),
            Cloth(3, "Summer Dress", 899.0, "Floral summer dress",
                "https://example.com/dress.jpg", "Women", listOf("XS", "S", "M", "L"),
                listOf("Pink", "Yellow", "White")),
            Cloth(4, "Winter Jacket", 2499.0, "Warm winter jacket",
                "https://example.com/jacket.jpg", "Men", listOf("M", "L", "XL", "XXL"),
                listOf("Black", "Navy", "Brown")),
            Cloth(5, "Sports Shoes", 1999.0, "Comfortable sports shoes",
                "https://example.com/shoes.jpg", "Footwear", listOf("7", "8", "9", "10"),
                listOf("White", "Black", "Gray")),
            Cloth(6, "Formal Shirt", 999.0, "Premium formal shirt",
                "https://example.com/shirt.jpg", "Men", listOf("S", "M", "L", "XL"),
                listOf("White", "Light Blue", "Gray"))
        ))
        clothAdapter.notifyDataSetChanged()
    }

    private fun addToCart(cloth: Cloth) {
        // Show size/color selection dialog
        showSizeColorDialog(cloth)
    }

    private fun showSizeColorDialog(cloth: Cloth) {
        val sizes = cloth.size
        val colors = cloth.color

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Options for ${cloth.name}")

        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val spinnerSize = Spinner(this).apply {
            adapter = sizeAdapter
        }

        val spinnerColor = Spinner(this).apply {
            adapter = colorAdapter
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)

            addView(TextView(context).apply {
                text = "Select Size:"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            })
            addView(spinnerSize)

            addView(TextView(context).apply {
                text = "Select Color:"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = 20 }
            })
            addView(spinnerColor)
        }

        builder.setView(layout)
        builder.setPositiveButton("Add to Cart") { _, _ ->
            val selectedSize = sizes[spinnerSize.selectedItemPosition]
            val selectedColor = colors[spinnerColor.selectedItemPosition]

            val existingItem = cartItems.find {
                it.cloth.id == cloth.id &&
                        it.selectedSize == selectedSize &&
                        it.selectedColor == selectedColor
            }

            if (existingItem != null) {
                existingItem.quantity++
            } else {
                cartItems.add(CartItem(cloth, 1, selectedSize, selectedColor))
            }

            Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show()
            saveCartToPreferences()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Already on home
                }
                R.id.nav_cart -> {
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_orders -> {
                    Toast.makeText(this, "Orders feature coming soon", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_contact -> {
                    Toast.makeText(this, "Contact: support@clothstore.com", Toast.LENGTH_SHORT).show()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun saveCartToPreferences() {
        val sharedPref = getSharedPreferences("cloth_app", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        val json = gson.toJson(cartItems)
        editor.putString("cart", json)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        loadCartFromPreferences()
    }

    private fun loadCartFromPreferences() {
        val sharedPref = getSharedPreferences("cloth_app", MODE_PRIVATE)
        val json = sharedPref.getString("cart", "[]")
        val gson = Gson()
        val type = object : TypeToken<MutableList<CartItem>>() {}.type
        cartItems.clear()
        cartItems.addAll(gson.fromJson(json, type))
    }
}