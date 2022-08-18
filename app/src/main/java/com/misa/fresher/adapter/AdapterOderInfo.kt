package com.misa.fresher.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misa.fresher.databinding.ListBillItemLayoutBinding
import com.misa.fresher.model.Order
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class AdapterOderInfo(
    private val mListInvoice: ArrayList<Order>,
    val updateView: (order: Order) -> Unit
) :
    RecyclerView.Adapter<AdapterOderInfo.ViewHolder>() {
    inner class ViewHolder(binding: ListBillItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvBillNum = binding.tvOderId
        private val decimalFormat = DecimalFormat("0,000")
        private val tvBillAmount = binding.tvOrderAmount
        private val tvTime = binding.tvTimeOrder
        private val tvStatus = binding.tvStatusOrder
        private val llListOder = binding.llListOder

        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {
            tvBillNum.text = order.idO.toString()
            tvBillAmount.text = decimalFormat.format(order.amount).toString() + " ₫"
            tvBillAmount.setTextColor(Color.RED)
            tvTime.text = order.time_order
            when (order.status) {
                TYPE_1 -> {
                    tvStatus.apply {
                        this.text = STATUS_1
                        this.setTextColor(Color.RED)
                    }
                }
                TYPE_2 -> {
                    tvStatus.apply {
                        this.text = STATUS_2
                        this.setTextColor(Color.GREEN)
                    }
                }
                TYPE_3 -> {
                    tvStatus.apply {
                        this.text = STATUS_3
                        this.setTextColor(Color.RED)
                    }
                }
                TYPE_4 -> {
                    tvStatus.apply {
                        this.text = STATUS_4
                        this.setTextColor(Color.GREEN)
                    }
                }
                TYPE_5 ->{
                    tvStatus.apply {
                        this.text = STATUS_5
                        this.setTextColor(Color.RED)
                    }
                }
            }
            llListOder.setOnClickListener { updateView(order) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterOderInfo.ViewHolder {
        val binding: ListBillItemLayoutBinding by lazy {
            ListBillItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        }
        return ViewHolder(binding)
    }

    override fun getItemCount() = mListInvoice.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mListInvoice[position])
    }

    companion object {
        const val TYPE_1 = 1
        const val TYPE_2 = 2
        const val TYPE_3 = 3
        const val TYPE_4 = 4
        const val TYPE_5 = 5
        const val STATUS_1 = "Chờ xác nhận"
        const val STATUS_2 = "Đã xác nhận"
        const val STATUS_3 = "Đang giao hàng"
        const val STATUS_4 = "Đã giao hàng"
        const val STATUS_5 = "Đã hủy"
    }
}
