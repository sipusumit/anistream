package `in`.sipusumit.aniapi.model

/**
 * Optional filters applied during anime search.
 *
 * Sources may partially support these filters.
 */
data class SearchFilters(

    /** Filter by genres (Action, Comedy, etc) */
    val genres: Set<String> = emptySet(),

    /** Filter by anime type (TV, Movie, OVA, etc) */
    val type: AnimeType? = null,

    /** Filter by release status */
    val status: AnimeStatus? = null,

    /** Filter by release year */
    val year: Int? = null
)