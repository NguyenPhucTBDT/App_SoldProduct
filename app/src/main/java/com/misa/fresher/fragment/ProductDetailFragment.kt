package com.misa.fresher.fragment

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.misa.fresher.R
import com.misa.fresher.databinding.FragmentProductDetailBinding
import com.misa.fresher.model.*
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import com.misa.fresher.viewmodel.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


class ProductDetailFragment : Fragment() {
    val binding: FragmentProductDetailBinding by lazy {
        FragmentProductDetailBinding.inflate(
            layoutInflater
        )
    }
    val viewModel: UserViewModel by activityViewModels()
    val decimalFormat = DecimalFormat("0,000")
    var productName: String? = null
    var imglink: String? = null
    var price: Float? = null
    var salePrice: Float? = null
    var idU: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getProductInfo()
        setUpView()
    }

    @SuppressLint("SetTextI18n")
    private fun getProductInfo() {
        val idP = arguments?.getInt("product_id")
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getProductDetail(idP!!)
                if (response.isSuccessful && response.body() != null) {
                    val type = object : TypeToken<ProductDetail>() {}.type
                    val body = Gson().fromJson(response.body(), type) as ProductDetail
                    withContext(Dispatchers.Main) {
                        Picasso.get().load(body.imglink).into(binding.imgProduct)
                        binding.tvProductName.text = body.product_name
                        binding.tvManufacture.text = "Xuất xứ : ${body.manufacture}"
                        binding.tvDescription.text = body.description
                        productName = body.product_name
                        imglink = body.imglink
                        price = body.price
                        salePrice = body.sale_price
                        if (body.sale_price > 0) {
                            binding.tvSalePrice.apply {
                                this.text = decimalFormat.format(body.sale_price) + " ₫"
                                this.setTextColor(resources.getColor(R.color.red))
                            }
                            binding.tvPrice.apply {
                                this.text = decimalFormat.format(body.price).toString() + " ₫"
                                this.setTextColor(resources.getColor(R.color.color_SKU))
                                this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            }
                        } else {
                            binding.tvPrice.apply {
                                this.text = decimalFormat.format(body.price) + " ₫"
                                this.setTextColor(resources.getColor(R.color.red))
                            }
                            binding.tvSalePrice.visibility = View.GONE
                        }
                    }
                } else {
                    activity?.showToast("Error : ${response.errorBody()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpView() {
        var amount = binding.tvAmount.text.toString().toInt()
        binding.btnAdd.setOnClickListener {
            amount += 1
            binding.tvAmount.text = amount.toString()
        }
        binding.btnSubtract.setOnClickListener {
            if (amount > 1) {
                amount -= 1
                binding.tvAmount.text = amount.toString()
            } else {
                activity?.showToast(this.getString(R.string.message_quantity))
            }
        }
        binding.tvAddCart.setOnClickListener {
            if (idU == 0) {
                activity?.showToast("Vui lòng đăng nhập")
            } else {
                val productId = arguments?.getInt("product_id")
                val cart = Cart(
                    idU,
                    productId!!,
                    productName!!,
                    amount,
                    imglink!!,
                    price!!,
                    salePrice!!
                )
                addCart(cart)
            }
        }
        binding.ibBack.setOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.customer.observe(viewLifecycleOwner) {
            idU = it.idU
        }
    }

    private fun addCart(cart: Cart) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.insertShoppingCart(cart)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val body = Gson().fromJson(response.body(), Messenger::class.java)
                        activity?.showToast(body.msg)
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
                            activity?.showToast(response.errorBody().toString())
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}