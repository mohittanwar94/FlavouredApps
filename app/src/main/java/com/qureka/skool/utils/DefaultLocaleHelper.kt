package com.qureka.skool.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import java.util.Locale

class DefaultLocaleHelper private constructor(context: Context) : BaseLocaleHelper(context) {
    companion object {
        /* Mark the instance as Volatile*/
        @Volatile
        private var instance: LocaleHelperKt? = null
        private var LOCK: Any = Any()

        /**
         * Return an instance of [LocaleHelperKt].
         *
         * @param context [Context].
         *
         * @return Instance of [LocaleHelperKt].
         */
        fun getInstance(context: Context): LocaleHelperKt {
            synchronized(LOCK) {
                if (instance == null) instance = DefaultLocaleHelper(context)
                return instance!!
            }
        }
    }

    override fun setCurrentLocale(language: String): Context {
        return setLocale(context, language)
    }

    override fun onAttach(defaultLanguage: String?): Context {
        val lang = getPersistedLocale(defaultLanguage ?: Locale.getDefault().language)
        return setLocale(context, lang)
    }

    override fun getCurrentLocale(): String =
        getPersistedLocale(Locale.getDefault().language)
}

/**
 * Base Locale Helper Class.
 *
 * This provide implementation of caching (SET, GET) selected/current locale by using [PreferenceManager].
 *
 * Also, provide implementation of setting new locale.
 *
 * @param context [Context].
 */
@Suppress("DEPRECATION")
abstract class BaseLocaleHelper(internal val context: Context) :
    LocaleHelperKt {
    companion object {
        private const val SELECTED_LANGUAGE = "LocaleHelperKt_SelectedLanguage"
    }

    /**
     * Get persisted locale from [SharedPreferences].
     *
     * @param defaultLocale If persisted locale not found, return the [defaultLocale].
     *
     * @return Current locale ([String])
     */
    internal fun getPersistedLocale(defaultLocale: String): String {
        return cacheStorage.getString(SELECTED_LANGUAGE, defaultLocale) ?: defaultLocale
    }

    /* Provide lazy an instance of [SharedPreferences] */
    private val cacheStorage: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            context
        )
    }

    /**
     * Update resource configuration and [Context].
     *
     * @param context [Context]
     * @param language [String]
     *
     * @return Context itself ([Context]])
     */
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    /**
     * Set new locale to the resource based on API versions.
     *
     * @param context Application [Context].
     * @param newLocale New locale to be set.
     *
     * @return Context itself ([Context]).
     */
    private fun baseSetLocale(context: Context, newLocale: Locale): Context {
        var tmpContext = context
        val res = tmpContext.resources
        val configuration = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale)
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            tmpContext = tmpContext.createConfigurationContext(configuration)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale)
            res.updateConfiguration(configuration, res.displayMetrics)
        } else {
            configuration.locale = newLocale
            res.updateConfiguration(configuration, res.displayMetrics)
        }
        return tmpContext
    }

    /**
     * Set new locale and cache the new locale to [SharedPreferences].
     *
     * @param context Application [Context].
     * @param newLocale New locale to be set.
     *
     * @return Context itself ([Context]).
     */
    internal fun setLocale(context: Context, newLocale: String): Context {
        cacheStorage.edit().apply {
            putString(SELECTED_LANGUAGE, newLocale)
            apply()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, newLocale)
        }
        val locale = Locale(newLocale)
        return baseSetLocale(context, locale)
    }
}

/**
 * Simple Locale Helper for Android.
 */
interface LocaleHelperKt {
    fun onAttach(defaultLanguage: String? = null): Context
    fun getCurrentLocale(): String
    fun setCurrentLocale(language: String): Context
}