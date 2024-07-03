package com.qureka.skool.sharef

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.qureka.skool.QurekaSkoolApplication
import java.util.*

@SuppressLint("StaticFieldLeak")
object AppPreferenceManager {


    internal var newcontext: Context? = null
    var appPreferenceManager: AppPreferenceManager? = null
    internal var sharedPreferences: SharedPreferences? = null

    @JvmStatic
    fun getInstanced(context: Context): AppPreferenceManager {
        newcontext = context
        if (appPreferenceManager == null) {
            appPreferenceManager = AppPreferenceManager
        }
        return appPreferenceManager as AppPreferenceManager
    }

    fun getInstanceManager(): AppPreferenceManager {
        newcontext = QurekaSkoolApplication.getApplication().applicationContext
        if (appPreferenceManager == null) {
            appPreferenceManager = AppPreferenceManager
        }
        return appPreferenceManager as AppPreferenceManager
    }

    fun setString(key: String, value: String) {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, value: String): String? {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, value)
    }

    fun getString(key: String): String {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")!!
    }

    fun setBoolean(key: String, value: Boolean) {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBoolean(key: String, value: Boolean): Boolean? {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, value)
    }

    fun getBoolean(key: String): Boolean {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, false)
    }

    fun setlong(key: String?, value: Long?) {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value!!)
        editor.commit()

    }

    fun setlong1(key: String, value: Long) {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.commit()

    }

    fun getlong(key: String, value: Long?): Long {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(key, value!!)
    }

    fun getInt(key: String): Int {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, 0)
    }

    fun setInt(key: String, value: Int) {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, defaultValue)
    }

    //for Gson

    fun putObject(key: String, `object`: Any) {
        try {
            sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val value = Gson().toJson(`object`)
            checkForNullKey(key)
            checkForNullValue(value)
            sharedPreferences!!.edit().putString(key, value).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getObject(key: String, clazz: Class<*>): Any? {
        try {
            return Gson().fromJson(getString(key), clazz)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (nsme: NoSuchMethodError) {
            nsme.printStackTrace()
        }
        return null
    }

    fun checkForNullKey(key: String?) {
        if (key == null) {
            throw NullPointerException()
        }
    }

    fun checkForNullValue(value: String?) {
        if (value == null) {
            throw NullPointerException()
        }

    }

    fun getListInt(key: String): ArrayList<Int> {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val myList = TextUtils.split(sharedPreferences.getString(key, ""), "‚‗‚")
        val arrayToList = ArrayList(Arrays.asList(*myList))
        val newList = ArrayList<Int>()

        for (item in arrayToList)
            newList.add(Integer.parseInt(item))

        return newList
    }

    fun putListInt(key: String, intList: ArrayList<Int>) {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        checkForNullKey(key)
        val myIntList = intList.toTypedArray()
        sharedPreferences.edit().putString(key, TextUtils.join("‚‗‚", myIntList)).apply()
    }

    fun setConsentStatus(isConsent: Boolean) {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("consentPopAllowDeny", isConsent).apply()
    }

    fun getConsentStatus(): Boolean {
        val sharedPreferences = newcontext!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("consentPopAllowDeny", false)
    }

    internal var sharedPreferences1 =
        newcontext?.getSharedPreferences("MyPref", Context.MODE_PRIVATE)

    /* fun saveSubjectList(key: String, list: ArrayList<OptedSubject>) {
         val gson = Gson()
         val json = gson.toJson(list)
         val editor = sharedPreferences1?.edit()
         editor!!.putString(key, json)
         editor.apply()

     }

     fun getSubjectList(key: String): ArrayList<OptedSubject> {
         var obj = ArrayList<OptedSubject>()
         try {
             val gson = Gson()
             val json = sharedPreferences1?.getString(key, "")
             val type = object : TypeToken<ArrayList<OptedSubject>>() {

             }.type
             obj = gson.fromJson<ArrayList<OptedSubject>>(json, type)
         } catch (soe: StackOverflowError) {
             soe.printStackTrace()
         } catch (e: NumberFormatException) {
             e.printStackTrace()
         } catch (exp: JsonSyntaxException) {
             exp.printStackTrace()
         } catch (ee: Exception) {
             ee.printStackTrace()
         }

         return obj
     }*/
}