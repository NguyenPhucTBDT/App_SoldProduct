package com.misa.fresher.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
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
import com.misa.fresher.BillViewModel
import com.misa.fresher.R
import com.misa.fresher.adapter.BillProductAdapter
import com.misa.fresher.showToast
import com.misa.fresher.data.bill.ImplBillDAO
import com.misa.fresher.data.cart.ImplCartDAO
import com.misa.fresher.databinding.FragmentBillDetailBinding
import com.misa.fresher.model.*
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.viewpager.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class BillDetailFragment : Fragment() {
    private val binding: FragmentBillDetailBinding by lazy {
        FragmentBillDetailBinding.inflate(
            layoutInflater
        )
    }
    private var idU: Int? = 0
    private var listBillDetail = arrayListOf<SelectedProduct>()
    private val decimal = DecimalFormat("0,000.0")
    private val viewModel: UserViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listBillDetail = getSelectedProduct()
        setUpRecycleView()
        setUpView()
        onBack()
        openShippingInfor()
        saveBillDetail()
    }

    /**
     *Lấy danh sách sản phẩm đã chọn từ Sale Fragment
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun getSelectedProduct(): ArrayList<SelectedProduct> {
        return arguments?.get(PRODUCTS) as ArrayList<SelectedProduct>
    }

    /**
     *Nạp dữ liệu vào RecycleView hiển thị danh sách sản phẩm đã chọn
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun setUpRecycleView() {
        val adapter = BillProductAdapter(
            listBillDetail,
            requireContext()
        ) { changeAmountProduct(it) }
        binding.rvSelectedProduct.adapter = adapter
        binding.rvSelectedProduct.layoutManager = LinearLayoutManager(requireActivity())
    }

    /**
     *Thay đổi UI khi nhận được danh sách sản phẩm đã chọn
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun setUpView() {
        binding.imbCart.visibility = View.GONE
        binding.tvTotalSelectedProduct.let {
            it.text = listBillDetail.sumOf { it.amount }.toString()
            it.setTextColor(Color.WHITE)
            it.setBackgroundResource(R.drawable.textview_amount_border)
        }
        binding.tvTotalBillPrice.let {
            it.text = context?.resources?.getText(R.string.get_payment)
            it.setTextColor(Color.WHITE)
            it.setBackgroundResource(R.drawable.textview_totalprice_border)
        }
        binding.tvTotalMoney.text =
            decimal.format(listBillDetail.sumOf { it.amount * it.product.price.toDouble() })
                .toString()
    }

    /**
     *Quay trở lại SaleFragment
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun onBack() {
        binding.ibBack.setOnClickListener { activity?.onBackPressed() }
        binding.tvBuyMore.setOnClickListener { activity?.onBackPressed() }
    }

    private fun changeAmountProduct(product: SelectedProduct) {
        for (i in listBillDetail) {
            if (i.product.idVegetable == product.product.idVegetable) {
                i.amount = product.amount
            }
        }
        binding.tvTotalMoney.text =
            decimal.format(listBillDetail.sumOf { it.amount * it.product.price.toDouble() })
                .toString()
        binding.tvTotalSelectedProduct.text =
            listBillDetail.sumOf { it.amount }.toString()
    }

    /**
     *Chuyển hướng sang màn hình ShippingInfor
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun openShippingInfor() {
        binding.ivShipping.setOnClickListener {
            findNavController().navigate(
                R.id.action_billDetailFragment_to_addressUserFragment
            )
        }
    }

    /**
     *Lưu hóa đơn vào database bằng SQLite
     *@author:NCPhuc
     *@date:3/26/2022
     **/
    private fun saveBillDetail() {
        binding.tvTotalBillPrice.setOnClickListener {
            viewModel.addressUser.observe(viewLifecycleOwner, Observer<AddressUser> {
                it.let {
                    if (it != null) {
                        oderProduct(it)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Vui lòng chọn địa chỉ giao hàng",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })

        }
    }

    private fun oderProduct(address: AddressUser) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        viewModel.customer.observe(viewLifecycleOwner,
            Observer<UserRespone> {
                idU = it.id
            })
        CoroutineScope(IO).launch {
            val amount = listBillDetail.sumOf { it.product.price.toDouble() * it.amount }
            val list = ArrayList<InvoiceDetail>()
            for (i in listBillDetail) {
                list.add(
                    InvoiceDetail(
                        0,
                        i.product.idVegetable,
                        i.product.nameVegetable.toString(),
                        i.amount,
                        i.product.price,
                        ""
                    )
                )
            }
            val cart =
                Cart(idU!!, address.address, address.phone, amount.toFloat(), list)
            try {
                val response = api.oderProduct(cart)
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.id == 200) {
                        withContext(Main) {
                            activity?.showToast("Đơn hàng được tạo thành công")
                            findNavController().navigate(R.id.action_billDetailFragment_to_saleFragment)
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

    companion object {
        const val PRODUCTS = "product"
    }

}

