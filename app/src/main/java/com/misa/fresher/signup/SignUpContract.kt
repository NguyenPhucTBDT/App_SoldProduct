package com.misa.fresher.signup

import com.misa.fresher.model.User

class SignUpContract {
    interface View{
        fun signUpSuccess()
        fun showProgressBar()
        fun showErrorMessage(string: String)
    }
    interface Presenter{
        fun attach(view:View)
        fun detach()
        fun signUp(user: User)
    }
}