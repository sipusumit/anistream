package `in`.sipusumit.aniapi.source.allanime

import `in`.sipusumit.aniapi.model.AnimeDetails
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.model.AnimeStatus
import `in`.sipusumit.aniapi.model.AnimeSummary
import `in`.sipusumit.aniapi.model.AnimeTitle
import `in`.sipusumit.aniapi.model.AnimeType
import `in`.sipusumit.aniapi.model.ContentRating
import `in`.sipusumit.aniapi.model.EpisodeCount
import `in`.sipusumit.aniapi.model.HomeEntry
import `in`.sipusumit.aniapi.model.ImageSet
import `in`.sipusumit.aniapi.model.Season
import `in`.sipusumit.aniapi.model.enumOrDefault
import `in`.sipusumit.aniapi.model.toEnumOrDefault
import `in`.sipusumit.aniapi.source.allanime.graphql.GetAnimeDetailQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.GetAnimeDetailsQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.HomePopularIdsQuery
import `in`.sipusumit.aniapi.source.allanime.model.SourceUrl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

private val json = Json {
    ignoreUnknownKeys = true
}

//fun HomePopularQuery.Recommendation.toHomeEntries(): HomeEntry{
//    return HomeEntry(
//        anime = AnimeSummary(
//            id = AnimeId(this.anyCard!!._id!!),
//            title = AnimeTitle(
//                primary = this.anyCard.name!!,
//                english = this.anyCard.englishName,
//                native = this.anyCard.nativeName,
//                alternatives = emptyList()
//            ),
//            poster = ImageSet(
//                large = this.anyCard.thumbnail
//            ),
//            type = AnimeType.valueOf(this.anyCard.type ?: "TV"),
////            type = AnimeType.TV,
//            status = AnimeStatus.valueOf(anyCard.status ?: "FINISHED"),
//            score = this.anyCard.score?.toFloat(),
//            episodeCount = EpisodeCount(
//                sub = 0,
//                dub = 0
//            ), // TODO
//            malId = this.anyCard.malId
//        ),
//        rank = 0,
//        views = 0,
//        trendingViews = 0
//    )
//}

fun JsonElement.toSourceUrls(): List<SourceUrl> =
    runCatching {
        json.decodeFromJsonElement<List<SourceUrl>>(this)
    }.getOrDefault(emptyList())

fun JsonElement.toSourceUrlsSafe(): List<SourceUrl> =
    when (this) {
        is JsonArray -> json.decodeFromJsonElement(this)
        is JsonObject -> listOf(json.decodeFromJsonElement(this))
        else -> emptyList()
    }

//@JvmName("popularToIds")
fun List<HomePopularIdsQuery.Recommendation>.toIds(): List<String>{
    return mapNotNull {
        it.anyCard?._id
    }
}

fun JsonElement.toEpisodeCount(): EpisodeCount {
//    return try {
       return json.decodeFromJsonElement<EpisodeCount>(this)
//    } catch (e: Exception) {
//        null
//    }
}


fun GetAnimeDetailsQuery.ShowsWithId.toAnimeSummary(): AnimeSummary {
    return AnimeSummary(
        id = AnimeId(_id!!),
        title = AnimeTitle(
            primary = name ?: "",
            english = englishName,
            native = nativeName,
            alternatives = altNames?.filterNotNull().orEmpty()
        ),
        poster = ImageSet(
            large = thumbnail
        ),
//            type = AnimeType.valueOf(type ?: "TV"),
        type = AnimeType.TV,
        status = enumOrDefault<AnimeStatus>(status, AnimeStatus.FINISHED),
        score = score?.toFloat(),
        episodeCount = availableEpisodes?.toEpisodeCount(),
        malId = malId
    )
}

fun GetAnimeDetailsQuery.ShowsWithId.toHomeEntry(): HomeEntry{
    return HomeEntry(
        anime = toAnimeSummary(),
        rank = 1,
        views = pageStatus?.views,
        trendingViews = pageStatus?.rangeViews ?: 0L
    )
}

fun GetAnimeDetailQuery.Show.toAnimeDetails(): AnimeDetails{
    return AnimeDetails(
        id = AnimeId(_id!!),
        title = AnimeTitle(
            primary = name ?: "",
            english = englishName,
            native = nativeName,
            alternatives = altNames?.filterNotNull().orEmpty()
        ),
        description = description ?: "",
//        genres = ,
//        tags = ,
        type = enumOrDefault(type,AnimeType.TV),
        status = enumOrDefault(status, AnimeStatus.FINISHED),
        rating = enumOrDefault(rating, ContentRating.R),
        score = score?.toFloat(),
        season = json.decodeFromJsonElement<Season>(season!!),
        studio = studios?.filterNotNull().orEmpty().first(),
        episodeDurationMs = 0L,
        posters = ImageSet(
            large = thumbnail
        ),
        banner = banner
    )
}