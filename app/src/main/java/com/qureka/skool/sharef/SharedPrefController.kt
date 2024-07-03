package com.qureka.skool.sharef

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson


@SuppressLint("StaticFieldLeak")
object SharedPrefController {
    private val PREF_FILE_NAME = "android.content.SharedPreferences"
    val COIN_WALLET_KEY = "Coins"

    val IS_ALTERNET = "is_alternet"
    internal var sharedPreferencesController: SharedPrefController? = null
    private var preferences: SharedPreferences? = null
    internal val lock = Any()
    private val TAG = SharedPrefController::class.java.simpleName
    internal var mcontext: Context? = null

    //    private fun SharedPrefController(context: Context): SharedPrefController? {
//        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
//    }
    init {

    }

    fun getSharedPreferencesController(context: Context): SharedPrefController? {
        mcontext = context
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        try {
            synchronized(lock) {
                if (sharedPreferencesController == null) {
                    sharedPreferencesController = SharedPrefController
                }
                return sharedPreferencesController
            }
        } catch (e: Exception) {

            e.printStackTrace()
            return sharedPreferencesController

        } catch (sof: StackOverflowError) {
            sof.printStackTrace()
            return sharedPreferencesController
        }

    }

    fun removeFromSharedPreferences(mContext: Context?, key: String) {
        if (mContext != null) {
            if (preferences != null)
                preferences!!.edit().remove(key).commit()
        }
    }

    fun setInt(key: String, value: Int) {
        val editor = preferences!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getInt(key: String): Int {
        return preferences!!.getInt(key, 0)
    }

    fun getDefaultInt(key: String, defaultValue: Int): Int {
        return preferences!!.getInt(key, defaultValue)
    }

    fun getIsAlternet(key: String): Boolean {
        val currentStatus: Boolean
        currentStatus = preferences!!.getBoolean(key, false)
        setIsAlternet(key, !currentStatus)

        return currentStatus
    }

    private fun setIsAlternet(key: String, value: Boolean?) {
        val editor = preferences!!.edit()
        editor.putBoolean(key, value!!)
        editor.commit()


    }

    fun getLongValue(key: String): Long {
        return preferences!!.getLong(key, 0)
    }

    fun putLong(key: String, value: Long) {
        checkForNullKey(key)
        preferences!!.edit().putLong(key, value).apply()
    }

    fun checkForNullKey(key: String?) {
        if (key == null) {
            throw NullPointerException()
        }
    }

//    fun getImageQuizList(key: String): ArrayList<ImageQuizQuestions> {
//        var obj = ArrayList<ImageQuizQuestions>()
//        try {
//            val gson = Gson()
//            val json = preferences!!.getString(key, "")
//            val type = object : TypeToken<ArrayList<ImageQuizQuestions>>() {
//
//            }.type
//            obj = gson.fromJson<ArrayList<ImageQuizQuestions>>(json, type)
//        } catch (e: NumberFormatException) {
//            e.printStackTrace()
//        } catch (exp: JsonSyntaxException) {
//            exp.printStackTrace()
//        } catch (ee: Exception) {
//            ee.printStackTrace()
//        }
//
//        return obj
//
//    }


//    fun setImageQuiz(key: String, imageQuiz: ArrayList<ImageQuizQuestions>?) {
//
//        if (imageQuiz != null && imageQuiz.size > 0) {
//            val gson = Gson()
//            val jsonString = gson.toJson(imageQuiz)
//            Log.d("finalResponse", jsonString)
//            val editor = preferences!!.edit()
//            editor.putString(key, jsonString)
//            editor.commit()
//
//        }
//    }


    fun setObject(key: String, `object`: Any) {
        val value = Gson().toJson(`object`)
        checkForNullKey(key)
        //checkForNullValue(value);
        preferences!!.edit().putString(key, value).apply()
    }

    fun getStringValue(key: String): String? {
        return preferences!!.getString(key, null)
    }

    fun setString(key: String, value: String) {
        val editor = preferences!!.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun setBoolean(key: String, value: Boolean?) {
        val editor = preferences!!.edit()
        editor.putBoolean(key, value!!)
        editor.commit()
    }

    fun getBooleanValue(key: String): Boolean? {
        return preferences!!.getBoolean(key, false)
    }

    fun setLongValue(key: String, value: Long) {
        val editor = preferences!!.edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun setAppCount(key: String, value: Int) {
        val editor = preferences!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getAppCount(key: String): Int {
        return preferences!!.getInt(key, 0)
    }

    fun setAppOverCount(key: String, value: Int) {
        val editor = preferences!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getAppOverCount(key: String): Int {
        return preferences!!.getInt(key, 0)
    }

    fun setleaderbCount(key: String, value: Int) {
        val editor = preferences!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getleaderbCount(key: String): Int {
        return preferences!!.getInt(key, 0)
    }

    fun setmixnmatchCount(key: String, value: Int) {
        val editor = preferences!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getmixnmatchCount(key: String): Int {
        return preferences!!.getInt(key, 0)
    }

}