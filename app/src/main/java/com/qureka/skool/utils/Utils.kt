package com.qureka.skool.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kaopiz.kprogresshud.KProgressHUD
import com.qureka.skool.AdInfo
import com.qureka.skool.BuildConfig
import com.qureka.skool.R
import com.qureka.skool.ServerConfig
import com.qureka.skool.SoundInfo
import com.qureka.skool.common.CustomTypefaceSpan
import com.qureka.skool.common.PLAYED_STATE
import com.qureka.skool.sharef.AppPreferenceManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object Utils {
    const val BOLD = 1
    const val MEDIUM = 2
    const val REGULAR = 3
    const val SEMI_BOLD = 4
    private val TAG = Utils::class.java.simpleName
    var hud: KProgressHUD? = null
    var dialog: Dialog? = null
    fun showProgBar(context: Context) {
        hud = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel(context.getString(R.string.please_wait)).setMaxProgress(100)
            .setCancellable(false).show()
        hud?.setProgress(90)
    }

    fun dismissProgress() {
        if (hud != null) {
            hud?.dismiss()
        }
    }

    fun showProgBarfull(context: Context) {
        /*val window: Window = myDialog.getWindow()
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)*/
        dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog?.setContentView(R.layout.dilog_bg)
        dialog?.show()
    }


    fun dismissProgressfull() {
        if (dialog != null) {
            dialog?.dismiss()
        }
    }

    fun getPre(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
    }

    fun getGsonParser(): Gson {
        val builder = GsonBuilder()
        return builder.create()
    }

    fun md5(mdString: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(mdString.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getAdPlatFormName(context: Context): String {
        return "AdMob"
    }

    fun getImage(context: Context, imageName: String): Drawable? {
        return ContextCompat.getDrawable(
            context, context.resources.getIdentifier(
                imageName,
                "drawable",
                context.packageName
            )
        )
    }

    fun showToast(context: Context) {
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun isInternetOn(context: Context?): Boolean {
        if (context != null) {
            try {
                // get Connectivity Manager object to check connection
                val connec =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                // Check for network connections
                if (connec != null) {
                    if (connec.activeNetworkInfo != null) {
                        if (connec.activeNetworkInfo != null && connec.activeNetworkInfo!!.state != null) {
                            try {
                                if (connec.getNetworkInfo(0)!!.state == android.net.NetworkInfo.State.CONNECTED || connec.getNetworkInfo(
                                        0
                                    )!!.state == android.net.NetworkInfo.State.CONNECTING || connec.getNetworkInfo(
                                        1
                                    )!!.state == android.net.NetworkInfo.State.CONNECTING || connec.getNetworkInfo(
                                        1
                                    )!!.state == android.net.NetworkInfo.State.CONNECTED
                                ) {


                                    return true


                                } else if (connec.getNetworkInfo(0)!!.state == android.net.NetworkInfo.State.DISCONNECTED || connec.getNetworkInfo(
                                        1
                                    )!!.state == android.net.NetworkInfo.State.DISCONNECTED
                                ) {


                                    return false
                                }
                            } catch (e: NullPointerException) {

                            } catch (e: Exception) {

                            }

                        }
                        return false
                    }
                }
                return false
            } catch (e: Exception) {

            }
        }
        return false
    }

    fun createPrivacyPolicySpan(str: String, textView: TextView, context: Context) {
        try {
            val ss = SpannableString(str)

            val mediumFont: Typeface = when (BuildConfig.APPLICATION_ID) {

                context.getString(R.string.app_30),
                context.getString(R.string.app_31),
                context.getString(R.string.app_29),
                context.getString(R.string.app_33) -> {
                    Typeface.createFromAsset(context.assets, "fonts/poppins_bold.ttf")
                }

                context.getString(R.string.app_35) -> {
                    Typeface.createFromAsset(context.assets, "fonts/poppins_bold.ttf")
                }

                context.getString(R.string.app_28) -> {
                    Typeface.createFromAsset(context.assets, "fonts/montserrat_regular.ttf")
                }

                context.getString(R.string.app_26) -> {
                    Typeface.createFromAsset(context.assets, "fonts/canava_grotesk_regular.ttf")
                }

                context.getString(R.string.app_21) -> {
                    Typeface.createFromAsset(context.assets, "fonts/calibri.ttf")
                }

                context.getString(R.string.app_19) -> {
                    Typeface.createFromAsset(context.assets, "fonts/franklin_gothic_urw_boo.ttf")
                }

                context.getString(R.string.app_22),
                context.getString(R.string.app_20),
                context.getString(R.string.app_28) -> {
                    Typeface.createFromAsset(context.assets, "fonts/montserrat_medium.ttf")
                }

                else -> {
                    Typeface.createFromAsset(context.assets, "fonts/poppins_medium.ttf")
                }
            }
            val color: Int = when (BuildConfig.APPLICATION_ID) {
                context.getString(R.string.app_6),
                context.getString(R.string.app_10),
                context.getString(R.string.app_11),
                context.getString(R.string.app_12),
                context.getString(R.string.app_13),
                context.getString(R.string.app_15),
                context.getString(R.string.app_16),
                context.getString(R.string.app_21),
                context.getString(R.string.app_23),
                context.getString(R.string.app_25),
                context.getString(R.string.app_26),
                context.getString(R.string.app_28),
                context.getString(R.string.app_30),
                context.getString(R.string.app_29),
                context.getString(R.string.app_31),
                context.getString(R.string.app_33) -> {
                    ContextCompat.getColor(context, R.color.white)
                }

                context.getString(R.string.app_1),
                context.getString(R.string.app_4) -> {
                    ContextCompat.getColor(context, R.color.greyish_brown)
                }

                context.getString(R.string.app_2),
                context.getString(R.string.app_38),
                context.getString(R.string.app_17), context.getString(R.string.app_39) -> {
                    ContextCompat.getColor(context, R.color.black)
                }

                context.getString(R.string.app_36) -> {
                    ContextCompat.getColor(context, R.color.white)
                }

                else -> {
                    ContextCompat.getColor(context, R.color.color_tncs)
                }
            }
            val typefaceSpanMediumFontTnc: TypefaceSpan = CustomTypefaceSpan(mediumFont)
            val typefaceSpanMediumFontPrivacy: TypefaceSpan = CustomTypefaceSpan(mediumFont)
            val typefaceSpanMediumFontCookies: TypefaceSpan = CustomTypefaceSpan(mediumFont)
            val privacyClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                                        ServerConfig.PRIVACY_POLICY_URL
                                    } else {
                                        ServerConfig.PRIVACY_POLICY_URL_INTL
                                    }
                                )
                            )
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val tncClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                                        ServerConfig.TERM_CONDITION_URL
                                    } else {
                                        ServerConfig.TERM_CONDITION_URL_INTL
                                    }
                                )
                            )
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val cookiesClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    if (CountyCheckAndGroupAssign.isIndiaCountry()) {
                                        ServerConfig.COOKIES_URL
                                    } else {
                                        ServerConfig.COOKIES_URL_INTL
                                    }
                                )
                            )
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val cookiesPolicy = context.getString(R.string.cookies)
            val indexCookies = str.indexOf(cookiesPolicy)
            ss.setSpan(
                cookiesClick,
                indexCookies,
                indexCookies + cookiesPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(color),
                indexCookies,
                indexCookies + cookiesPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                UnderlineSpan(),
                indexCookies,
                indexCookies + cookiesPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                typefaceSpanMediumFontCookies,
                indexCookies,
                indexCookies + cookiesPolicy.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            val privacyPolicy = context.getString(R.string.privacypolicy)
            val index = str.indexOf(privacyPolicy)
            ss.setSpan(
                privacyClick,
                index,
                index + privacyPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(color),
                index,
                index + privacyPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                UnderlineSpan(),
                index,
                index + privacyPolicy.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                typefaceSpanMediumFontPrivacy,
                index,
                index + privacyPolicy.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            val termsCondition = context.getString(R.string.termsCondition)
            val indexTerms = str.indexOf(termsCondition)
            ss.setSpan(
                tncClick,
                indexTerms,
                indexTerms + termsCondition.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(color),
                indexTerms,
                indexTerms + termsCondition.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                UnderlineSpan(),
                indexTerms,
                indexTerms + termsCondition.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                typefaceSpanMediumFontTnc,
                indexTerms,
                indexTerms + termsCondition.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            textView.text = ss
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createGettingStartedSpan(str: String, textView: TextView, context: Context) {
        try {
            val ss = SpannableString(str)
            val mediumFont = Typeface.createFromAsset(context.assets, "fonts/poppins_medium.ttf")
            val boldFont = Typeface.createFromAsset(context.assets, "fonts/poppins_bold.ttf")
            val color = ContextCompat.getColor(context, R.color.black)
            val colorOrange = ContextCompat.getColor(context, R.color.colorPrimary)
            val typefaceSpanMediumFont: TypefaceSpan = CustomTypefaceSpan(mediumFont)
            val typefaceSpanBoldFont: TypefaceSpan = CustomTypefaceSpan(boldFont)
            val turnShatteredClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            val wherePrankClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            val wherePrankMeet = context.getString(R.string.where_prank_meet)
            val indexWherePrankMeet = str.indexOf(wherePrankMeet)
            ss.setSpan(
                wherePrankClick,
                indexWherePrankMeet,
                indexWherePrankMeet + wherePrankMeet.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(colorOrange),
                indexWherePrankMeet,
                indexWherePrankMeet + wherePrankMeet.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                typefaceSpanBoldFont,
                indexWherePrankMeet,
                indexWherePrankMeet + wherePrankMeet.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )

            val turnShattered = context.getString(R.string.turn_shattered)
            val index = str.indexOf(turnShattered)
            ss.setSpan(
                turnShatteredClick,
                index,
                index + turnShattered.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(color),
                index,
                index + turnShattered.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                typefaceSpanMediumFont,
                index,
                index + turnShattered.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            textView.text = ss
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setClickAble(view: View) {
        view.isClickable = false
        Looper.myLooper()?.let { looper ->
            val handler = Handler(looper)
            handler.postDelayed({ view.isClickable = true }, 2000)
        }
    }

    fun getSoundData(dashActivity: Activity): ArrayList<SoundInfo> {
        val arrayList = ArrayList<SoundInfo>()
        when (BuildConfig.APPLICATION_ID) {
            dashActivity.getString(R.string.app_33) -> {
                val categoryAirHorn = SoundInfo(
                    dashActivity.getString(R.string.air_horn),
                    "air_horn",
                    0,
                    isUnlock(0, dashActivity)
                )
                val categoryCarHorn = SoundInfo(
                    dashActivity.getString(R.string.car),
                    "vehicle",
                    1,
                    isUnlock(1, dashActivity)
                )
                val categoryPoliceHorn = SoundInfo(
                    dashActivity.getString(R.string.police_siren),
                    "siren",
                    2,
                    isUnlock(2, dashActivity)
                )
                val categorySneezing = SoundInfo(
                    dashActivity.getString(R.string.sneezing),
                    "sneezing",
                    3,
                    isUnlock(3, dashActivity)
                )
                val categoryPartyHorn = SoundInfo(
                    dashActivity.getString(R.string.party_horn),
                    "party_horn",
                    4,
                    isUnlock(4, dashActivity)
                )
                val categoryFart = SoundInfo(
                    dashActivity.getString(R.string.fart),
                    "fart",
                    5,
                    isUnlock(5, dashActivity)
                )
                arrayList.add(categoryAirHorn)
                arrayList.add(categoryCarHorn)
                arrayList.add(categoryPoliceHorn)
                arrayList.add(categorySneezing)
                arrayList.add(categoryPartyHorn)
                arrayList.add(categoryFart)
            }

            dashActivity.getString(R.string.app_39) -> {
                val categoryAirHorn = SoundInfo(
                    dashActivity.getString(R.string.air_horn),
                    "air_horn",
                    0,
                    isUnlock(0, dashActivity)
                )
                val categoryCarHorn = SoundInfo(
                    dashActivity.getString(R.string.car),
                    "vehicle",
                    1,
                    isUnlock(1, dashActivity)
                )
                val categoryPoliceHorn = SoundInfo(
                    dashActivity.getString(R.string.police_siren),
                    "siren",
                    2,
                    isUnlock(2, dashActivity)
                )
                val categorySneezing = SoundInfo(
                    dashActivity.getString(R.string.sneezing),
                    "sneezing",
                    3,
                    isUnlock(3, dashActivity)
                )
                val categoryPartyHorn = SoundInfo(
                    dashActivity.getString(R.string.party_horn),
                    "party_horn",
                    4,
                    isUnlock(4, dashActivity)
                )
                val categoryFart = SoundInfo(
                    dashActivity.getString(R.string.fart),
                    "fart",
                    5,
                    isUnlock(5, dashActivity)
                )
                arrayList.add(categoryAirHorn)
                arrayList.add(categoryCarHorn)
                arrayList.add(categoryPoliceHorn)
                arrayList.add(categorySneezing)
                arrayList.add(categoryPartyHorn)
                arrayList.add(categoryFart)
            }

            dashActivity.getString(R.string.app_46) -> {
                val categoryAirHorn = SoundInfo(
                    dashActivity.getString(R.string.air_horn),
                    "air_horn",
                    0,
                    isUnlock(0, dashActivity)
                )
                val categoryCarHorn = SoundInfo(
                    dashActivity.getString(R.string.car),
                    "vehicle",
                    1,
                    isUnlock(1, dashActivity)
                )
                val categoryPoliceHorn = SoundInfo(
                    dashActivity.getString(R.string.police_siren),
                    "siren",
                    2,
                    isUnlock(2, dashActivity)
                )
                val categorySneezing = SoundInfo(
                    dashActivity.getString(R.string.sneezing),
                    "sneezing",
                    3,
                    isUnlock(3, dashActivity)
                )
                val categoryPartyHorn = SoundInfo(
                    dashActivity.getString(R.string.party_horn),
                    "party_horn",
                    4,
                    isUnlock(4, dashActivity)
                )
                val categoryFart = SoundInfo(
                    dashActivity.getString(R.string.fart),
                    "fart",
                    5,
                    isUnlock(5, dashActivity)
                )
                val categorySchoolBell = SoundInfo(
                    dashActivity.getString(R.string.school_bell),
                    "school_bell",
                    6,
                    isUnlock(6, dashActivity)
                )
                val categoryWhistle = SoundInfo(
                    dashActivity.getString(R.string.whistle),
                    "whistle",
                    7,
                    isUnlock(7, dashActivity)
                )
                val categoryCoughing = SoundInfo(
                    dashActivity.getString(R.string.coughing),
                    "cough",
                    8,
                    isUnlock(8, dashActivity)
                )

                arrayList.add(categoryAirHorn)
                arrayList.add(categoryCarHorn)
                arrayList.add(categoryPoliceHorn)
                arrayList.add(categorySneezing)
                arrayList.add(categoryPartyHorn)
                arrayList.add(categoryFart)
                arrayList.add(categorySchoolBell)
                arrayList.add(categoryWhistle)
                arrayList.add(categoryCoughing)
            }

            else -> {

                val categoryAirHorn = SoundInfo(
                    dashActivity.getString(R.string.air_horn),
                    "air_horn",
                    0,
                    isUnlock(0, dashActivity)
                )
                val categoryCarHorn = SoundInfo(
                    dashActivity.getString(R.string.car),
                    "vehicle",
                    1,
                    isUnlock(1, dashActivity)
                )
                val categoryPoliceHorn = SoundInfo(
                    dashActivity.getString(R.string.police_siren),
                    "siren",
                    2,
                    isUnlock(2, dashActivity)
                )
                val categorySneezing = SoundInfo(
                    dashActivity.getString(R.string.sneezing),
                    "sneezing",
                    3,
                    isUnlock(3, dashActivity)
                )
                val categoryPartyHorn = SoundInfo(
                    dashActivity.getString(R.string.party_horn),
                    "party_horn",
                    4,
                    isUnlock(4, dashActivity)
                )
                val categoryFart = SoundInfo(
                    dashActivity.getString(R.string.fart),
                    "fart",
                    5,
                    isUnlock(5, dashActivity)
                )
                val categorySchoolBell = SoundInfo(
                    dashActivity.getString(R.string.school_bell),
                    "school_bell",
                    6,
                    isUnlock(6, dashActivity)
                )
                val categoryWhistle = SoundInfo(
                    dashActivity.getString(R.string.whistle),
                    "whistle",
                    7,
                    isUnlock(7, dashActivity)
                )
                val categoryCoughing = SoundInfo(
                    dashActivity.getString(R.string.coughing),
                    "cough",
                    8,
                    isUnlock(8, dashActivity)
                )

                arrayList.add(categoryAirHorn)
                arrayList.add(categoryCarHorn)
                arrayList.add(categoryPoliceHorn)
                arrayList.add(categorySneezing)
                arrayList.add(categoryPartyHorn)
                arrayList.add(categoryFart)
                arrayList.add(categorySchoolBell)
                arrayList.add(categoryWhistle)
                arrayList.add(categoryCoughing)
            }
        }
        return arrayList
    }

    private fun isUnlock(position: Int, dashActivity: Activity): Int {
        (AppPreferenceManager.getInstanced(dashActivity)
            .getObject(PLAYED_STATE, AdInfo::class.java) as? AdInfo)?.let {
            return when (position) {
                0 -> {
                    if (it.airHornUnlock == true) 1 else 0
                }

                1 -> {
                    if (it.carHornUnlock == true) 1 else 0
                }

                2 -> {
                    if (it.policeHornUnlock == true) 1 else 0
                }

                3 -> {
                    if (it.sneezingUnlock == true) 1 else 0
                }

                4 -> {
                    if (it.partyUnlock == true) 1 else 0
                }

                5 -> {
                    if (it.fartUnlock == true) 1 else 0
                }

                6 -> {
                    if (it.schoolUnlock == true) 1 else 0
                }

                7 -> {
                    if (it.whistleUnlock == true) 1 else 0
                }

                8 -> {
                    if (it.coughingUnlock == true) 1 else 0
                }

                else -> 0
            }
        } ?: kotlin.run {
            return 0
        }
    }

    fun saveUnlock(position: Int, dashActivity: Activity) {
        (AppPreferenceManager.getInstanced(dashActivity)
            .getObject(PLAYED_STATE, AdInfo::class.java) as? AdInfo)?.let {
            when (position) {
                0 -> {
                    it.airHornUnlock = true
                }

                1 -> {
                    it.carHornUnlock = true
                }

                2 -> {
                    it.policeHornUnlock = true
                }

                3 -> {
                    it.sneezingUnlock = true
                }

                4 -> {
                    it.partyUnlock = true
                }

                5 -> {
                    it.fartUnlock = true
                }

                6 -> {
                    it.schoolUnlock = true
                }

                7 -> {
                    it.whistleUnlock = true
                }

                else -> {
                    it.coughingUnlock = true
                }
            }
            AppPreferenceManager.getInstanced(dashActivity)
                .putObject(PLAYED_STATE, it)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getGaID(context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val adInfo: AdvertisingIdClient.Info =
                AdvertisingIdClient.getAdvertisingIdInfo(context)
            val myId: String? = adInfo.id
            println("gaID Inner==============$myId")
            myId
        } catch (e: Exception) {
            e.printStackTrace()
            "N/A"
        }
    }

    fun getAdRequest(activity: Context) = AppConstant.getAdRequest(activity)

    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val encodedHash = md.digest(toByteArray(StandardCharsets.UTF_8))
        return bytesToHex(encodedHash)
    }

    private fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuilder(2 * hash.size)
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }

    fun createSpanStringWithMultyTypeface(str: String, textView: TextView, context: Context) {
        try {
            val ss = SpannableString(str)

            val TYPE_FACE_ONE: Typeface
            val TYPE_FACE_TWO: Typeface
            val COLOR_ONE: Int
            val COLOR_TWO: Int

            when (BuildConfig.APPLICATION_ID) {
                context.getString(R.string.app_24) -> {
                    COLOR_ONE = ContextCompat.getColor(context, R.color.white)
                    COLOR_TWO = ContextCompat.getColor(context, R.color.color_f2eca6)
                    TYPE_FACE_ONE =
                        Typeface.createFromAsset(context.assets, "fonts/poppins_regular.ttf")
                    TYPE_FACE_TWO =
                        Typeface.createFromAsset(context.assets, "fonts/poppins_bold.ttf")
                }

                else -> {
                    COLOR_ONE = ContextCompat.getColor(context, R.color.white)
                    COLOR_TWO = ContextCompat.getColor(context, R.color.color_f2eca6)
                    TYPE_FACE_ONE =
                        Typeface.createFromAsset(context.assets, "fonts/poppins_regular.ttf")
                    TYPE_FACE_TWO =
                        Typeface.createFromAsset(context.assets, "fonts/poppins_bold.ttf")
                }
            }
            val typefaceSpanOne: TypefaceSpan = CustomTypefaceSpan(TYPE_FACE_ONE)
            val typefaceSpanTwo: TypefaceSpan = CustomTypefaceSpan(TYPE_FACE_TWO)

            val firstClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            val twoClick = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    try {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }

            val spanStringOne = context.getString(R.string.span_string_one)
            val indexSpanStringOne = str.indexOf(spanStringOne)
            ss.setSpan(
                AbsoluteSizeSpan(context.resources.getDimension(R.dimen._14sdp).toInt(), false),
                indexSpanStringOne,
                indexSpanStringOne + spanStringOne.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                firstClick,
                indexSpanStringOne,
                indexSpanStringOne + spanStringOne.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(COLOR_ONE),
                indexSpanStringOne,
                indexSpanStringOne + spanStringOne.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            /* ss.setSpan(
                 UnderlineSpan(),
                 indexSpanStringOne,
                 indexSpanStringOne + spanStringOne.length,
                 Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
             )*/
            ss.setSpan(
                typefaceSpanOne,
                indexSpanStringOne,
                indexSpanStringOne + spanStringOne.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            val spanStringTwo = context.getString(R.string.span_string_two)
            val indexSpanStringTwo = str.indexOf(spanStringTwo)
            ss.setSpan(
                AbsoluteSizeSpan(context.resources.getDimension(R.dimen._14sdp).toInt(), false),
                indexSpanStringTwo,
                indexSpanStringTwo + spanStringTwo.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                twoClick,
                indexSpanStringTwo,
                indexSpanStringTwo + spanStringTwo.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                ForegroundColorSpan(COLOR_TWO),
                indexSpanStringTwo,
                indexSpanStringTwo + spanStringTwo.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            /* ss.setSpan(
                 UnderlineSpan(),
                 indexSpanStringTwo,
                 indexSpanStringTwo + spanStringTwo.length,
                 Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
             )*/
            ss.setSpan(
                typefaceSpanTwo,
                indexSpanStringTwo,
                indexSpanStringTwo + spanStringTwo.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            textView.text = ss
            textView.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}