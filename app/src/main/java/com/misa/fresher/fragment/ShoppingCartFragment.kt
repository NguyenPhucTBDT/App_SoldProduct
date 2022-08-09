package com.misa.fresher.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterShoppingCart
import com.misa.fresher.databinding.FragmentBillDetailBinding
import com.misa.fresher.model.*
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import com.misa.fresher.viewpager.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


class ShoppingCartFragment : Fragment() {
    private var listShoppingCart = arrayListOf<ShoppingCart>()
    private val viewModel: UserViewModel by activityViewModels()
    private var idU: Int? = 0
    private val decimal = DecimalFormat("0,000.0")
    private val binding: FragmentBillDetailBinding by lazy {
        FragmentBillDetailBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customer.observe(viewLifecycleOwner, Observer<UserRespone> {
            getListShoppingCart(it.id)
        })
        binding.tvBillCode.text = "Giỏ hàng"
        binding.imbCart.visibility = View.GONE
        onBack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun initView() {
        val adapter = AdapterShoppingCart(listShoppingCart!!, requireContext()) {}
        binding.rvSelectedProduct.adapter = adapter
        binding.rvSelectedProduct.layoutManager = LinearLayoutManager(requireActivity())
        val total = listShoppingCart.sumOf { it.quantity * it.price.toDouble() }
        if (listShoppingCart.size > 0) {
            binding.tvTotalSelectedProduct.let {
                it.text = listShoppingCart.sumOf { it.quantity }.toString()
                it.setTextColor(Color.WHITE)
                it.setBackgroundResource(R.drawable.textview_amount_border)
            }
            binding.tvTotalBillPrice.let {
                it.text = context?.resources?.getText(R.string.get_payment)
                it.setTextColor(Color.WHITE)
                it.setBackgroundResource(R.drawable.textview_totalprice_border)
            }
            binding.ivShipping.setOnClickListener {
                findNavController().navigate(R.id.action_shoppingCartFragment_to_addressUserFragment)
            }
            binding.tvTotalMoney.text =
                decimal.format(total)
                    .toString()
            binding.tvTotalBillPrice.setOnClickListener {
                viewModel.addressUser.observe(viewLifecycleOwner,Observer<AddressUser> {
                    if(it != null) {
                        oderProduct(it)
                    }
                    else {

                    }
                })
            }
        }

    }

    private fun onBack() {
        binding.ibBack.setOnClickListener { activity?.onBackPressed() }
        binding.tvBuyMore.setOnClickListener { activity?.onBackPressed() }
    }

    private fun getListShoppingCart(id: Int) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(IO).launch {
            try {
                val response = api.getShoppingCart(id)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Main) {
                        listShoppingCart = (response.body() as ArrayList<ShoppingCart>?)!!
                        initView()
                    }
                } else {
                    Log.e("errr", response.errorBody().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun oderProduct(addressUser: AddressUser) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<UserRespone> {
                idU = it.id
            })
        CoroutineScope(IO).launch {
            val amount = listShoppingCart.sumOf { it.price.toDouble() * it.quantity }
            val list = ArrayList<InvoiceDetail>()
            for (i in listShoppingCart) {
                list.add(
                    InvoiceDetail(
                        0,
                        i.idV,
                        i.title.toString(),
                        i.quantity,
                        i.price,
                        ""
                    )
                )
            }
            val cart = Cart(idU!!, addressUser.address,addressUser.phone, amount.toFloat(), list)
            try {
                val response = api.oderProduct(cart)
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.id == 200) {
                        withContext(Main) {
                            deleteAll(idU!!)
                            activity?.showToast("Đơn hàng được tạo thành công")
                        }
                    } else {
                        withContext(Main) {
                            activity?.showToast("Đơn hàng được tạo không thành công")
                        }
                    }
                } else {
                    Log.e("errr", response.errorBody().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteAll(id: Int) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(IO).launch {
            try {
                val response = api.deleteAll(id)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Main) {
                        if (response.body()!!.id == 200) {
                            activity?.onBackPressed()
                        } else {
                            activity?.showToast("Xóa không thành công")
                        }
                    }
                } else {
                    Log.e("errr", response.errorBody().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}