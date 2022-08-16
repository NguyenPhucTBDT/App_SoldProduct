package com.misa.fresher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misa.fresher.databinding.CartItemLayoutBinding
import com.misa.fresher.model.Cart
import com.misa.fresher.model.ShoppingCart
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class AdapterShoppingCart(
    private val mListShoppingCart: ArrayList<Cart>, val mCtx: Context,
    val updateView: (product: Cart) -> Unit
) : RecyclerView.Adapter<AdapterShoppingCart.ViewHolder>() {
    inner class ViewHolder(binding: CartItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val quantity = binding.tvQuantity
        private val price = binding.tvPrice
        private val img = binding.imgProduct
        private val tvDelete = binding.tvDelete
        private val decimalFormat = DecimalFormat("0,000.0")
        fun bind(product: Cart) {
            Picasso.get().load(product.imglink).into(img)
            title.text = product.title
            quantity.text = product.quantity.toString()
            if (product.sale_price > 0) {
                price.text = decimalFormat.format(product.sale_price) + " ₫"
            } else {
                price.text = decimalFormat.format(product.price) + " ₫"
            }
            tvDelete.setOnClickListener { updateView(product) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterShoppingCart.ViewHolder {
        val binding: CartItemLayoutBinding by lazy {
            CartItemLayoutBinding.inflate(
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