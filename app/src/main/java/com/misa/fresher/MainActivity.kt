package com.misa.fresher


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.misa.fresher.model.UserRespone
import com.misa.fresher.viewpager.UserViewModel


class MainActivity : AppCompatActivity() {
    private var viewModel : UserViewModel ? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val bundle = intent.extras
        val id = bundle?.getInt("idU")
        val fullname = bundle?.getString("fullname")
        val account = bundle?.getString("account")
        val password = bundle?.getString("password")
        viewModel?.addUser(UserRespone(id!!.toInt(),account.toString(),password.toString(),fullname.toString()))
        val nav = findViewById<NavigationView>(R.id.nv_menu)
        val headerView = nav.getHeaderView(0)
        val tvFullname = headerView.findViewById<TextView>(R.id.tv_full_name)
        val tvId = headerView.findViewById<TextView>(R.id.tv_id)
        tvFullname.text = fullname.toString()
        tvId.text = "ID : $id"
    }

    fun openDrawerLayout() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.dlLeft)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || findNavController(R.id.fragmentContainerView).navigateUp()
    }
}
