package com.misa.fresher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misa.fresher.R
import com.misa.fresher.databinding.LayoutItemBillBinding
import com.misa.fresher.model.SelectedProduct
import com.misa.fresher.showToast
import java.text.DecimalFormat

class BillProductAdapter(
    private val mListBill: ArrayList<SelectedProduct>, val mCtx: Context,
    val updateView: (product: SelectedProduct) -> Unit
) :
    RecyclerView.Adapter<BillProductAdapter.ViewHolder>() {
    inner class ViewHolder(binding: LayoutItemBillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvBillProductName =binding.tvBillProductName
        private val tvBillProductPrice = binding.tvBillProductPrice
        private val tvBillProductSKU = binding.tvBillProductSKU
        private val tvBillTotalPrice = binding.tvBillTotalPrice
        private val tvAmountProduct = binding.tvAmountProduct
        private val ibAddAmount = binding.ibAddAmount
        private val ibSubtractAmount = binding.ibSubtractAmount
        private val decimalFormat = DecimalFormat("0,000.0")
        fun bind(product: SelectedProduct) {
            tvBillProductName.text = product.product.nameVegetable
            tvBillProductSKU.text = product.product.manufacture
            tvBillProductPrice.text = decimalFormat.format(product.product.price).toString()
            tvBillTotalPrice.text =
                decimalFormat.format(product.amount * product.product.price)
                    .toString()
            tvAmountProduct.text = product.amount.toString()
            ibAddAmount.setOnClickListener {
                product.amount++
                tvAmountProduct.text = product.amount.toString()
                tvBillProductPrice.text =
                    decimalFormat.format(product.amount * product.product.price).toString()
                updateView(product)
            }
            ibSubtractAmount.setOnClickListener {
                if (product.amount > 1) {
                    product.amount--
                    tvAmountProduct.text = product.amount.toString()
                    tvBillTotalPrice.text =
                        decimalFormat.format(product.amount * product.product.price)
                            .toString()
                    updateView(product)
                } else if (product.amount == 1) {
                    mCtx.showToast(mCtx.resources?.getText(R.string.message_quantity).toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemBillBinding by lazy {
            LayoutItemBillBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(mListBill[position])

    override fun getItemCount() = mListBill.size
}