package com.qureka.skool.ipchecker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IpCheckerResponse(
    val status: String? = null,
    val message: String? = null,
) : Parcelable