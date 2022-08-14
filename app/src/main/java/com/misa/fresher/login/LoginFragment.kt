package com.misa.fresher.login

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.misa.fresher.R
import com.misa.fresher.databinding.ActivityLoginBinding
import com.misa.fresher.model.User
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import com.misa.fresher.showToast
import com.misa.fresher.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginFragment : Fragment() {
    private val binding: ActivityLoginBinding by lazy { getInflater(layoutInflater) }
    val getInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate
    var viewModel: UserViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.btnLogin.setOnClickListener {
            signIn()
        }
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_signUpActivity
            )
        }
    }

    /**
     *Đăng nhập vào ứng dụng
     *@author:NCPhuc
     *@date:3/22/2022
     **/
    private fun signIn() {
        val user = User(
            0,
            binding.tietUsername.text.toString(),
            binding.tietPassword.text.toString(),
            "",
            "",
            ""
        )
        val resIn = ApiHelper.getInstance().create(ApiInterface::class.java)
        if (binding.tietUsername.text.toString().isEmpty() || binding.tietPassword.text.toString()
                .isEmpty()
        ) {
            activity?.showToast("Tài khoản/mật khẩu không được để trống!")
        } else if (!checkForInternet(requireContext())) {
            activity?.showToast("Không có kết nối mạng!")
        } else {
            binding.flProgessBarSignIn.isVisible = true
            CoroutineScope(IO).launch {
                try {
                    val signIn = resIn.signIn(user)
                    if (signIn.isSuccessful && signIn.body() != null) {
                        withContext(Main) {
                            val body = signIn.body()
                            if (signIn.code() == 200) {
                                findNavController().navigate(
                                    R.id.action_loginFragment_to_saleFragment, bundleOf(
                                        Pair("id", body!!.idU), Pair("name", body.fullname),Pair("phone",body.phone),
                                        Pair("email",body.email)
                                    )
                                )
                            } else {
                                activity?.showToast("Thông tin tài khoản không chính xác")
                                binding.flProgessBarSignIn.isVisible = false
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            activity?.showToast(signIn.errorBody().toString())
                            binding.flProgessBarSignIn.isVisible = false
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        activity?.showToast(e.message.toString())
                        binding.flProgessBarSignIn.isVisible = false
                    }
                }
            }
        }
    }

    /**
     *Kiểm tra kết nối mạng
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
}
