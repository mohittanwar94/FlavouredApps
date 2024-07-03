package com.qureka.skool.utils

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.qureka.skool.BuildConfig
import com.qureka.skool.ServerConfig
import java.util.Currency
import java.util.Locale

const val TAG_COUNTRY = "CountyCheckGroupAssign"

object CountyCheckAndGroupAssign {
    private val groupIndiaCountries =
        listOf("IN", "VN", "MM", "SD", "AF", "MZ", "SO", "NA", "GW", "DJ", "BT", "EH", "MV", "BN")

    private val groupUSCountries = listOf(
        "US",
        "JP",
        "KH",
        "HT",
        "TG",
        "NI",
        "TM",
        "SV",
        "GE",
        "GM",
        "GA",
        "GQ",
        "MU",
        "MO",
        "CV",
        "MQ"
    )

    private val googleConsentGroupThreeCountries = listOf(
        "AT",
        "BE",
        "BG",
        "HR",
        "CY",
        "CZ",
        "DK",
        "EE",
        "FI",
        "FR",
        "DE",
        "GR",
        "HU",
        "IS",
        "IE",
        "IT",
        "LV",
        "LI",
        "LT",
        "LU",
        "MT",
        "NL",
        "NO",
        "PL",
        "PT",
        "RO",
        "SK",
        "SI",
        "ES",
        "SE",
        "GB",
        "CH"
    )

    val groupFourCountries = listOf(
        "CA",
        "AU",
        "NZ",
        "ID",
        "PH",
        "MY",
        "SG",
        "LK",
        "BD",
        "BR",
        "HK",
        "NP",
        "NG",
        "TH",
        "DZ",
        "BH",
        "EG",
        "IR",
        "IQ",
        "IL",
        "JO",
        "KW",
        "LB",
        "LY",
        "MA",
        "OM",
        "PS",
        "QA",
        "SA",
        "SY",
        "TN",
        "AE",
        "YE",
        "AZ",
        "BY",
        "CM",
        "CL",
        "CO",
        "CR",
        "CY",
        "EC",
        "FJ",
        "GH",
        "GT",
        "JO",
        "KZ",
        "KE",
        "ML",
        "MX",
        "PA",
        "PG",
        "PY",
        "PE",
        "KN",
        "LC",
        "SN",
        "RS",
        "SC",
        "KR",
        "TW",
        "TJ",
        "TZ",
        "TT",
        "TR",
        "UG",
        "UA",
        "UY",
        "UZ",
        "VA",
        "VE",
        "ZM",
        "ZW"
    )

    private val blockCountries = listOf("CN", "RU")
    fun detectAndAssignCountry(context: Context, defaultCountryIsoCode: String) {
        if (BuildConfig.DEBUG.not()) ServerConfig.commonCountryCode =
            getDetectedCountry(context, defaultCountryIsoCode).uppercase()
        if (ServerConfig.commonCountryCode.isEmpty()) ServerConfig.commonCountryCode =
            defaultCountryIsoCode
        applyGroupToApp()
        println("$TAG_COUNTRY ========commonCountryCode: ${ServerConfig.commonCountryCode}")
        when {
            ServerConfig.groupIndiaApk -> println("$TAG_COUNTRY ======Group: groupIndiaApk")
            ServerConfig.groupTwoManagePreferencesApk -> println("$TAG_COUNTRY ======Group: groupTwoManagePreferencesApk")
            ServerConfig.groupThreeConsonantPopupApk -> println("$TAG_COUNTRY ======Group: groupThreeConsonantPopupApk")
            else -> println("$TAG_COUNTRY ==========Group: groupFourApk")
        }
    }

    private fun isSafeWithIndiaGroup(): Boolean {
        return groupIndiaCountries.contains(ServerConfig.commonCountryCode.uppercase())
    }

    private fun isUsGroupTwoApp(): Boolean {
        return groupUSCountries.contains(ServerConfig.commonCountryCode.uppercase())
    }

    private fun isEuUser(): Boolean {
        return googleConsentGroupThreeCountries.contains(ServerConfig.commonCountryCode.uppercase())
    }

    fun isBannedCountry(): Boolean {
        return blockCountries.contains(ServerConfig.commonCountryCode.uppercase())
    }

    fun isIndiaCountry(): Boolean {
        return ServerConfig.commonCountryCode.uppercase() == "IN"
    }

    private fun applyGroupToApp() {
        ServerConfig.groupIndiaApk = false
        ServerConfig.groupTwoManagePreferencesApk = false
        ServerConfig.groupThreeConsonantPopupApk = false
        ServerConfig.groupFourBanGAApk = false
        if (isSafeWithIndiaGroup()) ServerConfig.groupIndiaApk = true
        else if (isUsGroupTwoApp()) ServerConfig.groupTwoManagePreferencesApk = true
        else if (isEuUser()) ServerConfig.groupThreeConsonantPopupApk = true
        else ServerConfig.groupFourBanGAApk = true

    }

    private fun getDetectedCountry(context: Context, defaultCountryIsoCode: String): String {
        return detectSIMCountry(context) ?: detectNetworkCountry(context) ?: detectLocaleCountry(
            context
        ) ?: defaultCountryIsoCode
    }

    private fun detectSIMCountry(context: Context): String? {
        try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (BuildConfig.DEBUG) println("$TAG_COUNTRY detectSIMCountry: ${telephonyManager.simCountryIso}")
            return telephonyManager.simCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun detectNetworkCountry(context: Context): String? {
        try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (BuildConfig.DEBUG) println("$TAG_COUNTRY Country: ${telephonyManager.networkCountryIso}")
            return telephonyManager.networkCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun detectLocaleCountry(context: Context): String? {
        try {
            val localeCountryISO = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0].country
            } else {
                context.resources.configuration.locale.country
            }
            if (BuildConfig.DEBUG) println("$TAG_COUNTRY detectLocaleCountry: $localeCountryISO")
            return localeCountryISO
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getCurrencySymbols(countryCode: String?): String? {
        return try {
            val map: Map<*, *> = getCurrencyLocaleMap()
            val locale = Locale("EN", countryCode)
            val currency = Currency.getInstance(locale)
            var s = currency.getSymbol(map[currency] as Locale?)
            if (s != null) {
                if (s.length > 1) {
                    s = s.substring(s.length - 1)
                }
            }
            s
        } catch (ignored: Throwable) {
            ""
        }
    }

    fun getCurrencyLocaleMap(): Map<Currency, Locale> {
        val map = HashMap<Currency, Locale>()
        for (locale in Locale.getAvailableLocales()) {
            try {
                val currency = Currency.getInstance(locale)
                map[currency] = locale
            } catch (e: Exception) {
                // skip strange locale
            }
        }
        return map
    }
}