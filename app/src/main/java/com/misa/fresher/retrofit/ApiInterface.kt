package com.misa.fresher.retrofit

import com.google.gson.JsonElement
import com.misa.fresher.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST("WSLibrary/api/user/signin")
    suspend fun signIn(@Body user: User): Response<JsonElement>

    @POST("WSLibrary/api/user/signup")
    suspend fun signUp(@Body user: User): Response<JsonElement>

    @GET("WSLibrary/api/user/info/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>

    @GET("WSLibrary/api/product/product_detail/{idP}")
    suspend fun getProductDetail(@Path("idP") idP: Int): Response<JsonElement>

    @GET("WSLibrary/api/product/all")
    suspend fun getListProduct(): Response<JsonElement>

    @GET("WSLibrary/api/category/all")
    suspend fun getListCate(): Response<List<Category>>

    @Headers("Content-Type: application/json")
    @POST("WSLibrary/api/order/buy")
    suspend fun oderProduct(@Body oderInfo: OrderInfo): Response<JsonElement>

    @GET("WSLibrary/api/cart/all/{idU}")
    suspend fun getShoppingCart(@Path("idU") id: Int): Response<List<Cart>>

    @Headers("Content-Type: application/json")
    @POST("WSLibrary/api/cart/insert")
    suspend fun insertShoppingCart(
        @Body cart: Cart
    ): Response<JsonElement>

    @DELETE("WSLibrary/api/cart/delete/{idP}/{idU}")
    suspend fun deleteByID(@Path("idP") idP: Int, @Path("idU") id: Int): Response<JsonElement>

    @GET("WSLibrary/api/order/list-order/{idU}")
    suspend fun getListOder(@Path("idU") id: Int): Response<JsonElement>

    @GET("WSLibrary/api/address/all/{idU}")
    suspend fun getListAddress(@Path("idU") id: Int): Response<JsonElement>

    @GET("WSLibrary/api/order/list-order-detail/{idO}")
    suspend fun getOderDetailById(@Path("idO") id: Int): Response<JsonElement>

    @PUT("WSLibrary/api/user/update")
    suspend fun updateUserInfo(@Body user: User): Response<JsonElement>

    @DELETE("WSLibrary/api/address/delete/{idA}")
    suspend fun deleteAddress(@Path("idA") idA : Int) : Response<JsonElement>

    @POST("WSLibrary/api/address/insert")
    suspend fun insertAddress(@Body address: Address) : Response<JsonElement>

    @PUT("WSLibrary/api/address/update")
    suspend fun updateAddress(@Body address: Address) : Response<JsonElement>
}
