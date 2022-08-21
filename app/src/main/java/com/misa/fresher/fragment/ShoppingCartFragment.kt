package com.misa.fresher.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
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
import com.example.historyvideokotlin.ui.ProgressBarDialog
import com.google.gson.Gson
import com.misa.fresher.MainActivity
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterShoppingCart
import com.misa.fresher.databinding.FragmentBillDetailBinding
import com.misa.fresher.databinding.FragmentShoppingCartBinding
import com.misa.fresher.model.*
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


class ShoppingCartFragment : Fragment() {
    private var listShoppingCart = arrayListOf<Cart>()
    private val viewModel: UserViewModel by activityViewModels()
    private val decimal = DecimalFormat("0,000.0")
    private val binding: FragmentShoppingCartBinding by lazy {
        FragmentShoppingCartBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customer.observe(viewLifecycleOwner, Observer<User> {
            getListShoppingCart(it.idU)
        })
        binding.tvBillCode.text = "Giỏ hàng"
        binding.ivShipping.visibility = View.GONE
        onBack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initView(idU: Int) {
        val adapter = AdapterShoppingCart(listShoppingCart, requireContext()) {
            deleteAll(
                it.product_id,
                idU
            )
        }
        binding.rvSelectedProduct.adapter = adapter
        binding.rvSelectedProduct.layoutManager = LinearLayoutManager(requireActivity())
        val total = listShoppingCart.sumOf {
            if (it.sale_price > 0) {
                it.quantity * it.sale_price.toDouble()
            } else {
                it.quantity * it.price.toDouble()
            }
        }
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
            binding.tvTotalMoney.let {
                it.text = decimal.format(total)
                    .toString() + " ₫"
                it.setTextColor(resources.getColor(R.color.red))
            }
            binding.tvTotalBillPrice.setOnClickListener {
                findNavController().navigate(
                    R.id.action_shoppingCartFragment_to_oderFragment,
                    bundleOf("list" to listShoppingCart)
                )
            }
        } else {
            binding.tvTotalSelectedProduct.let {
                it.text = "0"
                it.setBackgroundResource(R.drawable.border_left_corner)
            }
            binding.tvTotalBillPrice.let {
                it.text = ""
                it.hint = context?.resources?.getText(R.string.not_select)
                it.setBackgroundResource(R.drawable.border_right_corner_3)
            }
            binding.tvTotalMoney.text =
                decimal.format(0)
                    .toString()
        }

    }

    private fun onBack() {
        binding.ibBack.setOnClickListener { activity?.onBackPressed() }
        binding.tvBuyMore.setOnClickListener { activity?.onBackPressed() }
    }

    private fun getListShoppingCart(id: Int) {
        (activity as MainActivity).showLoading(true)
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(IO).launch {
            try {
                val response = api.getShoppingCart(id)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Main) {
                        listShoppingCart = (response.body() as ArrayList<Cart>?)!!
                        initView(id)
                        (activity as MainActivity).showLoading(false)
                    }
                } else {
                    Log.e("errr", response.errorBody().toString())
                    (activity as MainActivity).showLoading(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun deleteAll(idP: Int, idU: Int) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(R.string.confirm_delete)
                setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
                        CoroutineScope(IO).launch {
                            try {
                                val response = api.deleteByID(idP, idU)
                                if (response.isSuccessful && response.body() != null) {
                                    withContext(Main) {
                                        val body =
                                            Gson().fromJson(response.body(), Messenger::class.java)
                                        dialog.dismiss()
                                        activity?.showToast(body.msg)
                                        getListShoppingCart(idU)
                                    }
                                } else {
                                    withContext(Main) {
                                        if(response.code() == 404) {
                                            val errorBody = Gson().fromJson(response.errorBody()?.charStream(),Messenger::class.java)
                                            activity?.showToast(errorBody.msg)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
                setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            }
            builder.create()
        }
        alertDialog!!.show()
    }
}