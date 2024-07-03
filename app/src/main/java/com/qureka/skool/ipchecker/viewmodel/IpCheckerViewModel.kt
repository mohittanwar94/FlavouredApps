package com.qureka.skool.ipchecker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qureka.skool.ipchecker.model.ConsentRequest
import com.qureka.skool.ipchecker.model.IpCheckerResponse
import com.qureka.skool.ipchecker.repository.IpCheckerRepository
import com.qureka.skool.network.ResultWrapper
import com.qureka.skool.utils.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IpCheckerViewModel @Inject constructor(private val ipCheckerRepository: IpCheckerRepository ) : UserViewModel() {
    private val _ipCheckerResponse = MutableLiveData<IpCheckerResponse>()

    val ipCheckerResponse: LiveData<IpCheckerResponse> get() = _ipCheckerResponse
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getIpCheckForBlockCountry( countryIp: String?) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            scopeSuperVisor.launch {
                val response =
                    ipCheckerRepository.getIpCheckForBlockCountry( countryIp)
                response?.let { it ->
                    when (it) {
                        is ResultWrapper.NetworkError -> showNetworkError(response as ResultWrapper.NetworkError)
                        is ResultWrapper.GenericError -> showGenericError(response as ResultWrapper.GenericError)
                        is ResultWrapper.Success -> it.value.let {
                            _ipCheckerResponse.postValue(it)
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun updateConsentStatus( request: ConsentRequest) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            scopeSuperVisor.launch {
                val response =
                    ipCheckerRepository.updateConsentStatus(request)
                response?.let { it ->
                    when (it) {
                        is ResultWrapper.NetworkError -> showNetworkError(response as ResultWrapper.NetworkError)
                        is ResultWrapper.GenericError -> showGenericError(response as ResultWrapper.GenericError)
                        is ResultWrapper.Success -> it.value.let {
                            _ipCheckerResponse.postValue(it)
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }
}