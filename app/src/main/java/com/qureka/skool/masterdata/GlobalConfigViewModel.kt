package com.qureka.skool.masterdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qureka.skool.network.ResultWrapper
import com.qureka.skool.utils.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalConfigViewModel @Inject constructor(private val globalConfigRepository: GlobalConfigRepository) : UserViewModel() {

    private val _baseResponse = MutableLiveData<BaseResponse>()
    val globalConfigResponse: LiveData<BaseResponse> get() = _baseResponse
    suspend fun getGlobalConfig() {
        viewModelScope.launch {
            scopeSuperVisor.launch {
                val response = globalConfigRepository.getGlobalConfig()
                response?.let {
                    when (it) {
                        is ResultWrapper.NetworkError -> {
                            showNetworkError(response as ResultWrapper.NetworkError)
                        }

                        is ResultWrapper.GenericError -> {
                            showGenericError(response as ResultWrapper.GenericError)
                        }

                        is ResultWrapper.Success -> {
                            it.value.let {
                                _baseResponse.postValue(it)
                            }
                        }
                    }
                }
            }
        }
    }
}