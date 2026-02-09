package `in`.sipusumit.aniapi.model

/**
 * Full detailed information about an anime.
 *
 * Designed for detail screens in GUI apps.
 */
data class AnimeDetails(

    /** Stable anime identifier */
    val id: AnimeId,

    /** All known titles */
    val title: AnimeTitle,

    /** Cleaned description (no raw HTML) */
    val description: String,

    /** Genres like Action, Adventure, etc */
    val genres: List<String> = emptyList(),

    /** Extra tags / themes (free-form) */
    val tags: List<String> = emptyList(),

    /** TV, Movie, OVA, etc */
    val type: AnimeType,

    /** Ongoing / Finished / Upcoming */
    val status: AnimeStatus,

    /** Age rating (PG-13, R, etc) */
    val rating: ContentRating? = null,

    /** Average score (0–100 or 0–10 depending on source) */
    val score: Float? = null,

    /** Release season */
    val season: Season? = null,

    /** Studio / production house */
    val studio: String? = null,

    /** Episode duration in milliseconds */
    val episodeDurationMs: Long? = null,

    /** Posters / covers */
    val posters: ImageSet? = null,

    /** Large banner image */
    val banner: String? = null
)