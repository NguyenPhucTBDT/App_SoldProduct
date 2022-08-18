package com.misa.fresher.model

class OrderDetail(
    val idO: Int,
    val product_id: Int,
    val title: String,
    val quantity: Int,
    val price: Float,
    val sale_price: Float,
    val imglink: String
) {
}