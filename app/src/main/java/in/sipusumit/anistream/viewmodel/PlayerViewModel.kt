package `in`.sipusumit.anistream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.model.Episode
import `in`.sipusumit.aniapi.model.EpisodeNumber
import `in`.sipusumit.aniapi.model.Stream
import `in`.sipusumit.anistream.data.local.HistoryDao
import `in`.sipusumit.anistream.data.local.WatchHistory
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


fun EpisodeNumber.next(): EpisodeNumber = // TODO: fix this
    EpisodeNumber(value + 1)

private fun List<Episode>.indexOfEpisode(number: EpisodeNumber): Int {
    return indexOfFirst { it.number == number }
}

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Ready(
        val streams: List<Stream>,
        val current: PlayingEpisode,
        val hasNext: Boolean,
        val startPosition: Long = 0L
    ) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

data class PlayingEpisode(
    val animeId: AnimeId,
    val episode: EpisodeNumber
)

class PlayerViewModel(
    private val source: AnimeSource,
    private val animeId: AnimeId,
    private val episode: EpisodeNumber,
    private val historyDao: HistoryDao
): ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes = _episodes.asStateFlow()

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState

    private val _downloadUrl = MutableSharedFlow<Pair<String, String>>()
    val downloadUrl = _downloadUrl.asSharedFlow()

    private val _currentStream = MutableStateFlow<Stream?>(null)
    val currentStream: StateFlow<Stream?> = _currentStream.asStateFlow()

    private val _episodeList = MutableStateFlow<List<Episode>>(emptyList())
    val episodeList: StateFlow<List<Episode>> = _episodeList.asStateFlow()

    fun onStreamSelected(stream: Stream) {
        _currentStream.value = stream
    }

    init {
        viewModelScope.launch {
            _episodes.value = source.getEpisodes(animeId).getOrNull() ?: emptyList()
            playEpisode(animeId, episode)
        }
    }

//    private fun loadStreams() {
//        viewModelScope.launch {
//            source.getStreams(animeId, episode)
//                .onSuccess { streams ->
//                    _uiState.value = PlayerUiState.Ready(streams)
//                }
//                .onFailure {
//                    _uiState.value = PlayerUiState.Error("Failed to load streams")
//                }
//        }
//    }

    fun onEpisodeEnded() {
        val state = _uiState.value
        if (state !is PlayerUiState.Ready) return

        val list = _episodes.value
        val currentIndex = list.indexOfEpisode(state.current.episode)

        if (currentIndex == 0) return
        if (currentIndex >= list.lastIndex) return // no next episode

        val nextEpisode = list[currentIndex - 1]

        playEpisode(
            animeId = state.current.animeId,
            episode = nextEpisode.number
        )
    }

    // --------------------
    // MANUAL EPISODE CHANGE
    // --------------------
    fun playEpisode(episode: EpisodeNumber) {
        val state = _uiState.value
        if (state !is PlayerUiState.Ready) return

        playEpisode(state.current.animeId, episode)
    }

    fun playEpisode(animeId: AnimeId, episode: EpisodeNumber) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading

            // Fetch history in parallel with streams
            val historyJob = async { historyDao.getHistory(animeId.value, episode.value) }
            val streamsResult = source.getStreams(animeId, episode)
            val history = historyJob.await()

            source.getStreams(animeId, episode)
                .onSuccess { streams ->
                    _uiState.value = PlayerUiState.Ready(
                        streams = streams,
                        current = PlayingEpisode(animeId, episode),
                        hasNext = episode.value.toFloat() < episodes.value.size,
                        startPosition = history?.position ?: 0L
                    )
                }
                .onFailure {
                    _uiState.value = PlayerUiState.Error(it.message ?: "Failed to load episode")
                }
        }
    }

    fun onProgressChange(position: Long, duration: Long) {
        val state = _uiState.value as? PlayerUiState.Ready ?: return

        // Debounce: Only save if significant progress or periodically
        // For simplicity, launch a coroutine. In production, use a debouncer.
        viewModelScope.launch {
            historyDao.saveHistory(
                WatchHistory(
                    animeId = state.current.animeId.value,
                    episodeNumber = state.current.episode.value,
                    position = position,
                    duration = duration
                )
            )
        }
    }

    fun downloadEpisode(episodeNumber: EpisodeNumber){
        viewModelScope.launch {
            source.getStreams(animeId, episode).onSuccess { streams ->
                // pick best stream (you decide logic)
                source.getAnimeDetails(animeId).onSuccess {
//                    val stream = streams.first()
                    val streamUrl = streams.first().url
                    val fileName = "${it.title.english?: it.title.primary}_Ep${episodeNumber.value}.mp4"
                    _downloadUrl.emit(Pair(fileName, streamUrl))
                }
            }
        }
    }

    private fun hasNextEpisode(current: EpisodeNumber): Boolean {
        val list = _episodes.value
        val index = list.indexOfEpisode(current)
        return index != -1 && index < list.lastIndex
    }

    fun onQueryChange(q: String) {
        _query.value = q
    }

}