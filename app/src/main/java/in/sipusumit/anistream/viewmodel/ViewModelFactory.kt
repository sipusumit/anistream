package `in`.sipusumit.anistream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.model.EpisodeNumber

class HomeViewModelFactory(
    private val source: AnimeSource
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(source) as T
        }
        error("Unknown ViewModel class")
    }
}

class SearchViewModelFactory(private val source: AnimeSource): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(source) as T
        }
        error("Unknown ViewModel class")
    }
}

class DetailsViewModelFactory(
    private val source: AnimeSource,
    private val animeId: AnimeId
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(
                source = source,
                animeId = animeId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

class PlayerViewModelFactory(
    private val source: AnimeSource,
    private val animeId: AnimeId,
    private val episode: EpisodeNumber
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(source, animeId, episode) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}