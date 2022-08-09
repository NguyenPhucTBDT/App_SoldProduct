package com.misa.fresher.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.misa.fresher.MainActivity
import com.misa.fresher.R
import com.misa.fresher.signup.SignUpActivity
import com.misa.fresher.databinding.ActivityLoginBinding
import com.misa.fresher.model.User
import com.misa.fresher.showToast
import com.misa.fresher.viewpager.UserViewModel

class LoginTest : AppCompatActivity(), LoginContract.View {
    private val binding: ActivityLoginBinding by lazy { getInflater(layoutInflater) }
    val getInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate
    private var mPresenter: LoginPresenter? = null
    private var viewModel : UserViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        initPresenter()
        binding.btnLogin.setOnClickListener {
            checkValid()
        }
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initPresenter() {
        mPresenter = LoginPresenter().also { it.attachView(this) }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun loginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun showErrorMessage(string: String) {
        application?.showToast(string)
    }

    private fun checkValid() {
        val user = User(0,binding.tietUsername.text.toString(), binding.tietPassword.text.toString(),"")
        if (binding.tietUsername.text.toString().isEmpty() || binding.tietPassword.text.toString()
                .isEmpty()
        ) {
            application.showToast(resources.getString(R.string.login_account))
        } else if (!checkForInternet(this)) {
            application.showToast(resources.getString(R.string.connect_error))
        } else {
            binding.flProgessBarSignIn.isVisible = true
            mPresenter?.signIn(user)
        }
    }

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
}