package com.qureka.skool.network

import com.qureka.skool.fgp.model.FirstGamePlay
import com.qureka.skool.fgp.model.FirstGamePlayRequest
import com.qureka.skool.ipchecker.model.ConsentRequest
import com.qureka.skool.ipchecker.model.IpCheckerResponse
import com.qureka.skool.masterdata.BaseResponse
import retrofit2.http.*

interface ApiInterface {
    @POST("/api/v1/app/info")
    suspend fun sendGamePlayEvent(@Body body: FirstGamePlayRequest): FirstGamePlay

    @GET("/api/v1/globaldata")
    suspend fun getGlobalConfig(): BaseResponse

    @GET("/api/v1/microapp/validate-allow-country")
    suspend fun getIpCheckForBlockCountry(
        @Query("ip") countryIp: String? = null,
    ): IpCheckerResponse

    @POST("/api/v1/opt/status")
    suspend fun updateConsentStatus(
        @Body consent: ConsentRequest? = null,
    ): IpCheckerResponse
}

