package `in`.sipusumit.aniapi.model

/**
 * Type / format of the anime.
 */
enum class AnimeType {
    TV,
    MOVIE,
    OVA,
    ONA,
    SPECIAL
}

/**
 * Release status of the anime.
 */
enum class AnimeStatus {
    ONGOING,
    FINISHED,
    UPCOMING
}

/**
 * Age rating of the anime.
 *
 * Based on common MAL / AniList style ratings.
 */
enum class ContentRating {
    G,        // All ages
    PG,       // Parental guidance
    PG_13,    // Teens 13+
    R,        // 17+ (violence / profanity)
    R_PLUS,   // Mild nudity
    RX        // Explicit
}
