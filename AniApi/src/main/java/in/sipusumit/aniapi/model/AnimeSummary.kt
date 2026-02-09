package `in`.sipusumit.aniapi.model

/**
 * Lightweight anime representation.
 *
 * Used for search results, lists, and recommendations.
 */
data class AnimeSummary(

    /** Stable anime identifier */
    val id: AnimeId,

    /** Titles */
    val title: AnimeTitle,

    /** Poster / cover images */
    val poster: ImageSet? = null,

    /** Anime type (TV, Movie, OVA, etc) */
    val type: AnimeType,

    /** Current status (Ongoing, Finished, etc) */
    val status: AnimeStatus,

    /** Average score (nullable if unavailable) */
    val score: Float? = null,

    /** Episode count if known */
    val episodeCount: EpisodeCount? = null,

    /** MAL Id if known */
    val malId: Long? = null
)