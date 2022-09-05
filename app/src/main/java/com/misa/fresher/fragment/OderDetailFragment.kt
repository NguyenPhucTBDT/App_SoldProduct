package com.misa.fresher.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterOderDetail
import com.misa.fresher.databinding.FragmentOderDetailBinding
import com.misa.fresher.model.Messenger
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
    private var status: Int? = 0
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
        status = arguments?.getInt("status")
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
        binding.btnCancel.setOnClickListener() {
            cancelOrder(idO!!);
        }
        val tvStatus = binding.tvStatusOrder
        when (status) {
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
                binding.btnCancel.visibility = View.GONE
                tvStatus.apply {
                    this.text = STATUS_3
                    this.setTextColor(Color.RED)
                }
            }
            TYPE_4 -> {
                binding.btnCancel.apply {
                    this.text= "Đặt hàng"
                    this.setTextColor(Color.GREEN)
                    this.isEnabled = false
                }
                tvStatus.apply {
                    this.text = STATUS_4
                    this.setTextColor(Color.GREEN)
                }
            }
            TYPE_5 -> {
                binding.btnCancel.apply {
                    this.text= "Đặt hàng"
                    this.setTextColor(Color.GREEN)
                    this.isEnabled = false
                }
                tvStatus.apply {
                    this.text = STATUS_5
                    this.setTextColor(Color.RED)
                }
            }
        }

    }

    private fun getOderDetail() {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getOderDetailById(idO!!)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val body = Gson().fromJson(
                            response.body(),
                            object :
                                TypeToken<List<OrderDetail>>() {}.type
                        ) as ArrayList<OrderDetail>
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

    private fun cancelOrder(idO: Int) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage("Bạn có muốn hủy đơn hàng?")
                setPositiveButton(
                    R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = api.cancelOrder(idO)
                            try {
                                if (response.isSuccessful && response.body() != null) {
                                    withContext(Dispatchers.Main) {
                                        val body =
                                            Gson().fromJson(response.body(), Messenger::class.java)
                                        activity?.showToast(body.msg)
                                        activity?.onBackPressed()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        if (response.code() == 404) {
                                            val errorBody = Gson().fromJson(
                                                response.errorBody()?.charStream(),
                                                Messenger::class.java
                                            )
                                            activity?.showToast(errorBody.msg)
                                        } else {
                                            activity?.showToast("Có lỗi xảy ra. Vui lòng thử lại")
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
                setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            }
            builder.create()
        }
        alertDialog!!.show()
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