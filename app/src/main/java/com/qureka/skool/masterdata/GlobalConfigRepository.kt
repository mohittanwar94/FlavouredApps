package com.qureka.skool.masterdata

import com.qureka.skool.network.ApiCallHandler
import com.qureka.skool.network.ApiClient
import com.qureka.skool.network.ApiInterface
import com.qureka.skool.network.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class GlobalConfigRepository @Inject constructor(private val apiInterface: ApiInterface) {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getGlobalConfig(): ResultWrapper<BaseResponse> {
        return ApiCallHandler.safeApiCall(dispatcher) {
            apiInterface.getGlobalConfig()
        }
    }
}