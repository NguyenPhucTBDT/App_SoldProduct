package com.misa.fresher.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.misa.fresher.MainActivity
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterCate
import com.misa.fresher.adapter.AdapterProduct
import com.misa.fresher.databinding.FragmentSaleBinding
import com.misa.fresher.login.LoginActivity
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
import java.text.Collator
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class SaleFragment : Fragment() {
    private val binding: FragmentSaleBinding by lazy { FragmentSaleBinding.inflate(layoutInflater) }
    var products: ArrayList<Product>? = null
    val viewModel: UserViewModel by activityViewModels()
    private var categories: ArrayList<Category>? = null
    var adapterProduct: AdapterProduct? = null
    private val decimalFormat = DecimalFormat("0,000.0")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListVegetable()
        searchProduct(view)
        clearProduct()
        //showBillFragment()
        //updateView()
        configFilter(view)
        getListVegetable()
        refresh()
    }

    /**
     *Lấy danh sách sản phẩm phẩm để hiện thị lên RecycleView
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun setUpView(vet: List<Product>) {
        adapterProduct =
            AdapterProduct(vet as ArrayList<Product>) {
//                showBottomDialog(it)
            }
        binding.rcvProduct.adapter = adapterProduct
        binding.rcvProduct.layoutManager = GridLayoutManager(requireContext(),2)
        setUpNavigation()
        openDrawerLayoutMenu()
    }

    /**
     *Thiết lập dữ liệu cho spinner
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    private fun setUpSpinner(view: View) {
        categories?.add(Category(0, "None"))
        val spnColor = view.findViewById<Spinner>(R.id.spnColor)
        val adapter = AdapterCate(
            requireContext(), android.R.layout.simple_spinner_item,
            categories!!
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnColor.adapter = adapter
        spnColor.setSelection(categories!!.size - 1)
        spnColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    /**
     *Hiển thị BottomDialog để chọn số lượng của sản phẩm -> dismiss->thêm sản phẩm chọn vào danh sách
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun showBottomDialog(product: Product) {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheetView: View =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_bottom_sheet, view as DrawerLayout, false)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.findViewById<TextView>(R.id.tvName)!!.text = product.product_name
        bottomSheetDialog.findViewById<TextView>(R.id.tvSKU)!!.text = product.price.toString()
        bottomSheetDialog.show()
        val tvAmount =
            bottomSheetView.findViewById<TextView>(R.id.tvAmount)
        val btnAdd = bottomSheetDialog.findViewById<ImageButton>(R.id.btnAdd)
        val btnSubtract = bottomSheetView.findViewById<ImageButton>(R.id.btnSubtract)
        var amount = tvAmount.text.toString().toInt()
        btnAdd?.setOnClickListener {
            amount += 1
            tvAmount.text = amount.toString()
        }
        btnSubtract.setOnClickListener {
            if (amount > 1) {
                amount -= 1
                tvAmount.text = amount.toString()
            } else {
                activity?.showToast(this.getString(R.string.message_quantity))
            }
        }
//        bottomSheetDialog.findViewById<Button>(R.id.btn_buy_now)?.setOnClickListener {
//            if (checkSelectedProduct(vegetable.idVegetable)) {
//                for (i in productList) {
//                    if (i.product.idVegetable == vegetable.idVegetable) {
//                        i.amount = i.amount + amount
//                    }
//                }
//            } else {
//                productList.add(SelectedProduct(vegetable, amount))
//            }
//            updateView()
//            bottomSheetDialog.dismiss()
//        }
//        bottomSheetDialog.findViewById<Button>(R.id.btn_add_cart)?.setOnClickListener {
//            val shoppingCart = ShoppingCart(
//                vegetable.idVegetable,
//                vegetable.nameVegetable.toString(), amount, vegetable.price
//            )
//            viewModel.customer.observe(viewLifecycleOwner, Observer<UserRespone> {
//                addCart(shoppingCart, it.id)
//                bottomSheetDialog.dismiss()
//            })
//
//        }
    }

    /**
     *Tìm kiếm sản phẩm khi nhập từ khóa ở toolbar
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun searchProduct(view: View) {
        val txtSearch = view.findViewById<EditText>(R.id.etSearch)
        txtSearch?.doAfterTextChanged {
            updateList(txtSearch.text.toString())
        }
    }

    /**
     *Cập nhật lại danh sách sản phẩm khi tìm kiếm
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun updateList(strSearch: String) {
        val productSearch = mutableListOf<Product>()
        for (i in products!!) {
            if (i.product_name?.lowercase()?.contains(strSearch.lowercase())!!) {
                productSearch.add(i)
            }
        }
        adapterProduct =
            AdapterProduct(productSearch as ArrayList<Product>) { showBottomDialog(it) }
        binding.rcvProduct.adapter = adapterProduct
    }

    /**
     *Xóa hết các sản phẩm có trong danh sách chọn mua
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun clearProduct() {
        binding.ivRefresh.setOnClickListener {
            binding.tvProductAmount.let {
                it.text = "0"
                it.setTextColor(Color.BLACK)
                it.setBackgroundResource(R.drawable.border_left_corner)
            }
            binding.tvTotalPrice.let {
                it.text = ""
                it.setTextColor(Color.BLACK)
                it.setBackgroundResource(R.drawable.border_right_corner)
            }
            binding.llRefresh.setBackgroundResource(R.drawable.border_button)
            binding.ivRefresh.setBackgroundResource(R.drawable.border_button)
            //productList.clear()
        }
    }

    /**
     *Cập nhật lại UI khi chọn sản phẩm ở bottomDialog
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    @SuppressLint("SetTextI18n")
//    private fun updateView() {
//        if (productList.size > 0) {
//            binding.tvProductAmount.let { view ->
//                view.text = productList.sumOf { it.amount }.toString()
//                view.setTextColor(Color.WHITE)
//                view.setBackgroundResource(R.drawable.textview_amount_border)
//            }
//            binding.tvTotalPrice.let { view ->
//                view.text =
//                    context?.getText(R.string.all).toString() + " " + decimalFormat.format(
//                        productList.sumOf { it.amount * it.product.price.toDouble() }
//                    )
//                        .toString()
//                view.setTextColor(Color.WHITE)
//                view.setBackgroundResource(R.drawable.textview_totalprice_border)
//            }
//            binding.ivRefresh.setBackgroundResource(R.drawable.linearlayout_refresh_border)
//            binding.llRefresh.setBackgroundResource(R.drawable.linearlayout_refresh_border)
//        }
//    }

    /**
     *Mở navigationView ở phía bên trái của màn hình
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun openDrawerLayoutMenu() {
        binding.ibMenu.setOnClickListener {
            (activity as MainActivity).openDrawerLayout()
        }
    }

    /**
     *Thiết lập điều hướng khi chọn các item của NavigationView
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun setUpNavigation() {
        val navigationView = (activity as MainActivity).findViewById<NavigationView>(R.id.nv_menu)
        val drawerLayout = (activity as MainActivity).findViewById<DrawerLayout>(R.id.dlLeft)
        navigationView?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mnBill -> {
                    findNavController().navigate(
                        R.id.action_saleFragment_to_listBillsFragment
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.mnShoppingCart -> {
                    findNavController().navigate(
                        R.id.action_saleFragment_to_shoppingCartFragment
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.mnLogOut -> {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    (activity as MainActivity).startActivity(intent)
                }
            }
            true
        }
    }

    /**
     *Chuyển sang BillFragment
     *@author:NCPhuc
     *@date:3/16/2022
     **/
