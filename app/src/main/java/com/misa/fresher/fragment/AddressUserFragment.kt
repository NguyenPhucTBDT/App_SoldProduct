package com.misa.fresher.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterAddress
import com.misa.fresher.databinding.FragmentBillDetailBinding
import com.misa.fresher.model.AddressUser
import com.misa.fresher.model.UserRespone
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import com.misa.fresher.viewpager.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddressUserFragment : Fragment() {
    val binding: FragmentBillDetailBinding by lazy {
        FragmentBillDetailBinding.inflate(
            layoutInflater
        )
    }
    val viewModel: UserViewModel by activityViewModels()
    var idU: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBillCode.text = "Danh sách địa chỉ giao hàng"
        binding.imbCart.visibility = View.GONE
        binding.llBuyMore.visibility = View.GONE
        binding.layoutCus.visibility = View.GONE
        binding.llTotal.visibility = View.GONE
        binding.llBottomToolbar.visibility = View.GONE
        getListAddress()
        onBack()
    }

    private fun onBack() {
        binding.ibBack.setOnClickListener { activity?.onBackPressed() }
        binding.tvBuyMore.setOnClickListener { activity?.onBackPressed() }
    }

    private fun setupView(list: List<AddressUser>) {
        val adapterAddress = AdapterAddress(list as ArrayList<AddressUser>) { chooseAddress(it) }
        binding.rvSelectedProduct.adapter = adapterAddress
        binding.rvSelectedProduct.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getListAddress() {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        viewModel.customer.observe(viewLifecycleOwner, Observer<UserRespone> {
            CoroutineScope(IO).launch {
                Log.e("idU:", idU.toString())
                val response = api.getListAddress(it.id)
                try {
                    if (response.isSuccessful && response.body() != null) {
                        withContext(Main) {
                            setupView(response.body() as ArrayList<AddressUser>)
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

    private fun chooseAddress(address: AddressUser) {
        viewModel.addAddress(address)
        activity?.onBackPressed()
    }
}