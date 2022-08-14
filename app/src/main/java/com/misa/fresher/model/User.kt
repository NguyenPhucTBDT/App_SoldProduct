package com.misa.fresher.model

data class User(
    val idU : Int,
    val account: String,
    val password: String,
    val fullname: String,
    val phone: String,
    val email: String,
)
{
}