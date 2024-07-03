package com.qureka.skool.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.qureka.skool.AddUnit
import com.qureka.skool.InterstitialAdManager
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.todo.ToDoListScreen
import com.qureka.skool.utils.OnRecyclerViewClick
import com.qureka.skool.utils.Utils
import com.qureka.skool.viewmodel.ToDoViewModel

const val ITEM = "ITEM"
const val EDIT = "EDIT"

class DetailActivity : BaseActivity() {
    private val toDoViewModel by viewModels<ToDoViewModel>()
    private lateinit var interstitialAdManager: InterstitialAdManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                DetailUi()
            }
        }
        interstitialAdManager = InterstitialAdManager()
        interstitialAdManager.preLoadInterstitialAd(
            this@DetailActivity, AddUnit.PP_BackButton_Interstitial
        )
    }

    override fun onResume() {
        super.onResume()
        toDoViewModel.loadTasks()
    }


    @Composable
    private fun DetailUi() {
        ToDoListScreen(toDoViewModel, object : OnRecyclerViewClick {
            override fun onClick(position: Int, type: Int) {
                when (type) {
                    1 -> {
                        showAd()
                    }

                    3 -> {
                        if (preferences.getBoolean(IS_FIRST_GAME_PLAY, false)
                                ?.not() == true
                        ) fireFgpEvent()
                        val bundle = Bundle()
                        if (position == 4)
                            bundle.putSerializable(ITEM, toDoViewModel.todo.value)
                        bundle.putBoolean(EDIT, position == 4)
                        startActivity(
                            Intent(
                                this@DetailActivity,
                                AddEditNotesActivity::class.java
                            ).putExtras(bundle)
                        )
                    }
                }


            }
        })
    }

    private fun showAd() {
        finish()
       /* Utils.showProgBar(this)
        if (interstitialAdManager.mInterstitialAd == null && interstitialAdManager.isAdLoading.value == false) {
            interstitialAdManager.preLoadInterstitialAd(
                this@DetailActivity,
                AddUnit.PP_BackButton_Interstitial,
            )
            interstitialAdManager.isAdLoading.value = true
        }
        showAdMobAd(interstitialAdManager,
            this,
            object : InterstitialAdManager.interstitialAdCompleteListener {
                override fun onNavigateToNext() {
                    Utils.dismissProgress()
                    finish()
                }

                override fun dismissProgressBar() {
                    Utils.dismissProgress()
                }
            })*/
    }

    override fun onBackPressedCustom() {
        showAd()
    }
}