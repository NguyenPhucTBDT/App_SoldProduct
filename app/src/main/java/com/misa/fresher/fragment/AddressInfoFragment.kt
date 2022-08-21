package com.misa.fresher.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.misa.fresher.MainActivity
import com.misa.fresher.databinding.FragmentAddressInfoBinding
import com.misa.fresher.model.Address
import com.misa.fresher.model.Messenger
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import com.misa.fresher.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressInfoFragment : Fragment() {
    val binding: FragmentAddressInfoBinding by lazy {
        FragmentAddressInfoBinding.inflate(
            layoutInflater
        )
    }
    val viewModel : UserViewModel by activityViewModels()
    var type: Int = 0
    private var idA: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val edtFullName = binding.edtFullName
        val edtPhone = binding.edtPhone
        val edtAddress = binding.edtAddress
        type = arguments?.getInt("type")!!
        if (type == TYPE_1) {
            binding.tvTitle.text = TITLE_1
            binding.btnUpdate.text = "Thêm mới"
        } else {
            binding.tvTitle.text = TITLE_2
            arguments.let {
                edtFullName.setText(it?.getString("name"))
                edtAddress.setText(it?.getString("address"))
                edtPhone.setText(it?.getString("phone"))
                idA = it?.getInt("idA")!!
            }
        }
        binding.btnGoBack.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnUpdate.setOnClickListener {
            if (type == TYPE_1) {
                if(edtAddress.text.trim().toString().isEmpty()) {
                    activity?.showToast("Địa chỉ giao hàng bắt buộc")
                }
                else if(edtFullName.text.trim().toString().isEmpty()) {
                    activity?.showToast("Người nhận bắt buộc")
                }
                else if(edtPhone.text.trim().toString().isEmpty()) {
                    activity?.showToast("Số điện thoại bắt buộc")
                }
                else {
                    viewModel.customer.observe(viewLifecycleOwner) {
                        addAddress(Address(0,it.idU,edtAddress.text.toString(),edtFullName.text.toString(),edtPhone.text.toString()))
                    }
                }
            } else {
                if(edtAddress.text.trim().toString().isEmpty()) {
                    activity?.showToast("Địa chỉ giao hàng bắt buộc")
                }
                else if(edtFullName.text.trim().toString().isEmpty()) {
                    activity?.showToast("Người nhận bắt buộc")
                }
                else if(edtPhone.text.trim().toString().isEmpty()) {
                    activity?.showToast("Số điện thoại bắt buộc")
                }
                else {
                    updateAddress(Address(idA,0,edtAddress.text.toString(),edtFullName.text.toString(),edtPhone.text.toString()))
                }
            }
        }
    }

    private fun addAddress(address: Address) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.insertAddress(address)
            try {
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val body =
                            Gson().fromJson(response.body(), Messenger::class.java)
                        activity?.showToast(body.msg)
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed(Runnable {
                            activity?.onBackPressed()
                        }, 200)
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
    }

    private fun updateAddress(address: Address) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.updateAddress(address)
            try {
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val body =
                            Gson().fromJson(response.body(), Messenger::class.java)
                        activity?.showToast(body.msg)
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed(Runnable {
                            activity?.onBackPressed()
                        }, 200)
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
    }

    companion object {
        const val TYPE_1 = 1
        const val TYPE_2 = 2
        const val TITLE_1 = "Thêm mới địa chỉ"
        const val TITLE_2 = "Thông tin địa chỉ"
    }
}