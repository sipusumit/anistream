package `in`.sipusumit.aniapi.model

/**
 * Represents a relationship between two anime.
 *
 * Used for sequels, prequels, spin-offs, etc.
 */
data class RelatedAnime(

    /** The related anime */
    val anime: AnimeSummary,

    /** Relationship type */
    val relation: RelationType
)

/**
 * Type of relationship between anime.
 */
enum class RelationType {
    SEQUEL,
    PREQUEL,
    SPIN_OFF,
    SIDE_STORY,
    SUMMARY,
    ALTERNATIVE,
    ADAPTATION,
    OTHER
}
