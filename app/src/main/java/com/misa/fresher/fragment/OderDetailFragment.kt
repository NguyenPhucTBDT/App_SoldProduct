package com.misa.fresher.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.misa.fresher.adapter.AdapterOderDetail
import com.misa.fresher.databinding.FragmentOderDetailBinding
import com.misa.fresher.model.InvoiceDetail
import com.misa.fresher.model.OrderDetail
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OderDetailFragment : Fragment() {
    private var idO: Int? = 0
    private var address: String? = null
    private var phone: String? = null
    private var name: String? = null
    val binding: FragmentOderDetailBinding by lazy {
        FragmentOderDetailBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInvoice()
        getOderDetail()
        onBack()
    }

    private fun onBack() {
        binding.ibBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun getInvoice() {
        idO = arguments?.getInt("idO")
        address = arguments?.getString("address")
        phone = arguments?.getString("phone")
        name = arguments?.getString("name")
    }

    @SuppressLint("SetTextI18n")
    private fun initView(list: ArrayList<OrderDetail>) {
        val adapterOderDetail = AdapterOderDetail(list)
        binding.rcvOderDetail.adapter = adapterOderDetail
        binding.rcvOderDetail.layoutManager = LinearLayoutManager(requireContext())
        binding.tvOderId.text = idO.toString()
        binding.tvAddress.text = "Địa chỉ giao hàng : $address"
        binding.tvPhone.text = phone
        binding.tvFullName.text = name
    }

    private fun getOderDetail() {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getOderDetailById(idO!!)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val body = Gson().fromJson(response.body(),object : TypeToken<List<OrderDetail>>() {}.type) as ArrayList<OrderDetail>
                        initView(body)
                    }
                } else {
                    activity?.showToast("Error : ${response.errorBody()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}