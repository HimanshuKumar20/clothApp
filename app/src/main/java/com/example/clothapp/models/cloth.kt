package com.example.clothapp.models

data class Cloth(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val category: String,
    val size: List<String>,
    val color: List<String>
)

data class CartItem(
    val cloth: Cloth,
    var quantity: Int,
    var selectedSize: String,
    var selectedColor: String
)