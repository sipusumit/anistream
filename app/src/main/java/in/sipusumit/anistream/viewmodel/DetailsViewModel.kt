package `in`.sipusumit.anistream.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.sipusumit.aniapi.core.AnimeError
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.core.userMessage
import `in`.sipusumit.aniapi.model.AnimeDetails
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.model.Episode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetailsAndCount(val anime: AnimeDetails, val epList: List<Episode>)

sealed interface DetailsUiState {
    object Loading : DetailsUiState
    data class Success(val result: DetailsAndCount) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}


class DetailsViewModel(
    private val source: AnimeSource,
    val animeId: AnimeId
): ViewModel() {
    private val _state = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val state: StateFlow<DetailsUiState> = _state

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            source.getAnimeDetails(animeId)
                .onSuccess { details ->
                    source.getEpisodes(animeId)
                        .onSuccess { eps ->
                            _state.value = DetailsUiState.Success(DetailsAndCount(details, eps))
                        }
                        .onFailure {
                            Log.e("ANI", "Failed to load details", it)
                            _state.value = DetailsUiState.Success(DetailsAndCount(details, emptyList()))
                        }
                }
                .onFailure {
                    Log.e("ANI", "Failed to load details", it)
                    _state.value = DetailsUiState.Error(
                        (it as? AnimeError)?.userMessage() ?: "Failed to load"
                    )
                }
        }
    }
}