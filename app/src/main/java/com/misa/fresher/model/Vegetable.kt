package com.misa.fresher.model

import com.google.gson.annotations.SerializedName

class Vegetable {
    @SerializedName("idVegetable")
    var idVegetable: Int = 0

    @SerializedName("nameVegetable")
    val nameVegetable: String? = null

    @SerializedName("description")
    val description: String? = null

    @SerializedName("manufacture")
    val manufacture: String? = null

    @SerializedName("price")
    val price: Float = 0.toFloat()

    @SerializedName("imglink")
    val imgLink: String? = null

    @SerializedName("idCategory")
    val idCategory: Int = 0
}
