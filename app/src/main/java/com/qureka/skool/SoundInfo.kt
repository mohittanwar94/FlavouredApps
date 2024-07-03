package com.qureka.skool

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SoundInfo(
    val nameSound: String? = null,
    val soundImageName: String,
    val position: Int? = 0,
    var isUnlock: Int = 0,
) : Parcelable
