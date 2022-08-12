package com.misa.fresher.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.misa.fresher.R
import com.misa.fresher.base.BaseAdapter
import com.misa.fresher.base.BaseViewHolder
import com.misa.fresher.model.Product
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class AdapterProduct(
    override var items: ArrayList<Product>,
    override var clickItemListener: (Product) -> Unit
) : BaseAdapter<Product, AdapterProduct.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.product_item_layout, parent, false),
        clickItemListener
    )

    class ViewHolder(itemView: View, override var clickItemListener: (Product) -> Unit) :
        BaseViewHolder<Product>(itemView) {
        override fun bindData(item: Product) {
            super.bindData(item)
            itemView.apply {
                Picasso.get().load(item.imglink).resize(150, 150)
                    .into(this.findViewById<ImageView>(R.id.iv_image))
                this.findViewById<TextView>(R.id.tv_name_cate).text = item.product_name
                val decimalFormat = DecimalFormat("0,000")
                if (item.sale_price > 0) {
                    this.findViewById<TextView>(R.id.tv_sale_price).apply {
                        this.text = decimalFormat.format(item.sale_price).toString() + " ₫"
                    }
                    this.findViewById<TextView>(R.id.tv_price).apply {
                        this.text = decimalFormat.format(item.price).toString() + " ₫"
                        this.setTextColor(resources.getColor(R.color.color_SKU))
                        this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                } else {
                    this.findViewById<TextView>(R.id.tv_sale_price).visibility = View.GONE
                    this.findViewById<TextView>(R.id.tv_price).text =
                        decimalFormat.format(item.price).toString() + " ₫"
                }

            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int = items.size
}