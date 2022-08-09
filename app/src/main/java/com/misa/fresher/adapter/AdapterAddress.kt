package com.misa.fresher.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.misa.fresher.R
import com.misa.fresher.base.BaseAdapter
import com.misa.fresher.base.BaseViewHolder
import com.misa.fresher.model.AddressUser
import com.misa.fresher.model.Vegetable
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class AdapterAddress(
    override var items: ArrayList<AddressUser>,
    override var clickItemListener: (AddressUser) -> Unit
) : BaseAdapter<AddressUser, AdapterAddress.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_address_layout, parent, false),
        clickItemListener
    )

    class ViewHolder(itemView: View, override var clickItemListener: (AddressUser) -> Unit) :
        BaseViewHolder<AddressUser>(itemView) {
        override fun bindData(item: AddressUser) {
            super.bindData(item)
            itemView.apply {
                 itemView.findViewById<TextView>(R.id.tv_phone).text = item.phone
                itemView.findViewById<TextView>(R.id.tv_address).text = item.address
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int = items.size
}