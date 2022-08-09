package com.misa.fresher.login

import com.misa.fresher.model.User
import com.misa.fresher.retrofit.ApiHelper
import com.misa.fresher.retrofit.ApiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginPresenter : LoginContract.Presenter {
    private var mView: LoginContract.View? = null
    override fun attachView(view: LoginContract.View) {
        this.mView = view
    }

    override fun detachView() {
        this.mView = null
    }

    override fun signIn(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val signIn = ApiHelper.getInstance().create(ApiInterface::class.java).signIn(user)
                if (signIn.isSuccessful && signIn.body() != null) {
                    withContext(Main) { mView?.loginSuccess() }
                } else {
                    withContext(Dispatchers.Main) {
                        mView?.showErrorMessage(signIn.errorBody().toString())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mView?.showErrorMessage(e.printStackTrace().toString())
                }
            }
        }
    }
}
