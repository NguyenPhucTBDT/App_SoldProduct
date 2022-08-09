package com.misa.fresher.retrofit

import com.misa.fresher.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST("WSProduct/api/user/login")
    suspend fun signIn(@Body user: User): Response<UserRespone>

    @POST("WSProduct/api/user/login")
    suspend fun signUp(@Body user: User): Response<Messenger>

    @GET("WSProduct/api/vet/all")
    suspend fun getListVet(): Response<List<Vegetable>>

    @GET("WSProduct/api/cate/all")
    suspend fun getListCate(): Response<List<Category>>

    @Headers("Content-Type: application/json")
    @POST("WSProduct/api/cart/buy")
    suspend fun oderProduct(@Body cart: Cart): Response<Messenger>

    @GET("WSProduct/api/shopping/all/{idU}")
    suspend fun getShoppingCart(@Path("idU") id: Int): Response<List<ShoppingCart>>

    @Headers("Content-Type: application/json")
    @POST("WSProduct/api/shopping/insert/{idU}")
    suspend fun insertShoppingCart(
        @Body shoppingCart: ShoppingCart,
        @Path("idU") idU: Int
    ): Response<Messenger>

    @GET("WSProduct/api/shopping/deleteAll/{idU}")
    suspend fun deleteAll(@Path("idU") id: Int): Response<Messenger>

    @GET("WSProduct/api/cart/list-invoice/{idU}")
    suspend fun getListOder(@Path("idU") id: Int): Response<List<Invoice>>

    @GET("WSProduct/api/user/address/{idU}")
    suspend fun getListAddress(@Path("idU") id: Int): Response<List<AddressUser>>
    @GET("WSProduct/api/cart/list-invoice-detail/{idI}")
    suspend fun getOderDetailById(@Path("idI") id: Int): Response<List<InvoiceDetail>>
}
