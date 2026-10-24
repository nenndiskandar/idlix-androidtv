package com.iskanflix.data.api

import com.iskanflix.data.model.RedeemRequest
import com.iskanflix.data.model.RedeemResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface MajorplayApiService {

    @POST
    suspend fun redeemPlay(
        @Url url: String,
        @Header("Referer") referer: String = "https://z2.idlixku.com/",
        @Header("Origin") origin: String = "https://z2.idlixku.com",
        @Body request: RedeemRequest
    ): RedeemResponse
}
