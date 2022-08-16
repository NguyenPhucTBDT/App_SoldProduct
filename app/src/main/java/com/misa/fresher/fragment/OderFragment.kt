package com.misa.fresher.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterOder
import com.misa.fresher.databinding.FragmentOderBinding
import com.misa.fresher.model.Cart
import com.misa.fresher.viewmodel.UserViewModel
import java.text.DecimalFormat


class OderFragment : Fragment() {
    val binding: FragmentOderBinding by lazy { FragmentOderBinding.inflate(layoutInflater) }
    val decimalFormat = DecimalFormat("0,000")
    var listProduct = arrayListOf<Cart>()
    val viewModel : UserViewModel by activityViewModels()
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
        binding.llAddress.setOnClickListener {
            findNavController().navigate(R.id.action_oderFragment_to_addressUserFragment, bundleOf(
                Pair("type",2)
            ))
        }
        binding.ibBack.setOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.addressUser.observe(
            viewLifecycleOwner
        ) {
            binding.tvPhone.text = it.name + " | "+ it.phone
            binding.tvAddress.text = it.address
        }
    }
}