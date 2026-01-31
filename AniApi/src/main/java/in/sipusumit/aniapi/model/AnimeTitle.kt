package `in`.sipusumit.aniapi.model

/**
 * Represents all known titles of an anime.
 *
 * UI should choose which one to display.
 */
data class AnimeTitle(

    /** Primary title (source default) */
    val primary: String,

    /** English title if available */
    val english: String? = null,

    /** Native / original title if available */
    val native: String? = null,

    /** Alternate / synonym titles */
    val alternatives: List<String> = emptyList()
)