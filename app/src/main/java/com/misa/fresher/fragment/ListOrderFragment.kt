package com.misa.fresher.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.misa.fresher.MainActivity
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterOderInfo
import com.misa.fresher.databinding.FragmentListOrdersBinding
import com.misa.fresher.model.Order
import com.misa.fresher.model.User
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import com.misa.fresher.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class ListOrderFragment : Fragment() {
    private val binding: FragmentListOrdersBinding by lazy {
        FragmentListOrdersBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: UserViewModel by activityViewModels()
    private val decimalFormat = DecimalFormat("0,000.0")
    var mListOrder = arrayListOf<Order>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customer.observe(viewLifecycleOwner) {
            getListInvoice(it.idU)
        }
        setUpView()
    }

    /**
     *Thiết lập dữ liệu cho Spinner
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    private fun setUpView() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spnDay,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnDay.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spnPayment,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnPayment.adapter = adapter
        }
        binding.ibMenuLb.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    /**
     *Thiết lập lấy danh sách hóa đơn từ SQLite lên RecycleView
     *@author:NCPhuc
     *@date:3/25/2022
     **/
    private fun setUpRecycleView() {
        val adapter = AdapterOderInfo(mListOrder) { gotoOderDetail(it) }
        binding.rcvBill.adapter = adapter
        binding.rcvBill.layoutManager = LinearLayoutManager(requireContext())
        binding.tvListBillSize.text = mListOrder.size.toString()
        binding.tvListBillAmount.text =
            decimalFormat.format(mListOrder.sumOf { it.amount.toDouble() }).toString()
    }

    /**
     *Lấy danh sách hóa đơn
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    private fun getListInvoice(id: Int) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(IO).launch {
            try {
                val response = api.getListOder(id)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Main) {
                        val type = object : TypeToken<List<Order>>() {}.type
                        mListOrder = Gson().fromJson(response.body(), type)
                        setUpRecycleView()
                    }
                } else {
                    withContext(Main) {
                        activity?.showToast(response.errorBody().toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun gotoOderDetail(order: Order) {
        viewModel.customer.observe(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.action_listBillsFragment_to_oderDetailFragment,
                bundleOf(
                    Pair("idO", order.idO),
                    Pair("address", order.address),
                    Pair("phone", order.phone),
                    Pair("name", it.fullname)
                )
            )
        }
    }
}
