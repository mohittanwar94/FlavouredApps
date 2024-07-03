package com.qureka.skool.fgp.repository

import com.qureka.skool.fgp.model.FirstGamePlay
import com.qureka.skool.fgp.model.FirstGamePlayRequest
import com.qureka.skool.network.ApiCallHandler
import com.qureka.skool.network.ApiClient
import com.qureka.skool.network.ApiInterface
import com.qureka.skool.network.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class FirstGamePlayRepository @Inject constructor(private val apiInterface: ApiInterface) {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO


    suspend fun sendFirstGamePlay(
        firstGamePlayRequest: FirstGamePlayRequest,
    ): ResultWrapper<FirstGamePlay> {
        return ApiCallHandler.safeApiCall(dispatcher) {
            apiInterface.sendGamePlayEvent(firstGamePlayRequest)
        }
    }
}