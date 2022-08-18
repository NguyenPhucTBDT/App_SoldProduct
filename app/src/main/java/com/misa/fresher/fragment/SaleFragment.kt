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
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.misa.fresher.MainActivity
import com.misa.fresher.R
import com.misa.fresher.adapter.AdapterCate
import com.misa.fresher.adapter.AdapterProduct
import com.misa.fresher.databinding.FragmentSaleBinding
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
import java.util.*
import kotlin.collections.ArrayList

class SaleFragment : Fragment() {
    private val binding: FragmentSaleBinding by lazy { FragmentSaleBinding.inflate(layoutInflater) }
    var products: ArrayList<Product>? = null
    var idU: Int = 0
    val viewModel: UserViewModel by activityViewModels()
    private var categories: ArrayList<Category>? = null
    var adapterProduct: AdapterProduct? = null
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
        //searchProduct(view)
        configFilter(view)
        getListVegetable()
        refresh()
        initView()
    }

    /**
     *Lấy danh sách sản phẩm phẩm để hiện thị lên RecycleView
     *@author:NCPhuc
     *@date:3/16/2022
     **/
    private fun setUpView(vet: List<Product>) {
        adapterProduct =
            AdapterProduct(vet as ArrayList<Product>) {
                showBottomDialog(it)
            }
        binding.rcvProduct.adapter = adapterProduct
        binding.rcvProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        setUpNavigation()
        openDrawerLayoutMenu()

    }

    private fun initView() {
        viewModel.customer.observe(viewLifecycleOwner) {
            if (it.idU != 0) {
                idU = it.idU
                (activity as MainActivity).initView(it.fullname)
            } else {
                idU = 0
            }
        }
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
        findNavController().navigate(
            R.id.action_saleFragment_to_productDetailFragment,
            bundleOf(Pair("product_id", product.product_id))
        )
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
            if (i.product_name.lowercase().contains(strSearch.lowercase())) {
                productSearch.add(i)
            }
        }
        adapterProduct =
            AdapterProduct(productSearch as ArrayList<Product>) { showBottomDialog(it) }
        binding.rcvProduct.adapter = adapterProduct
    }

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
        val headerView = navigationView.getHeaderView(0)
        val tvLogin = headerView.findViewById<TextView>(R.id.tv_login)
        tvLogin.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.action_saleFragment_to_loginFragment)
        }
        navigationView?.setNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.mnBill -> {
                    if (idU != 0) {
                        findNavController().navigate(
                            R.id.action_saleFragment_to_listBillsFragment
                        )
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        activity?.showToast("Vui lòng đăng nhập")
                    }
                }
                R.id.mnShoppingCart -> {
                    if (idU != 0) {
                        findNavController().navigate(
                            R.id.action_saleFragment_to_shoppingCartFragment
                        )
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        activity?.showToast("Vui lòng đăng nhập")
                    }
                }
                R.id.mnAddress -> {
                    if (idU != 0) {
                        findNavController().navigate(
                            R.id.action_saleFragment_to_addressUserFragment,
                            bundleOf(Pair("type", 1))
                        )
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        activity?.showToast("Vui lòng đăng nhập")
                    }
                }
            }
            true
        }
        (activity as MainActivity).logOut()
    }

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
            if (idU == 0) {
                activity?.showToast("Vui lòng đăng nhập")
            } else {
                findNavController().navigate(R.id.action_saleFragment_to_shoppingCartFragment)
            }
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
            binding.swipelayout.isRefreshing = false
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

}

