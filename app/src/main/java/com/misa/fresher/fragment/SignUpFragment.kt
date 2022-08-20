package com.misa.fresher.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.misa.fresher.databinding.FragmentSignInBinding
import com.misa.fresher.model.Messenger
import com.misa.fresher.model.User
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SignUpFragment : Fragment() {
    private val binding: FragmentSignInBinding by lazy { getInflater(layoutInflater) }
    val getInflater: (LayoutInflater) -> FragmentSignInBinding
        get() = FragmentSignInBinding::inflate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUp.setOnClickListener {
            checkValid()
        }
        binding.tvLogin.setOnClickListener {
            activity?.onBackPressed()
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
        if (user.trim().isEmpty()) {
            activity?.showToast("Tài khoản không được để trống!")
        } else if (pass.trim().isEmpty()) {
            activity?.showToast("Mật khẩu không được bỏ trống!")
        } else if (rePass.trim().isEmpty()) {
            activity?.showToast("Mật khẩu nhập lại không được bỏ trống!")
        } else if (pass.trim() != rePass.trim()) {
            activity?.showToast("Mật khẩu không khớp nhau!")
        } else if (fullName.trim().isEmpty()) {
            activity?.showToast("Họ và tên không được bỏ trống!")
        } else if (!checkForInternet(requireContext())) {
            activity?.showToast("Không có kết nối mạng")
        } else {
            val userInfo = User(0, user, pass, fullName, "", "")
            binding.flProgressBar.visibility = View.VISIBLE
            signUp(userInfo)
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

    private fun signUp(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val signUp = ApiHelper.getInstance().create(ApiInterface::class.java).signUp(user)
                if (signUp.isSuccessful && signUp.body() != null) {
                    withContext(Dispatchers.Main)
                    {
                        val msg = Gson().fromJson(signUp.body(), Messenger::class.java)
                        binding.btnSignUp.visibility = View.GONE
                        activity?.showToast(msg.msg)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val msg = Gson().fromJson(
                            signUp.errorBody()!!.charStream(),
                            Messenger::class.java
                        )
                        binding.btnSignUp.visibility = View.GONE
                        activity?.showToast(msg.msg)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.btnSignUp.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        }
    }
}