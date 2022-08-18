package com.misa.fresher


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.misa.fresher.databinding.ActivityMainBinding
import com.misa.fresher.model.User
import com.misa.fresher.viewmodel.UserViewModel


class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: UserViewModel by lazy { ViewModelProvider(this)[UserViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogOut.visibility = View.GONE
    }

    fun openDrawerLayout() {
        binding.dlLeft.run {
            this.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            this.openDrawer(GravityCompat.START)
        }
    }

    fun logOut() {
        binding.btnLogOut.setOnClickListener {
            val user = User(0, "", "", "", "", "")
            viewModel.addUser(user)
            val nav = binding.nvMenu
            val headerView = nav.getHeaderView(0)
            val tvFullname = headerView.findViewById<TextView>(R.id.tv_full_name)
            val tvLogin = headerView.findViewById<TextView>(R.id.tv_login)
            tvFullname.visibility = View.GONE
            tvLogin.visibility = View.VISIBLE
            binding.btnLogOut.visibility = View.GONE
            binding.dlLeft.run {
                this.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                this.closeDrawer(GravityCompat.START)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun initView(name: String) {
        val nav = binding.nvMenu
        val headerView = nav.getHeaderView(0)
        val tvFullname = headerView.findViewById<TextView>(R.id.tv_full_name)
        val tvLogin = headerView.findViewById<TextView>(R.id.tv_login)
        tvFullname.visibility = View.VISIBLE
        tvFullname.text = "Xin chào, $name"
        tvLogin.visibility = View.GONE
        binding.btnLogOut.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || findNavController(R.id.fragmentContainerView).navigateUp()
    }
}
