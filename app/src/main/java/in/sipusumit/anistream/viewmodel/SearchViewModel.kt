package `in`.sipusumit.anistream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.sipusumit.aniapi.core.AnimeException
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.core.userMessage
import `in`.sipusumit.aniapi.model.AnimeSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SearchUiState{
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val result: List<AnimeSummary>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class SearchViewModel(private val source: AnimeSource) : ViewModel() {
    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val state: StateFlow<SearchUiState> = _state

    fun clear(){
        _state.value = SearchUiState.Idle;
    }

    fun search(query: String){
        if (query.isBlank()) {
            _state.value = SearchUiState.Idle
            return
        }
        viewModelScope.launch {
            _state.value = SearchUiState.Loading

            source.search(query)
                .onSuccess {
                    _state.value = SearchUiState.Success(it)
                }
                .onFailure {
                    _state.value = SearchUiState.Error(
                        (it as? AnimeException)?.error?.userMessage() ?: "Something Went Wrong"
                    )
                }
        }
    }
}