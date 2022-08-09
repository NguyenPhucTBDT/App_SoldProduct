package com.misa.fresher.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.misa.fresher.BillViewModel
import com.misa.fresher.MainActivity
import com.misa.fresher.R
import com.misa.fresher.adapter.ListBillAdapter
import com.misa.fresher.data.bill.ImplBillDAO
import com.misa.fresher.databinding.FragmentListBillsBinding
import com.misa.fresher.login.LoginActivity
import com.misa.fresher.model.BillInfor
import com.misa.fresher.model.Invoice
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
import java.text.DecimalFormat

class ListBillsFragment : Fragment() {
    private val binding: FragmentListBillsBinding by lazy {
        FragmentListBillsBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: UserViewModel by activityViewModels()
    private val decimalFormat = DecimalFormat("0,000.0")
    var mListInvoice = arrayListOf<Invoice>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customer.observe(viewLifecycleOwner, Observer<UserRespone> {
            getListInvoice(it.id)
        })
        setUpView()
        openDrawerLayoutMenu(view)
        showSaleFragment()
    }

    /**
     *Thiết lập dữ liệu cho Spinner
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    private fun setUpView() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spnDay,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnDay.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spnPayment,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnPayment.adapter = adapter
        }
    }

    /**
     *Thiết lập lấy danh sách hóa đơn từ SQLite lên RecycleView
     *@author:NCPhuc
     *@date:3/25/2022
     **/
    private fun setUpRecycleView() {
        val adapter = ListBillAdapter(mListInvoice) { gotoOderDetail(it) }
        binding.rcvBill.adapter = adapter
        binding.rcvBill.layoutManager = LinearLayoutManager(requireContext())
        binding.tvListBillSize.text = mListInvoice.size.toString()
        binding.tvListBillAmount.text =
            decimalFormat.format(mListInvoice.sumOf { it.amount.toDouble() }).toString()
    }

    /**
     *Mở Drawer
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    private fun openDrawerLayoutMenu(view: View) {
        binding.ibMenuLb.setOnClickListener {
            (activity as MainActivity).openDrawerLayout()
        }
    }

    /**
     *Hiển thị SaleFragment
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    private fun showSaleFragment() {
        val navigationView = (activity as MainActivity).findViewById<NavigationView>(R.id.nv_menu)
        val drawerLayout = (activity as MainActivity).findViewById<DrawerLayout>(R.id.dlLeft)
        navigationView?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mnSale -> {
                    activity?.onBackPressed()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.mnLogOut -> {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    (activity as MainActivity).startActivity(intent)
                }
                R.id.mnShoppingCart -> {
                    findNavController().navigate(
                        R.id.action_listBillsFragment_to_shoppingCartFragment
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
    }

    /**
     *Lấy danh sách hóa đơn
     *@author:NCPhuc
     *@date:3/18/2022
     **/
    private fun getListInvoice(id: Int) {
        val api = ApiHelper.getInstance().create(ApiInterface::class.java)
        CoroutineScope(IO).launch {
            try {
                val response = api.getListOder(id)
                if (response.isSuccessful && response.body() != null) {
                    withContext(Main) {
                        mListInvoice = response.body() as ArrayList<Invoice>
                        setUpRecycleView()
                    }
                } else {
                    Log.e("errr", response.errorBody().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun gotoOderDetail(invoice: Invoice) {
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(
                R.id.action_listBillsFragment_to_oderDetailFragment,
                bundleOf(
                    Pair("idI", invoice.id),
                    Pair("address", invoice.address),
                    Pair("phone", invoice.phone),
                    Pair("name", it.fullname)
                )
            )
        })
    }
}
