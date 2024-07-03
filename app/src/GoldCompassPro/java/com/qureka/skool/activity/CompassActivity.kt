package com.qureka.skool.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.qureka.skool.AdInfo
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.common.PLAYED_STATE
import com.qureka.skool.databinding.ActivityCompassBinding
import com.qureka.skool.utils.Utils

class CompassActivity : BaseActivity(), SensorEventListener {

    private lateinit var binding: ActivityCompassBinding
    private var currentDegree = 0f
    private var sensorManager: SensorManager? = null
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sensorManager = this.getSystemService(SENSOR_SERVICE) as SensorManager
        interstitialAdManager = InterstitialAdManager()
        interstitialAdManager.preLoadInterstitialAd(
            this@CompassActivity, AddUnit.GET_STARTED_SCREEN
        )
        binding.ivBack.setOnClickListener {
            showAd()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        // get the angle around the z-axis rotated
        val degree = Math.round(event.values[0]).toFloat()
        val degreeText = degree.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0]
        binding.textDegree.text = degreeText

        // create a rotation animation (reverse turn degree degrees)
        val ra = RotateAnimation(
            currentDegree,
            -degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        // how long the animation will take place
        ra.duration = 210

        // set the animation after the end of the reservation status
        ra.fillAfter = true

        // Start the animation
        binding.ivCompass.startAnimation(ra)
        currentDegree = -degree
    }

    override fun onAccuracyChanged(sensor: Sensor?, i: Int) {}

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    private fun showAd() {
        Utils.showProgBar(this)
        if (interstitialAdManager.mInterstitialAd == null && interstitialAdManager.isAdLoading.value == false) {
            interstitialAdManager.preLoadInterstitialAd(
                this@CompassActivity,
                AddUnit.GET_STARTED_SCREEN,
            )
            interstitialAdManager.isAdLoading.value=true
        }
        showAdMobAd(
            interstitialAdManager,
            this,
            object : InterstitialAdManager.interstitialAdCompleteListener {
                override fun onNavigateToNext() {
                    Utils.dismissProgress()
                    finish()
                }

                override fun dismissProgressBar() {
                    Utils.dismissProgress()
                }
            })
    }
    override fun onBackPressed() {
        showAd()
    }
}