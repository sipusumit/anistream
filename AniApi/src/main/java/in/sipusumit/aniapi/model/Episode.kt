package `in`.sipusumit.aniapi.model

import java.time.Instant

/**
 * Represents a single episode of an anime.
 *
 * Designed to be UI-friendly and source-agnostic.
 */
data class Episode(

    /** Episode identifier (e.g. "1", "12.5", "OVA") */
    val number: EpisodeNumber,

    /** Optional episode title (many sources don't provide this yet) */
    val title: String? = null,

    /** Whether this episode is currently available to watch */
    val isAvailable: Boolean = true,

    /** Release date/time if known */
    val releaseDate: Instant? = null
)