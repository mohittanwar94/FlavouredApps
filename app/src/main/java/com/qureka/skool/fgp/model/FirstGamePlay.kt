package com.qureka.skool.fgp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirstGamePlay(
    val status: String? = null,
    val message: String? = null,
) : Parcelable
