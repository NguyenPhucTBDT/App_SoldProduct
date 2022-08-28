package com.misa.fresher.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misa.fresher.databinding.CartItemLayoutBinding
import com.misa.fresher.databinding.OderItemLayoutBinding
import com.misa.fresher.model.InvoiceDetail
import com.misa.fresher.model.OrderDetail
import com.misa.fresher.model.ShoppingCart
import com.squareup.picasso.Picasso
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class AdapterOderDetail(
    private val mListVet: ArrayList<OrderDetail>
) : RecyclerView.Adapter<AdapterOderDetail.ViewHolder>() {
    inner class ViewHolder(binding: OderItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val img = binding.imgProduct
        private val title = binding.tvProductName
        private val quantity = binding.tvQuantity
        private val price = binding.tvPrice
        private val decimalFormat = DecimalFormat("0,000.0")
        private val space = binding.spacer

        @SuppressLint("SetTextI18n")
        fun bind(invoiceDetail: OrderDetail,gone : Boolean) {
            Picasso.get().load(invoiceDetail.imglink).into(img)
            title.text = invoiceDetail.title
            quantity.text = "Số lượng: ${invoiceDetail.quantity}"
            if (invoiceDetail.sale_price > 0) {
                price.text = decimalFormat.format(invoiceDetail.sale_price) + " ₫"
            } else {
                price.text = decimalFormat.format(invoiceDetail.price) + " ₫"
            }
            if(gone) {
                space.visibility = View.GONE
            }
            else {
                space.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterOderDetail.ViewHolder {
        val binding: OderItemLayoutBinding by lazy {
            OderItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        }
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = mListVet.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == (mListVet.size - 1)) {
            holder.bind(mListVet[position],true)
        }
        else {
            holder.bind(mListVet[position],false)
        }
    }
}