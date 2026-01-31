package `in`.sipusumit.aniapi.core

import `in`.sipusumit.aniapi.model.*

/**
 * A single anime content provider.
 *
 * UI code must depend ONLY on this interface,
 * never on concrete implementations.
 */
interface AnimeSource {

    /** Unique stable id (e.g. "allanime") */
    val sourceId: String

    /** Human readable name (e.g. "AllAnime") */
    val displayName: String

    /**
    * Get Anime Details on HomeScreen
    */
    suspend fun getHomeScreen(): Result<HomeSection>

    /**
     * Search anime by text query.
     */
    suspend fun search(
        query: String,
        filters: SearchFilters = SearchFilters()
    ): Result<List<AnimeSummary>>

    /**
     * Fetch full anime details.
     */
    suspend fun getAnimeDetails(
        id: AnimeId
    ): Result<AnimeDetails>

    /**
     * List episodes for an anime.
     */
    suspend fun getEpisodes(
        id: AnimeId,
        language: LanguageMode = LanguageMode.SUB
    ): Result<List<Episode>>

    /**
     * Fetch playable streams for an episode.
     */
    suspend fun getStreams(
        id: AnimeId,
        episode: EpisodeNumber,
        language: LanguageMode = LanguageMode.SUB
    ): Result<List<Stream>>

    /**
     * Related / recommended anime.
     */
    suspend fun getRelated(
        id: AnimeId
    ): Result<List<RelatedAnime>>
}