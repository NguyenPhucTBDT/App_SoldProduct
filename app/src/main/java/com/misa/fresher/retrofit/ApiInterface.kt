package com.misa.fresher.retrofit

import com.misa.fresher.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST("WSLibrary/api/user/login")
    suspend fun signIn(@Body user: User): Response<User>

    @POST("WSLibrary/api/user/signup")
    suspend fun signUp(@Body user: User): Response<Messenger>

    @GET("WSLibrary/api/user/infor/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>

    @GET("WSLibrary/api/product/product_detail/{idP}")
    suspend fun getProductDetail(@Path("idP") idP: Int): Response<Product_Detail>

    @GET("WSLibrary/api/product/all")
    suspend fun getListProduct(): Response<List<Product>>

    @GET("WSLibrary/api/category/all")
    suspend fun getListCate(): Response<List<Category>>

    @Headers("Content-Type: application/json")
    @POST("WSProduct/api/cart/buy")
    suspend fun oderProduct(@Body cart: Cart): Response<Messenger>

    @GET("WSLibrary/api/cart/all/{idU}")
    suspend fun getShoppingCart(@Path("idU") id: Int): Response<List<Cart>>

    @Headers("Content-Type: application/json")
    @POST("WSLibrary/api/cart/insert/{idU}")
    suspend fun insertShoppingCart(
        @Body cart: Cart,
        @Path("idU") idU: Int
    ): Response<Messenger>

    @GET("WSLibrary/api/cart/delete/{idP}&{idU}")
    suspend fun deleteByID(@Path("idP") idP: Int, @Path("idU") id: Int): Response<Messenger>

    @GET("WSProduct/api/cart/list-invoice/{idU}")
    suspend fun getListOder(@Path("idU") id: Int): Response<List<Invoice>>

    @GET("WSProduct/api/user/address/{idU}")
    suspend fun getListAddress(@Path("idU") id: Int): Response<List<AddressUser>>

    @GET("WSProduct/api/cart/list-invoice-detail/{idI}")
    suspend fun getOderDetailById(@Path("idI") id: Int): Response<List<InvoiceDetail>>
}
