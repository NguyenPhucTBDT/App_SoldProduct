package com.misa.fresher.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.misa.fresher.adapter.AdapterAddress
import com.misa.fresher.databinding.FragmentAddressUserBinding
import com.misa.fresher.model.Address
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


class AddressUserFragment : Fragment() {
    val binding: FragmentAddressUserBinding by lazy {
        FragmentAddressUserBinding.inflate(
            layoutInflater
        )
    }
    val viewModel: UserViewModel by activityViewModels()
    var idU: Int = 0
    var type : Int =0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getListAddress()
        onBack()
    }

    private fun onBack() {
        binding.ibBack.setOnClickListener { activity?.onBackPressed() }
    }

    private fun setupView(list: List<Address>) {
        val adapterAddress = AdapterAddress(list as ArrayList<Address>) { chooseAddress(it) }
        binding.rvSelectedProduct.adapter = adapterAddress
        binding.rvSelectedProduct.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getListAddress() {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        viewModel.customer.observe(viewLifecycleOwner, Observer<User> {
            CoroutineScope(IO).launch {
                Log.e("idU:", idU.toString())
                val response = api.getListAddress(it.idU)
                try {
                    if (response.isSuccessful && response.body() != null) {
                        withContext(Main) {
                            setupView(response.body() as ArrayList<Address>)
                        }
                    } else {
                        withContext(Main) {
                            activity?.showToast("Có lỗi xảy ra. Vui lòng thử lại")
                        }
                    }
                } catch (e: Exception) {

                }
            }
        })

    }

    private fun initView() {
        type = arguments?.getInt(TYPE)!!
        if (type == TYPE_GET_ADDRESS) {
            binding.tvBillCode.text = "Danh sách địa chỉ"
        } else if (type == TYPE_CHOOSE_ADDRESS) {
            binding.tvBillCode.text = "Chọn địa chỉ nhận hàng"
        }
    }

    private fun chooseAddress(address: Address) {
        if (type == TYPE_GET_ADDRESS) {
            activity?.showToast("Tesst")
        } else if (type == TYPE_CHOOSE_ADDRESS) {
            viewModel.addAddress(address)
            activity?.onBackPressed()
        }
    }

    companion object {
        const val TYPE_GET_ADDRESS = 1
        const val TYPE_CHOOSE_ADDRESS = 2
        const val TYPE = "type"
    }
}