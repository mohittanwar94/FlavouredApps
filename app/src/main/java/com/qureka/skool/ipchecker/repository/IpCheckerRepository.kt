package com.qureka.skool.ipchecker.repository

import com.qureka.skool.ipchecker.model.ConsentRequest
import com.qureka.skool.ipchecker.model.IpCheckerResponse
import com.qureka.skool.network.ApiCallHandler
import com.qureka.skool.network.ApiInterface
import com.qureka.skool.network.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class IpCheckerRepository @Inject constructor(private val apiInterface: ApiInterface) {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getIpCheckForBlockCountry(
        countryIp: String? = null,
    ): ResultWrapper<IpCheckerResponse> {
        return ApiCallHandler.safeApiCall(dispatcher) {
            apiInterface.getIpCheckForBlockCountry(countryIp)
        }
    }

    suspend fun updateConsentStatus(
        countryIp: ConsentRequest? = null,
    ): ResultWrapper<IpCheckerResponse> {
        return ApiCallHandler.safeApiCall(dispatcher) {
            apiInterface.updateConsentStatus(countryIp)
        }
    }
}