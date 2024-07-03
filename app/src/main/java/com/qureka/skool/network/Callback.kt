package com.qureka.skool.network

import retrofit2.Call
import retrofit2.Response
import java.net.SocketTimeoutException

abstract class Callback<T> : retrofit2.Callback<T> {

    var SOME_THING_WENT_WRONG = "Something went wrong!"

    fun Callback() {

    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.code() == 200) {
            try {
                this.success(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            try {
                if (response.errorBody() == null) {
                    this.failure(response.body()!!.toString(), response.code())
                } else
                    this.failure(response.errorBody()!!.string(), response.code())
            } catch (e: Exception) {
                try {
                    this.failure(SOME_THING_WENT_WRONG, 0)
                } catch (exx: Exception) {
                    exx.printStackTrace()
                }

            }

        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        try {
            if (t is SocketTimeoutException) {
                onNetworkFail(SOME_THING_WENT_WRONG)
            } else {
                onNetworkFail("No internet connection")
            }
        } catch (e: Exception) {
        } finally {
            onNetworkFail("no internet connection")
        }
    }

    abstract fun success(response: Response<T>)

    abstract fun failure(errorStr: String, code: Int)

    abstract fun onNetworkFail(errorMessage: String)
}