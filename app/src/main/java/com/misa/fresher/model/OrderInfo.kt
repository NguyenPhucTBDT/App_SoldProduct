package com.misa.fresher.model

class OrderInfo(
    val idU: Int,
    val address_receiver: String,
    val phone_receiver: String,
    val name_receiver : String,
    val amount: Float,
    val payment_method: Int,
    val note: String,
    val time_order: String,
    val status: Int,
    val list: List<OrderDetail>
) {
}