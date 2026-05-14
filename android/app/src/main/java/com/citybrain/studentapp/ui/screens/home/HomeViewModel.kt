package com.citybrain.studentapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.citybrain.studentapp.data.model.HomeBundle
import com.citybrain.studentapp.data.repository.CityBrainRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val bundle: HomeBundle? = null,
    val error: String? = null,
    val isLivePolling: Boolean = false
)

class HomeViewModel(
    private val repository: CityBrainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
                refresh(showLoading = _uiState.value.bundle == null)
                delay(2_000)
            }
        }
    }

    fun refresh(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            } else {
                _uiState.value = _uiState.value.copy(error = null, isLivePolling = true)
            }

            runCatching {
                repository.getHomeBundle()
            }.onSuccess {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    bundle = it,
                    isLivePolling = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message,
                    isLivePolling = false
                )
            }
        }
    }
}

class HomeViewModelFactory(
    private val repository: CityBrainRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
