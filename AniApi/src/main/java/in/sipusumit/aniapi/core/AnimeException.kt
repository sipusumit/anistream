package `in`.sipusumit.aniapi.core

class AnimeException(
    val error: AnimeError,
    cause: Throwable? = null
) : Exception(
    when (error) {
        is AnimeError.Internal -> error.message
        is AnimeError.Source -> error.message
        is AnimeError.Parsing -> error.message
        else -> error.userMessage()
    },
    cause
)