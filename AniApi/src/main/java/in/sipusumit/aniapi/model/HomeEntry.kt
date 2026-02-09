package `in`.sipusumit.aniapi.model

data class HomeEntry(
    val anime: AnimeSummary,
    val rank: Int,
    val views: Long?,
    val trendingViews: Long
)