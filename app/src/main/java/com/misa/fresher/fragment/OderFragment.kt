package com.misa.fresher.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.misa.fresher.MainActivity
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterOder
import com.misa.fresher.databinding.FragmentOderBinding
import com.misa.fresher.model.Cart
import com.misa.fresher.model.Messenger
import com.misa.fresher.model.OrderDetail
import com.misa.fresher.model.OrderInfo
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import com.misa.fresher.viewmodel.PaymentViewModel
import com.misa.fresher.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OderFragment : Fragment() {
    val binding: FragmentOderBinding by lazy { FragmentOderBinding.inflate(layoutInflater) }
    val decimalFormat = DecimalFormat("0,000")
    var listProduct = arrayListOf<Cart>()
    val viewModel: UserViewModel by activityViewModels()
    var address: String? = null
    var phone: String? = null
    var name: String? = null
    var payment: Int? = 0
    var note: String? = null
    private val paymentModel: PaymentViewModel by activityViewModels()
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

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val adapterOder = AdapterOder(listProduct)
        binding.rcvListOder.let {
            it.adapter = adapterOder
            it.layoutManager = LinearLayoutManager(requireContext())
        }
        val total = listProduct.sumOf { it.quantity * it.price.toDouble() }
        binding.tvTotalPrice.text = decimalFormat.format(total).toString() + " ₫"
        binding.llAddress.setOnClickListener {
            findNavController().navigate(
                R.id.action_oderFragment_to_addressUserFragment, bundleOf(
                    Pair("type", 2)
                )
            )
        }
        binding.llPaymentMethod.setOnClickListener {
            findNavController().navigate(R.id.action_oderFragment_to_paymentMethodFragment)
        }
        binding.ibBack.setOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.addressUser.observe(
            viewLifecycleOwner
        ) {
            binding.tvPhone.text = it.name + " | " + it.phone
            binding.tvAddress.text = it.address
            phone = it.phone
            address = it.address
            name = it.name
        }
        paymentModel.shipItem.observe(viewLifecycleOwner) {
            if (it == PAYMENT_TYPE_1) {
                payment = PAYMENT_TYPE_1
                binding.tvPaymentMethod.text = "Thanh toán khi nhận hàng"
            } else {
                payment = PAYMENT_TYPE_2
                binding.tvPaymentMethod.text = "Thanh toán bằng thẻ ngân hàng"
            }
        }
        binding.btnOrder.setOnClickListener {
            orderProduct()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun orderProduct() {
        val list = mutableListOf<OrderDetail>()
        for (i in listProduct) {
            list.add(
                OrderDetail(
                    0,
                    i.product_id,
                    i.title,
                    i.quantity,
                    i.price,
                    i.sale_price,
                    i.imglink
                )
            )
        }
        val amount = list.sumOf {
            if (it.sale_price > 0) {
                it.quantity * it.sale_price.toDouble()
            } else {
                it.quantity * it.price.toDouble()
            }
        }
        viewModel.customer.observe(viewLifecycleOwner) {
            val date = Date()
            val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val oderInfo = OrderInfo(
                it.idU,
                address.toString(), phone.toString(),name.toString(), amount.toFloat(), payment!!,
                "", format.format(date), 1, list
            )
            val api = ApiHelper.getInstance().create(ApiInterface::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = api.oderProduct(oderInfo)
                    if (response.isSuccessful && response.body() != null) {
                        withContext(Main) {
                            val body = Gson().fromJson(response.body(), Messenger::class.java)
                            activity?.showToast(body.msg)
                            findNavController().navigate(R.id.action_oderFragment_to_saleFragment)
                        }
                    } else {
                        if (response.code() == 404) {
                            val body = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                Messenger::class.java
                            )
                            activity?.showToast(body.msg)
                        } else {
                            activity?.showToast("Có lỗi xảy ra, vui lòng thử lại")
                        }
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    companion object {
        const val PAYMENT_TYPE_1 = 1
        const val PAYMENT_TYPE_2 = 2
    }
}