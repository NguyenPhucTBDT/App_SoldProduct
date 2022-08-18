package com.misa.fresher.signup

import com.google.gson.Gson
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
import kotlin.math.sign

class SignUpPresenter : SignUpContract.Presenter {
    private var mSignUpView: SignUpContract.View? = null
    override fun attach(view: SignUpContract.View) {
        this.mSignUpView = view
    }

    override fun detach() {
        this.mSignUpView = null
    }

    override fun signUp(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val signUp = ApiHelper.getInstance().create(ApiInterface::class.java).signUp(user)
                if (signUp.isSuccessful && signUp.body() != null) {
                    withContext(Dispatchers.Main)
                    {
                        val msg = Gson().fromJson(signUp.body(),Messenger::class.java)
                        mSignUpView?.showErrorMessage(msg.msg)
                        mSignUpView?.signUpSuccess()
                        mSignUpView?.showProgressBar()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val msg = Gson().fromJson(signUp.errorBody()!!.charStream(),Messenger::class.java)
                        mSignUpView?.showProgressBar()
                        mSignUpView?.showErrorMessage(msg.msg)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mSignUpView?.showProgressBar()
                    mSignUpView?.showErrorMessage(e.printStackTrace().toString())
                }
            }
        }
    }
}