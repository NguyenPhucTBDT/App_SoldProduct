package com.misa.fresher.model

class Cart(
    val idU: Int,
    val product_id: Int,
    val title: String,
    val quantity: Int,
    val imglink: String,
    val price: Float,
    val sale_price: Float
) {
}