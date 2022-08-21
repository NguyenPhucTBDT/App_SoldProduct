package com.misa.fresher.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterAddress
import com.misa.fresher.databinding.FragmentAddressUserBinding
import com.misa.fresher.model.Address
import com.misa.fresher.model.Messenger
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


class AddressUserFragment : Fragment() {
    val binding: FragmentAddressUserBinding by lazy {
        FragmentAddressUserBinding.inflate(
            layoutInflater
        )
    }
    val viewModel: UserViewModel by activityViewModels()
    var idU: Int = 0
    var type: Int = 0
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
        val adapterAddress =
            AdapterAddress(list as ArrayList<Address>, { chooseAddress(it) }, { deleteAdd(it) })
        binding.rvSelectedProduct.adapter = adapterAddress
        binding.rvSelectedProduct.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getListAddress() {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        viewModel.customer.observe(viewLifecycleOwner) {
            CoroutineScope(IO).launch {
                Log.e("idU:", idU.toString())
                val response = api.getListAddress(it.idU)
                try {
                    if (response.isSuccessful && response.body() != null) {
                        withContext(Main) {
                            val type = object : TypeToken<List<Address>>() {}.type
                            val listAddress =
                                Gson().fromJson(response.body(), type) as ArrayList<Address>
                            setupView(listAddress)
                        }
                    } else {
                        withContext(Main) {
                            activity?.showToast("Có lỗi xảy ra. Vui lòng thử lại")
                        }
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        type = arguments?.getInt(TYPE)!!
        if (type == TYPE_GET_ADDRESS) {
            binding.tvBillCode.text = "Danh sách địa chỉ"
        } else if (type == TYPE_CHOOSE_ADDRESS) {
            binding.tvBillCode.text = "Chọn địa chỉ nhận hàng"
        }
        binding.tvAddAddress.setOnClickListener {
            findNavController().navigate(
                R.id.action_addressUserFragment_to_addressInfoFragment,
                bundleOf(Pair("type", 1))
            )
        }
    }

    private fun chooseAddress(a: Address) {
        if (type == TYPE_GET_ADDRESS) {
            findNavController().navigate(
                R.id.action_addressUserFragment_to_addressInfoFragment,
                bundleOf(
                    Pair("idA", a.idA),
                    Pair("address", a.address),
                    Pair("name", a.name),
                    Pair("phone", a.phone),
                    Pair("type", "2")
                )
            )
        } else if (type == TYPE_CHOOSE_ADDRESS) {
            viewModel.addAddress(a)
            activity?.onBackPressed()
        }
    }

    companion object {
        const val TYPE_GET_ADDRESS = 1
        const val TYPE_CHOOSE_ADDRESS = 2
        const val TYPE = "type"
    }

    private fun deleteAdd(address: Address) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(R.string.confirm_delete)
                setPositiveButton(
                    R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
                        CoroutineScope(IO).launch {
                            val response = api.deleteAddress(address.idA)
                            try {
                                if (response.isSuccessful && response.body() != null) {
                                    withContext(Main) {
                                        val body =
                                            Gson().fromJson(response.body(), Messenger::class.java)
                                        activity?.showToast(body.msg)
                                        getListAddress()
                                    }
                                } else {
                                    withContext(Main) {
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
}