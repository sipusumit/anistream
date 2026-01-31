package `in`.sipusumit.aniapi.core

/**
 * Represents all possible errors that can occur
 * when interacting with an AnimeSource.
 *
 * This type is UI-safe and source-agnostic.
 */
sealed interface AnimeError {

    /** Network unavailable, timeout, DNS failure, etc */
    data object Network : AnimeError

    /** Requested anime / episode does not exist */
    data object NotFound : AnimeError

    /** Anime exists but content is not available yet */
    data object Unavailable : AnimeError

    /** Failed to parse server response */
    data class Parsing(
        val message: String? = null
    ) : AnimeError

    /**
     * Source-specific failure
     * (rate limit, Cloudflare, provider bug, etc)
     */
    data class Source(
        val sourceId: String,
        val message: String? = null
    ) : AnimeError

    /** Internal library error (bug / unexpected state) */
    data class Internal(
        val message: String,
        val cause: Throwable? = null
    ) : AnimeError

    data class SearchError(
        val query: String,
        val cause: Throwable? = null
    ) : AnimeError
}

fun AnimeError.userMessage(): String = when (this) {
    AnimeError.Network ->
        "No internet connection"

    AnimeError.NotFound ->
        "Anime not found"

    AnimeError.Unavailable ->
        "Content not available yet"

    is AnimeError.Parsing ->
        "Failed to read server response"

    is AnimeError.Source ->
        message ?: "Source error ($sourceId)"

    is AnimeError.Internal ->
        message

    is AnimeError.SearchError ->
        "Search Error:\nquery: $query\n"
}
