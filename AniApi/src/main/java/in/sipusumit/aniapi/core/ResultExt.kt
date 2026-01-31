package `in`.sipusumit.aniapi.core

fun Throwable.toAnimeError(sourceId: String? = null): AnimeError =
    when (this) {
        is AnimeException -> error

        is java.net.UnknownHostException,
        is java.net.SocketTimeoutException ->
            AnimeError.Network

        is kotlinx.serialization.SerializationException ->
            AnimeError.Parsing(message)

        else ->
            AnimeError.Internal(
                message = message ?: "Unknown error",
                cause = this
            )
    }

/**
 * Maps an exception inside Result to AnimeError.
 */
inline fun <T> Result<T>.mapError(
    mapper: (Throwable) -> AnimeError
): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = {
            val error = mapper(it)
            Result.failure(AnimeException(error, it))
        }
    )

/**
 * Returns value or null if failed.
 */
fun <T> Result<T>.getOrNullSafe(): T? =
    getOrNull()

/**
 * Returns AnimeError if failed, null otherwise.
 */
fun <T> Result<T>.errorOrNull(): AnimeError? =
    (exceptionOrNull() as? AnimeException)?.error

/**
 * Runs a suspend block and converts thrown exceptions
 * into AnimeError automatically.
 */
suspend inline fun <T> runAnimeCatching(
    sourceId: String?,
    block: suspend () -> T
): Result<T> =
    try {
        Result.success(block())
    } catch (t: Throwable) {
        val error = t.toAnimeError(sourceId)
        Result.failure(AnimeException(error, t))
    }


inline fun <T> runSearchCatching(
    query: String,
    block: () -> T
): Result<T> =
    runCatching(block)
        .mapError { AnimeError.SearchError(query, it) }

/**
 * Throws if Result is failure.
 * Intended for internal chaining only.
 */
fun <T> Result<T>.requireSuccess(): T =
    getOrElse { throw it }

