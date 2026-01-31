package `in`.sipusumit.aniapi.model

/**
 * Stable identifier for an anime within a source.
 *
 * This is a value class to prevent accidental misuse
 * of raw strings across the codebase.
 */
@JvmInline
value class AnimeId(
    val value: String
) {
    init {
        require(value.isNotBlank()) {
            "AnimeId cannot be blank"
        }
    }

    override fun toString(): String = value
}