package com.misa.fresher.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.misa.fresher.model.ShipInfor

class PaymentViewModel :ViewModel(){
        private var shipInfor=MutableLiveData<Int>()
        val shipItem:LiveData<Int> get() = shipInfor
        fun add(method: Int)
        {
                shipInfor.value=method
                shipInfor.postValue(shipInfor.value)
        }
}