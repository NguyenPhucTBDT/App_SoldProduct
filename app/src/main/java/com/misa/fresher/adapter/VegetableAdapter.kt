package com.misa.fresher.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.misa.fresher.R
import com.misa.fresher.base.BaseAdapter
import com.misa.fresher.base.BaseViewHolder
import com.misa.fresher.model.Vegetable
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class VegetableAdapter(
    override var items: ArrayList<Vegetable>,
    override var clickItemListener: (Vegetable) -> Unit
) : BaseAdapter<Vegetable, VegetableAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.product_item_layout, parent, false),
        clickItemListener
    )

    class ViewHolder(itemView: View, override var clickItemListener: (Vegetable) -> Unit) :
        BaseViewHolder<Vegetable>(itemView) {
        override fun bindData(item: Vegetable) {
            super.bindData(item)
            itemView.apply {
                Picasso.get().load(item.imgLink).resize(150,150).into( this.findViewById<ImageView>(R.id.iv_image))
                this.findViewById<TextView>(R.id.tv_name_cate).text = item.nameVegetable
                this.findViewById<TextView>(R.id.tv_manufacture).text = item.manufacture
                val decimalFormat = DecimalFormat("0,000.00")
                this.findViewById<TextView>(R.id.tv_price).text =
                    decimalFormat.format(item.price).toString() + " VNĐ"
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int = items.size
}