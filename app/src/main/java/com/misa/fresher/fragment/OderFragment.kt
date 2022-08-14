package com.misa.fresher.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterOder
import com.misa.fresher.databinding.FragmentOderBinding
import com.misa.fresher.model.Cart
import java.text.DecimalFormat


class OderFragment : Fragment() {
    val binding: FragmentOderBinding by lazy { FragmentOderBinding.inflate(layoutInflater) }
    val decimalFormat = DecimalFormat("0,000")
    var listProduct = arrayListOf<Cart>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getList()
        initView()
    }

    private fun getList() {
        listProduct = arguments?.get("list") as ArrayList<Cart>
    }

    private fun initView() {
        val adapterOder = AdapterOder(listProduct)
        binding.rcvListOder.let {
            it.adapter = adapterOder
            it.layoutManager = LinearLayoutManager(requireContext())
        }
        val total = listProduct.sumOf { it.quantity * it.price.toDouble() }
        binding.tvTotalPrice.text = decimalFormat.format(total).toString() + " ₫"
    }
}