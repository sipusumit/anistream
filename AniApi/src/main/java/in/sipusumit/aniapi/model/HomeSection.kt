package `in`.sipusumit.aniapi.model

data class HomeSection(
    val type: HomeSectionType,
    val entries: List<HomeEntry>,
    val total: Int
)

enum class HomeSectionType {
    CAROUSEL,
    TRENDING,

    UNKNOWN
}
