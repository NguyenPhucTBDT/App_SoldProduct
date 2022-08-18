package com.misa.fresher.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.misa.fresher.R
import com.misa.fresher.databinding.FragmentLoginBinding
import com.misa.fresher.model.Messenger
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

class LoginFragment : Fragment() {
    private val binding: FragmentLoginBinding by lazy { getInflater(layoutInflater) }
    val getInflater: (LayoutInflater) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate
    val viewModel: UserViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            binding.tietUsername.text.toString().trim(),
            binding.tietPassword.text.toString().trim(),
            "",
            "",
            ""
        )
        val resIn = ApiHelper.getInstance().create(ApiInterface::class.java)
        if (binding.tietUsername.text.toString().trim().isEmpty() || binding.tietPassword.text.toString().trim()
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
                            val user = Gson().fromJson(body, User::class.java)
                            viewModel.addUser(user)
                            activity?.showToast("Đăng nhập thành công")
                            activity?.onBackPressed()
                        }
                    } else {
                        withContext(Main) {
                            val code = signIn.code()
                            if(code == 404) {
                                val messenger = Gson().fromJson(
                                    signIn.errorBody()!!.charStream(),
                                    Messenger::class.java
                                )
                                activity?.showToast(messenger.msg)
                                binding.flProgessBarSignIn.isVisible = false
                            }
                            else {
                                activity?.showToast("Có lỗi xảy ra, vui lòng thử lại")
                                binding.flProgessBarSignIn.isVisible = false
                            }
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
