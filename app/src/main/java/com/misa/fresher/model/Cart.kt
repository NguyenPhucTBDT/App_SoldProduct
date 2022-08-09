package com.misa.fresher.model

class Cart(
    val idU: Int,
    val address: String,
    val phone: String,
    val amount: Float,
    val lstInvoiceDetail: List<InvoiceDetail>
) {
}