package com.misa.fresher.signup

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.misa.fresher.databinding.ActivitySignUpBinding
import com.misa.fresher.login.LoginActivity
import com.misa.fresher.login.LoginPresenter
import com.misa.fresher.login.LoginTest
import com.misa.fresher.model.User
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import kotlinx.coroutines.*
import java.lang.Exception

class SignUpActivity : AppCompatActivity(), SignUpContract.View {
    private val binding: ActivitySignUpBinding by lazy { getInflater(layoutInflater) }
    val getInflater: (LayoutInflater) -> ActivitySignUpBinding
        get() = ActivitySignUpBinding::inflate
    private var mSignUpPresenter: SignUpPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initPresenter()
        binding.btnSignUp.setOnClickListener {
            checkValid()
        }
        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     *Đăng ký tài khoản mới
     *@author:NCPhuc
     *@date:3/22/2022
     **/
    private fun checkValid() {
        val user = binding.tietSignUpUserName.text.toString()
        val pass = binding.tietSignUpPassword.text.toString()
        val rePass = binding.tietReEnterPassword.text.toString()
        val fullName = binding.tietFullName.text.toString()
        if (user.isEmpty()) {
            application.showToast("Tài khoản không được để trống!")
        } else if (pass.isEmpty()) {
            application.showToast("Mật khẩu không được bỏ trống!")
        } else if (rePass.isEmpty()) {
            application.showToast("Mật khẩu nhập lại không được bỏ trống!")
        } else if (pass != rePass) {
            application.showToast("Mật khẩu không khớp nhau!")
        } else if (fullName.isEmpty()) {
            application.showToast("Họ và tên không được bỏ trống!")
        }
        else if (!checkForInternet(this)) {
            application.showToast("Không có kết nối mạng")
        } else {
            val userInfor = User(0,user, pass,fullName)
            binding.flProgressBar.isVisible = true
            mSignUpPresenter?.signUp(userInfor)
        }
    }

    /**
     *Kiểm tra kết nối Internet
     *@author:NCPhuc
     *@date:3/22/2022
     **/
    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION") return networkInfo.isConnected
        }

    }

    private fun initPresenter() {
        mSignUpPresenter = SignUpPresenter().also { it.attach(this) }
    }

    override fun signUpSuccess() {
        val intent = Intent(this, LoginTest::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun showProgressBar() {
        binding.flProgressBar.isGone = true
    }

    override fun showErrorMessage(string: String) {
        application?.showToast(string)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSignUpPresenter?.detach()
    }
}