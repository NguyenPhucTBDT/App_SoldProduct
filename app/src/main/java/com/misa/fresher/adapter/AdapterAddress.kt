package com.misa.fresher.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.misa.fresher.R
import com.misa.fresher.base.BaseAdapter
import com.misa.fresher.base.BaseViewHolder
import com.misa.fresher.model.Address

class AdapterAddress(
    override var items: ArrayList<Address>,
    override var clickItemListener: (Address) -> Unit
) : BaseAdapter<Address, AdapterAddress.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_address_layout, parent, false),
        clickItemListener
    )

    class ViewHolder(itemView: View, override var clickItemListener: (Address) -> Unit) :
        BaseViewHolder<Address>(itemView) {
        @SuppressLint("SetTextI18n")
        override fun bindData(item: Address) {
            super.bindData(item)
            itemView.apply {
                itemView.findViewById<TextView>(R.id.tv_receiver_info).text = item.name + " | " + item.phone
                itemView.findViewById<TextView>(R.id.tv_address).text = item.address
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int = items.size
}