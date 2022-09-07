package com.misa.fresher.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misa.fresher.databinding.OderItemLayoutBinding
import com.misa.fresher.model.Cart
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class AdapterOder(
    private val mListShoppingCart: ArrayList<Cart>
) : RecyclerView.Adapter<AdapterOder.ViewHolder>() {
    inner class ViewHolder(binding: OderItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvProductName
        private val quantity = binding.tvQuantity
        private val price = binding.tvPrice
        private val img = binding.imgProduct
        private val decimalFormat = DecimalFormat("0,000.0")

        @SuppressLint("SetTextI18n")
        fun bind(product: Cart) {
            Picasso.get().load(product.imglink).into(img)
            title.text = product.title
            quantity.text = "SL: ${product.quantity}"
            price.text = "Giá:" + decimalFormat.format(product.price) + " ₫"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterOder.ViewHolder {
        val binding: OderItemLayoutBinding by lazy {
            OderItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        }
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = mListShoppingCart.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mListShoppingCart[position])
    }
}