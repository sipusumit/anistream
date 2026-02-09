package `in`.sipusumit.aniapi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    UPCOMING,

    RELEASING,
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

@Deprecated(
    message = "use enumOrDefault() instead",
    replaceWith = ReplaceWith("enumOrDefault(this, default)", "in.sipusumit.aniapi.model.enumOrDefault")
)
inline fun <reified T : Enum<T>> String.toEnumOrDefault(default: T): T =
    enumValues<T>().firstOrNull { it.name.equals(this, ignoreCase = true) }
        ?: default
//
inline fun <reified T: Enum<T>> enumOrDefault(from: String?, default: T): T{
    if (from == null) return default
    val normalized = from
        .trim()
        .replace('-', '_')
        .replace(' ', '_')
        .uppercase()

    return enumValues<T>().firstOrNull { it.name == normalized }
        ?: default
}