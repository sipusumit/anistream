package `in`.sipusumit.anistream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.sipusumit.aniapi.core.AnimeException
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.core.userMessage
import `in`.sipusumit.aniapi.model.HomeSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val section: HomeSection) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(private val source: AnimeSource) : ViewModel() {
    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    init{
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            source.getHomeScreen()
                .onSuccess {
                    _state.value = HomeUiState.Success(it)
                }
                .onFailure {
                    _state.value = HomeUiState.Error(
                        (it as? AnimeException)?.error?.userMessage()
                            ?: "Something went wrong"
                    )
                }
        }
    }
}