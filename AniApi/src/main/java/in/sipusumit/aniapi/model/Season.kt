package `in`.sipusumit.aniapi.model

/**
 * Represents an anime release season.
 */
data class Season(
    val year: Int,
    val quarter: SeasonQuarter
)

/**
 * Anime season quarter.
 */
enum class SeasonQuarter {
    WINTER,
    SPRING,
    SUMMER,
    FALL
}