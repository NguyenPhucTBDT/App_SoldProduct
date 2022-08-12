package com.misa.fresher.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.misa.fresher.model.AddressUser
import com.misa.fresher.model.Invoice
import com.misa.fresher.model.User
import com.misa.fresher.model.UserRespone

class UserViewModel : ViewModel() {
    private val user = MutableLiveData<UserRespone>()
    val customer: LiveData<UserRespone> get() = user
    private val address = MutableLiveData<AddressUser>()
    val addressUser: LiveData<AddressUser> get() = address
    private val invoice = MutableLiveData<Invoice>()
    val invoices : LiveData<Invoice> get() = invoice
    fun addUser(a: UserRespone) {
        user.value = a
        user.postValue(user.value)
    }
    fun addAddress(a: AddressUser) {
        address.value = a
        address.postValue(address.value)
    }
    fun addInvoice(a : Invoice) {
        invoice.value = a
        invoice.postValue(invoice.value)
    }
    fun removeUser() {
        user.value = null
        user.postValue(user.value)
    }
}