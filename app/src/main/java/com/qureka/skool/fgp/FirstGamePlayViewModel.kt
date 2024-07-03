package com.qureka.skool.fgp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qureka.skool.activity.IS_FIRST_GAME_PLAY
import com.qureka.skool.fgp.model.FirstGamePlay
import com.qureka.skool.fgp.model.FirstGamePlayRequest
import com.qureka.skool.fgp.repository.FirstGamePlayRepository
import com.qureka.skool.network.ResultWrapper
import com.qureka.skool.sharef.AppPreferenceManager
import com.qureka.skool.utils.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstGamePlayViewModel @Inject constructor(private val firstGamePlayRepository: FirstGamePlayRepository ) : UserViewModel() {

    private val _baseResponse = MutableLiveData<FirstGamePlay>()
    val globalConfigResponse: LiveData<FirstGamePlay> get() = _baseResponse
    suspend fun sendFirstGpEvent(
        preferences: AppPreferenceManager,
        firstGamePlayRequest: FirstGamePlayRequest,
    ) {
        viewModelScope.launch {
            scopeSuperVisor.launch {
                val response = firstGamePlayRepository.sendFirstGamePlay(
                    firstGamePlayRequest
                )
                response?.let {
                    when (it) {
                        is ResultWrapper.NetworkError -> {
                            showNetworkError(response as ResultWrapper.NetworkError)
                            preferences.setBoolean(IS_FIRST_GAME_PLAY, false)
                        }

                        is ResultWrapper.GenericError -> {
                            preferences.setBoolean(IS_FIRST_GAME_PLAY, false)
                            showGenericError(response as ResultWrapper.GenericError)
                        }

                        is ResultWrapper.Success -> {
                            it.value.let { gamePlay ->
                                if (gamePlay.status.equals("200").not())
                                    preferences.setBoolean(IS_FIRST_GAME_PLAY, false)
                            }
                        }
                    }
                }
            }
        }
    }
}