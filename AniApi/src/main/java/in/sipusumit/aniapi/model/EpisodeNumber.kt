package `in`.sipusumit.aniapi.model

/**
 * Represents an episode identifier.
 *
 * Uses String internally because some sources
 * use values like "12.5", "OVA", "SP", etc.
 */
@JvmInline
value class EpisodeNumber(
    val value: String
) {
    init {
        require(value.isNotBlank()) {
            "EpisodeNumber cannot be blank"
        }
    }

    override fun toString(): String = value
}