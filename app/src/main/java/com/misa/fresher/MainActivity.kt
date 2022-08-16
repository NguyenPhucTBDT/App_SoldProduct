package com.misa.fresher


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.misa.fresher.viewmodel.UserViewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: UserViewModel ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openDrawerLayout() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.dlLeft)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawerLayout.openDrawer(GravityCompat.START)
    }
    fun initView(name : String) {
        val nav = findViewById<NavigationView>(R.id.nv_menu)
        val headerView = nav.getHeaderView(0)
        val tvFullname = headerView.findViewById<TextView>(R.id.tv_full_name)
        val tvId = headerView.findViewById<TextView>(R.id.tv_id)
        val tvLogin = headerView.findViewById<TextView>(R.id.tv_login)
        tvFullname.visibility = View.VISIBLE
        tvId.visibility = View.GONE
        tvFullname.text = "Xin chào, $name"
        tvLogin.visibility = View.GONE
    }
    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || findNavController(R.id.fragmentContainerView).navigateUp()
    }
}
