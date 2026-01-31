package `in`.sipusumit.aniapi.model

data class HomeSection(
    val title: String,
    val entries: List<HomeEntry>,
    val total: Int
)