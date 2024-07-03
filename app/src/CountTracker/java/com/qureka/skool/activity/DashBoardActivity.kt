package com.qureka.skool.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qureka.skool.AddUnit
import com.qureka.skool.R
import com.qureka.skool.ServerConfig.mStopService
import com.qureka.skool.adapter.SoundAdapter
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.databinding.ActivityDashBoardBinding
import com.qureka.skool.utils.NotificationUtils
import com.qureka.skool.utils.OnRecyclerViewClick
import com.qureka.skool.utils.Utils


const val POSITION = "position"

class DashBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashBoardBinding
    private lateinit var soundAdapter: SoundAdapter
    private val TIME_DELAY = 2000
    private var back_pressed: Long = 0
    private val REQUEST_PERMISSIONS_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUi()
        setDashBoardAdapter()
        updateSessionCount()
        onShowAd()
        updateDataPreferenceView()
        val notificationUtils = NotificationUtils(this@DashBoardActivity)
        notificationUtils.getManager().cancelAll()
        preferences.setInt(ANIMATION_POSITION, -1)
        mStopService = false
    }

    override fun onShowAd() {
        super.onShowAd()
        loadNativeBannerAd(
            this@DashBoardActivity,
            AddUnit.PP_Dashboard_NativeAdvanced,
            binding.adView
        )
    }

    override fun onResume() {
        super.onResume()
        binding.scrollView.post { binding.scrollView.scrollTo(0, 0) }
        updateDataPreferenceView()
        checkForConsentConditions()
        showManageDataPref(binding.layoutIncludeBottom.tvManageDataPref)
        if (this::soundAdapter.isInitialized)
            soundAdapter.setData(Utils.getSoundData(this@DashBoardActivity))
        onShowAd()
    }

    override fun updateDataPreferenceView() {
        showManageDataPref(binding.layoutIncludeBottom.tvManageDataPref)
    }

    private fun stopService() {
        mStopService = false
        preferences.setInt(ANIMATION_POSITION, -1)
    }

    private fun setDashBoardAdapter() {
        with(binding) {
            soundAdapter =
                SoundAdapter(
                    Utils.getSoundData(this@DashBoardActivity),
                    object : OnRecyclerViewClick {
                        override fun onClick(position: Int, type: Int) {
                            if (preferences.getInt(ANIMATION_POSITION, -1) != position)
                                stopService()
                            if (soundAdapter.getData()[position].isUnlock == 0) {
                                showRvDialog(position, this@DashBoardActivity)
                            } else {
                                startActivity(
                                    Intent(
                                        this@DashBoardActivity,
                                        DetailActivity::class.java
                                    ).putExtra(POSITION, position)
                                )
                            }
                        }
                    })
            val linearLayoutManager =
                StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
            rvSounds.layoutManager = linearLayoutManager
            rvSounds.adapter = soundAdapter
        }
    }

    override fun onRvAdDismiss(position: Int) {
        if (preferences.getBoolean(IS_FIRST_GAME_PLAY, false)?.not() == true) {
            fireFgpEvent()
        }
        Utils.saveUnlock(position, this)
        startActivity(
            Intent(
                activity,
                DetailActivity::class.java
            ).putExtra(POSITION, position)
        )
    }

    private fun setUi() {
        createPrivacyPolicyAndCookiesPolicySpan(
            getString(R.string.tnc_and_cookies_span),
            binding.tvPleaseAgree,
            this@DashBoardActivity
        )
    }

    override fun onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Utils.showToast(this@DashBoardActivity, "Press once again to exit!")
        }
        back_pressed = System.currentTimeMillis()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            val isDenied = checkDeniedPermission(grantResults)
            isDenied?.let {
                Utils.showToast(
                    this,
                    "Permission Not Allowed ${getString(R.string.app_name)} will not work without permission"
                )
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun checkDeniedPermission(grantResults: IntArray): Pair<Int, Boolean>? {
        var deniedPair: Pair<Int, Boolean>? = null
        for ((result, index) in grantResults.withIndex()) {
            if (result == PackageManager.PERMISSION_DENIED) {
                deniedPair = Pair(index, true)
            }
        }
        return deniedPair
    }

    private fun requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionArray = ArrayList<String?>()
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionArray.add(Manifest.permission.RECORD_AUDIO)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionArray.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (permissionArray.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    permissionArray.toTypedArray(),
                    REQUEST_PERMISSIONS_CODE
                )
            }
        }
    }
}