//    private fun showBillFragment() {
//        val drawerLayout = (activity as MainActivity).findViewById<DrawerLayout>(R.id.dlLeft)
//        binding.tvProductAmount.setOnClickListener {
//            drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
//            if (productList.size > 0) {
//                findNavController().navigate(
//                    R.id.action_saleFragment_to_billDetailFragment,
//                    bundleOf("product" to productList)
//                )
//            }
//        }
//        binding.tvTotalPrice.setOnClickListener {
//            drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
//            if (productList.size > 0) {
//                findNavController().navigate(
//                    R.id.action_saleFragment_to_billDetailFragment,
//                    bundleOf("product" to productList)
//                )
//            }
//        }
//    }

    /**
     *Kiểm tra xem sản phẩm chọn mới đã có sản phẩm tương tự trong danh sách hay chưa
     *@author:NCPhuc
     *@date:3/18/2022
     **/
//    private fun checkSelectedProduct(id: Int): Boolean {
//        var isOK = false
//        for (i in productList) {
//            if (i.product.id == id) {
//                isOK = true
//            }
//        }
//        return isOK
//    }

    /**
     *Thiết lập filter
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    @SuppressLint("RtlHardcoded")
    private fun configFilter(view: View) {
        binding.dlFilter.let {
            it.setScrimColor(Color.TRANSPARENT)
            it.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        }
        view.findViewById<ImageButton>(R.id.imbFilter)?.setOnClickListener {
            getListCategory(view)
            binding.dlFilter.openDrawer(Gravity.RIGHT)
        }
        view.findViewById<ImageButton>(R.id.imbCart)?.setOnClickListener {
            findNavController().navigate(R.id.action_saleFragment_to_shoppingCartFragment)
        }
        val btnSave = view.findViewById<Button>(R.id.btnDone)
        val btnClear = view.findViewById<Button>(R.id.btnClearnFilter)
        btnSave.setOnClickListener {
            filterProduct(getFilter(view))
            binding.dlFilter.closeDrawer(Gravity.RIGHT)
        }
        btnClear.setOnClickListener {
            view.findViewById<Spinner>(R.id.spnColor)?.setSelection(categories?.size!! - 1)
            view.findViewById<RadioButton>(R.id.rbName)?.isChecked = true
        }
    }

    /**
     *Nhận các giá trị để thực hiện filter
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    private fun getFilter(view: View): FilterProduct {
        val selectRadioButton = view.findViewById<RadioGroup>(R.id.rgSortby).checkedRadioButtonId
        val radioButtonText = selectRadioButton.let { view.findViewById<RadioButton>(it)?.text }
        val spColor = view.findViewById<Spinner>(R.id.spnColor).selectedItem as Category
        return FilterProduct(radioButtonText.toString(), spColor.id)
    }

    private fun refresh() {
        binding.swipelayout.setOnRefreshListener {
            getListVegetable()
            binding.swipelayout.isRefreshing=false
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun filterProduct(filter: FilterProduct) {
        var sortList = products
        if (filter.sortBy == "Tên") {
            if (filter.category == 0) {
                sortList?.sortWith { t, t2 ->
                    Collator.getInstance(Locale("vi", "VN"))
                        .compare(t.product_name, t2.product_name)
                }
            } else {
                sortList?.sortWith { t, t2 ->
                    Collator.getInstance(Locale("vi", "VN"))
                        .compare(t.product_name, t2.product_name)
                }
                sortList =
                    sortList?.filter { it.id == filter.category } as ArrayList<Product>?
            }
        } else {
            if (filter.category == 0) {
                sortList?.sortWith(compareBy(Product::price))
            } else {
                sortList?.sortWith(compareBy(Product::price))
                sortList =
                    sortList?.filter { it.id == filter.category } as ArrayList<Product>?
            }
        }
        adapterProduct?.items = sortList!!
        adapterProduct?.notifyDataSetChanged()
    }

    private fun getListVegetable() {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(IO).launch {
            try {
                val response = api.getListProduct()
                if (response.isSuccessful && response.body() != null) {
                    withContext(Main) {
                        products = response.body() as ArrayList<Product>?
                        products?.let { setUpView(it) }
                    }
                } else {
                    activity?.showToast("Error : ${response.errorBody()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getListCategory(view: View) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(IO).launch {
            try {
                val response = api.getListCate()
                if (response.isSuccessful && response.body() != null) {
                    withContext(Main) {
                        categories = response.body() as ArrayList<Category>
                        categories?.let { setUpSpinner(view) }
                    }
                } else {
                    activity?.showToast("Error : ${response.errorBody()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addCart(shoppingCart: ShoppingCart, idU: Int) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(IO).launch {
            try {
                val response = api.insertShoppingCart(shoppingCart, idU)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Main) {
                        if (response.body()!!.id == 100) {
                            activity?.showToast("Thêm vào giỏ hàng thành công")
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

