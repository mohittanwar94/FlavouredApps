package com.qureka.skool

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qureka.skool.activity.SplashActivity
import com.qureka.skool.activity.TestIpActivity
import com.qureka.skool.common.BaseActivity

const val COUNRTY_CODE = "country_code"

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startActivity(Intent(this@SplashScreenActivity, SplashActivity::class.java))
//        finish()
        startActivity(
            Intent(
                this@SplashScreenActivity,
                if (BuildConfig.DEBUG) {
                    if (preferences.getString(COUNRTY_CODE, "")?.isEmpty() == true) {
                        TestIpActivity::class.java
                    } else {
                        ServerConfig.commonCountryCode =
                            preferences.getString(COUNRTY_CODE, "").toString()
                        SplashActivity::class.java
                    }
                } else
                    SplashActivity::class.java
            )
        )
        finish()
    }
}