package com.qureka.skool.fgp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirstGamePlayRequest(
    var device_id: String? = null,
    var ga_id: String? = null,
    var package_id: String? = null,
    var signature: String? = "ghjgjhghj",
) : Parcelable
