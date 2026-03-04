package `in`.sipusumit.anistream.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.sipusumit.aniapi.core.AnimeException
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.core.userMessage
import `in`.sipusumit.aniapi.model.AnimeDetails
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.model.HomeSection
import `in`.sipusumit.anistream.Anime
import `in`.sipusumit.anistream.data.local.HistoryDao
import `in`.sipusumit.anistream.data.local.WatchHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


data class ContinueWatchingState(
    val history: WatchHistory,
    val anime: AnimeDetails
)
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val section: HomeSection) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(private val source: AnimeSource, private val historyDao: HistoryDao) : ViewModel() {
    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    // New State for Continue Watching
    private val _continueWatching = MutableStateFlow<ContinueWatchingState?>(null)
    val continueWatching = _continueWatching.asStateFlow()

    init{
        loadHome()
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            // Observe the DB. This automatically updates when you return from the player.
            historyDao.getLatestWatched().collectLatest { history ->
                if (history != null) {
                    // We have history, but we need the Image/Title. Fetch from API.
                    source.getAnimeDetails(AnimeId(history.animeId))
                        .onSuccess { anime ->
                            _continueWatching.value = ContinueWatchingState(history, anime)
                        }
                        .onFailure {
                            Log.e("HomeVM", "Failed to load history metadata", it)
                            _continueWatching.value = null
                        }
                } else {
                    _continueWatching.value = null
                }
            }
        }
    }

    fun loadHome() {
        viewModelScope.launch {
            source.getHomeScreen()
                .onSuccess {
                    _state.value = HomeUiState.Success(it)
                }
                .onFailure {
                    Log.e("ANI", "Failed to load home", it)
                    _state.value = HomeUiState.Error(
                        (it as? AnimeException)?.error?.userMessage()
                            ?: "Something went wrong"
                    )
                }
        }
    }
}