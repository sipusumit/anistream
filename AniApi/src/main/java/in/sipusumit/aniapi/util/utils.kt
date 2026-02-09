package `in`.sipusumit.aniapi.util

fun <T> reorderByIds(
    ids: List<String>,
    items: List<T>,
    idSelector: (T) -> String?
): List<T> {
    val map = items.associateBy { idSelector(it) }
    return ids.mapNotNull { map[it] }
}