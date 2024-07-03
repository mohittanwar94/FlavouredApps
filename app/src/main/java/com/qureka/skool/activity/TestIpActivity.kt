package com.qureka.skool.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.qureka.skool.BuildConfig
import com.qureka.skool.COUNRTY_CODE
import com.qureka.skool.R
import com.qureka.skool.ServerConfig
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.ActivityTestIpBinding
import com.qureka.skool.utils.DefaultLocaleHelper
import java.util.Locale

class TestIpActivity : BaseActivity() {
    private lateinit var _binding: ActivityTestIpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTestIpBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.btnsubmit.setOnClickListener {
            ServerConfig.commonCountryCode = _binding.etipenter.text.toString().uppercase()
            if (ServerConfig.commonCountryCode.isNotEmpty() && ServerConfig.commonCountryCode.length == 2) {
                // for set language
                if (BuildConfig.APPLICATION_ID == getString(R.string.app_8)) {
                    if (ServerConfig.commonCountryCode.uppercase(Locale.ROOT) == "ES"
                        || ServerConfig.commonCountryCode.uppercase(Locale.ROOT) == "MX"
                    ) {
                        DefaultLocaleHelper.getInstance(this).setCurrentLocale("es")
                    }
                }

                preferences.setString(COUNRTY_CODE, _binding.etipenter.text.toString().uppercase())
                val openMainActivity = Intent(this@TestIpActivity, SplashActivity::class.java)
                startActivity(openMainActivity)
                finish()
            } else {
                Toast.makeText(this, "Please enter Country Code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(DefaultLocaleHelper.getInstance(base!!).onAttach())
    }
}