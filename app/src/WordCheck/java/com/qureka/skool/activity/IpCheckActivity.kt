package com.qureka.skool.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.qureka.skool.databinding.ActivityIpCheckBinding

class IpCheckActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding: ActivityIpCheckBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIpCheckBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        initUi()
    }

    fun initUi() {
        /*AppPreferenceManager.getInstanced(this)
            .setBoolean(AppConstant.PreferenceKey.ipcheckactivityopen, false)*/
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    override fun onBackPressed() {
        finish()
    }
}