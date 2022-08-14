package com.misa.fresher.fragment

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.misa.fresher.R
import com.misa.fresher.databinding.FragmentProductDetailBinding
import com.misa.fresher.model.Cart
import com.misa.fresher.model.Category
import com.misa.fresher.model.ShoppingCart
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
    val viewModel : UserViewModel by activityViewModels()
    val decimalFormat = DecimalFormat("0,000")
    var productName : String ?= null
    var imglink : String ?= null
    var price : Float?=null
    var sale_price : Float?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        getProductInfo()
        setUpView()
    }

    private fun setupView() {
        binding.ibBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun getProductInfo() {
        val idP = arguments?.getInt("product_id")
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getProductDetail(idP!!)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        Picasso.get().load(body!!.imglink).into(binding.imgProduct)
                        binding.tvProductName.text = body.product_name
                        binding.tvManufacture.text = "Xuất xứ : ${body.manufacture}"
                        binding.tvDescription.text = body.description
                        productName = body.product_name
                        imglink = body.imglink
                        price = body.price
                        sale_price = body.sale_price
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
                            binding.tvSalePrice.visibility = View . GONE
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
            amount+=1
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
            viewModel.customer.observe(viewLifecycleOwner) {
                val productId = arguments?.getInt("product_id")
                val cart = Cart(it.idU, productId!!,productName!!,amount,imglink!!,price!!,sale_price!!)
                addCart(cart,it.idU)
            }
        }
    }
    private fun addCart(cart: Cart, idU: Int) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.insertShoppingCart(cart, idU)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        if (response.body()!!.id == 200) {
                            activity?.showToast("Thêm vào giỏ hàng thành công")
                            activity?.onBackPressed()
                        } else {
                            activity?.showToast("Thêm vào giỏ hàng thất bại")
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
}