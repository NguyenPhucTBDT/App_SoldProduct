package com.misa.fresher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misa.fresher.databinding.CartItemLayoutBinding
import com.misa.fresher.model.InvoiceDetail
import com.misa.fresher.model.ShoppingCart
import java.text.DecimalFormat

class AdapterOderDetail(
    private val mListVet: ArrayList<InvoiceDetail>
) : RecyclerView.Adapter<AdapterOderDetail.ViewHolder>() {
    inner class ViewHolder(binding: CartItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val quantity = binding.tvQuantity
        private val price = binding.tvPrice
        private val decimalFormat = DecimalFormat("0,000.0")
        fun bind(invoiceDetail: InvoiceDetail) {
            title.text = invoiceDetail.title
            quantity.text = invoiceDetail.quantity.toString()
            price.text = decimalFormat.format(invoiceDetail.price) + " VNĐ"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterOderDetail.ViewHolder {
        val binding: CartItemLayoutBinding by lazy {
            CartItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        }
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = mListVet.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mListVet[position])
    }
}