package com.qureka.skool

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdInfo(
    var airHornUnlock: Boolean? = false,
    var carHornUnlock: Boolean? = false,
    var policeHornUnlock: Boolean? = false,
    var sneezingUnlock: Boolean? = false,
    var partyUnlock: Boolean? = false,
    var fartUnlock: Boolean? = false,
    var schoolUnlock: Boolean? = false,
    var whistleUnlock: Boolean? = false,
    var coughingUnlock: Boolean? = false,
) : Parcelable
