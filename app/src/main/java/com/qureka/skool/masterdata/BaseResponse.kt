package com.qureka.skool.masterdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BaseResponse(
    val status: String? = null,
    val message: String? = null,
    val response: Response? = null,
) : Parcelable

@Parcelize
class Response(
    val id: Int? = -1,
    val singularSdkKey: String? = null,
    val appName: String? = null,
    val appPackage: String? = null,
    val authenticationKey: String? = null,
) : Parcelable