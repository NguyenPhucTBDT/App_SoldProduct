package com.misa.fresher.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misa.fresher.databinding.ListBillItemLayoutBinding
import com.misa.fresher.model.BillInfor
import com.misa.fresher.model.Invoice
import com.misa.fresher.model.SelectedProduct
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ListBillAdapter(
    private val mListInvoice: ArrayList<Invoice>,
    val updateView: (invoice: Invoice) -> Unit
) :
    RecyclerView.Adapter<ListBillAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ListBillItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvBillNum = binding.tvBillNum
        private val decimalFormat = DecimalFormat("0,000.0")
        private val tvBillAmount = binding.tvBillAmount
        private val llListOder = binding.llListOder
        fun bind(invoice: Invoice) {
            tvBillNum.text = invoice.id.toString()
            tvBillAmount.text = decimalFormat.format(invoice.amount).toString() + " VNĐ"
            tvBillAmount.setTextColor(Color.GREEN)
            llListOder.setOnClickListener { updateView(invoice) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListBillAdapter.ViewHolder {
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
}
