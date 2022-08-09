package com.misa.fresher.login

import com.misa.fresher.model.User

class LoginContract {
    interface View {
        fun loginSuccess()
        fun showErrorMessage(string: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun signIn(user: User)
    }
}
