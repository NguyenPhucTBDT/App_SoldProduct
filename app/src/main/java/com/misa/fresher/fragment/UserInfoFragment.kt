package com.misa.fresher.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.misa.fresher.databinding.FragmentUserInfoBinding
import com.misa.fresher.model.User
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import com.misa.fresher.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserInfoFragment : Fragment() {
    val viewModel: UserViewModel by activityViewModels()
    var idU: Int? = 0
    val binding: FragmentUserInfoBinding by lazy { FragmentUserInfoBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intData()
        binding.btnUpdate.setOnClickListener {
            update()
        }
        binding.btnGoBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun intData() {
        viewModel.customer.observe(viewLifecycleOwner) {
            binding.edtFullName.setText(it.fullname)
            binding.edtPhone.setText(it.phone)
            binding.edtEmail.setText(it.email)
            idU = it.idU
        }
    }

    private fun update() {
        val fullName = binding.edtFullName.text.trim().toString()
        val phone = binding.edtPhone.text.trim().toString()
        val email = binding.edtEmail.text.trim().toString()
        if (binding.edtFullName.text.trim().toString().isEmpty()) {
            activity?.showToast("Họ và tên bắt buộc")
        } else if (binding.edtPhone.text.trim().toString().isEmpty()) {
            activity?.showToast("Số điện thoại bắt buộc")
        } else if (binding.edtEmail.text.trim().toString().isEmpty()) {
            activity?.showToast("Email bắt buộc")
        } else {
            val api = ApiHelper.getInstance().create(ApiInterface::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = api.updateUserInfo(
                        User(idU!!, "", "", fullName, phone, email)
                    )
                    if (response.isSuccessful && response.body() != null) {
                        withContext(Main) {
                            val boolean = Gson().fromJson(
                                response.body(),
                                object : TypeToken<Boolean>() {}.type
                            ) as Boolean
                            if (boolean) {
                                activity?.showToast("Cập nhật thành công")
                                viewModel.addUser(User(idU!!, "", "", fullName, phone, email))
                            } else {
                                activity?.showToast("Cập nhật không thành công")
                            }
                        }
                    } else {
                        withContext(Main) {
                            activity?.showToast("Error : ${response.errorBody()}")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}