package `in`.sipusumit.aniapi.source.allanime

import `in`.sipusumit.aniapi.core.LanguageMode
import `in`.sipusumit.aniapi.model.*
import kotlinx.serialization.json.*

internal object AllAnimeMapper {
    fun mapSearchResults(root: JsonElement): List<AnimeSummary> {
        val edges = root
            .jsonObject["data"]
            ?.jsonObject
            ?.get("shows")
            ?.jsonObject
            ?.get("edges")
            ?.jsonArray
            ?: return emptyList()

        return edges.mapNotNull { node ->
            val obj = node.jsonObject

            val id = obj["_id"]?.jsonPrimitive?.content ?: return@mapNotNull null
            val title = obj["name"]?.jsonPrimitive?.content ?: return@mapNotNull null

            AnimeSummary(
                id = AnimeId(id),
                title = AnimeTitle(
                    primary = title,
                    english = obj["englishName"]?.jsonPrimitive?.contentOrNull,
                    native = obj["nativeName"]?.jsonPrimitive?.contentOrNull
                ),
                poster = obj["thumbnail"]?.jsonPrimitive?.contentOrNull
                    ?.let { ImageSet(large = it) },
                type = parseType(obj["type"]),
                status = parseStatus(obj["status"]),
                score = obj["score"]?.jsonPrimitive?.floatOrNull,
                episodeCount = parseEpisodeCount(obj["availableEpisodes"])
            )
        }
    }
    fun mapAnimeDetails(root: JsonElement): AnimeDetails {
        val show = root
            .jsonObject["data"]
            ?.jsonObject
            ?.get("show")
            ?.jsonObject
            ?: error("Invalid anime details response")

        return AnimeDetails(
            id = AnimeId(show["_id"]!!.jsonPrimitive.content),
            title = AnimeTitle(
                primary = show["name"]!!.jsonPrimitive.content,
                english = show["englishName"]?.jsonPrimitive?.contentOrNull,
                native = show["nativeName"]?.jsonPrimitive?.contentOrNull,
                alternatives = emptyList()
            ),
            description = show["description"]
                ?.jsonPrimitive
                ?.contentOrNull
                ?.replace(Regex("<[^>]*>"), "")
                ?: "",
            genres = show["genres"]?.jsonArray?.mapNotNull {
                it.jsonPrimitive.contentOrNull
            } ?: emptyList(),
            tags = show["tags"]?.jsonArray?.mapNotNull {
                it.jsonPrimitive.contentOrNull
            } ?: emptyList(),
            type = parseType(show["type"]),
            status = parseStatus(show["status"]),
            rating = parseRating(show["rating"]),
            score = show["score"]?.jsonPrimitive?.intOrNull,
            season = parseSeason(show["season"]),
            studio = show["studios"]?.jsonArray
                ?.firstOrNull()
                ?.jsonPrimitive
                ?.contentOrNull,
            episodeDurationMs = show["episodeDuration"]
                ?.jsonPrimitive
                ?.longOrNull,
            posters = show["thumbnail"]
                ?.jsonPrimitive
                ?.contentOrNull
                ?.let { ImageSet(large = it) },
            banner = show["banner"]?.jsonPrimitive?.contentOrNull
        )
    }
    fun mapEpisodes(
        root: JsonElement,
        mode: LanguageMode
    ): List<Episode> {

        val list = root
            .jsonObject["data"]
            ?.jsonObject
            ?.get("show")
            ?.jsonObject
            ?.get("availableEpisodesDetail")
            ?.jsonObject
            ?.get(mode.apiValue)
            ?.jsonArray
            ?: return emptyList()

        return list.mapNotNull { ep ->
            val num = ep.jsonPrimitive.contentOrNull ?: return@mapNotNull null
            Episode(
                number = EpisodeNumber(num),
                isAvailable = true,
                releaseDate = null
            )
        }
    }
    private fun parseEpisodeCount(el: JsonElement?): EpisodeCount? {
        val obj = el?.jsonObject ?: return null
        return EpisodeCount(
            sub = obj["sub"]?.jsonPrimitive?.intOrNull,
            dub = obj["dub"]?.jsonPrimitive?.intOrNull
        )
    }
    private fun parseType(el: JsonElement?): AnimeType =
        when (el?.jsonPrimitive?.contentOrNull?.uppercase()) {
            "TV" -> AnimeType.TV
            "MOVIE" -> AnimeType.MOVIE
            "OVA" -> AnimeType.OVA
            "ONA" -> AnimeType.ONA
            "SPECIAL" -> AnimeType.SPECIAL
            else -> AnimeType.TV
        }
    private fun parseStatus(el: JsonElement?): AnimeStatus =
        when (el?.jsonPrimitive?.contentOrNull?.uppercase()) {
            "FINISHED" -> AnimeStatus.FINISHED
            "RELEASING", "ONGOING" -> AnimeStatus.ONGOING
            "UPCOMING" -> AnimeStatus.UPCOMING
            else -> AnimeStatus.ONGOING
        }
    private fun parseRating(el: JsonElement?): ContentRating? =
        when (el?.jsonPrimitive?.contentOrNull) {
            "G" -> ContentRating.G
            "PG" -> ContentRating.PG
            "PG-13" -> ContentRating.PG_13
            "R" -> ContentRating.R
            "R+" -> ContentRating.R_PLUS
            "RX" -> ContentRating.RX
            else -> null
        }
    private fun parseSeason(el: JsonElement?): Season? {
        val obj = el?.jsonObject ?: return null
        val year = obj["year"]?.jsonPrimitive?.intOrNull ?: return null
        val quarter = when (obj["quarter"]?.jsonPrimitive?.contentOrNull) {
            "Winter" -> SeasonQuarter.WINTER
            "Spring" -> SeasonQuarter.SPRING
            "Summer" -> SeasonQuarter.SUMMER
            "Fall" -> SeasonQuarter.FALL
            else -> return null
        }
        return Season(year, quarter)
    }

    fun mapHomeScreenResults(el: JsonElement?): HomeSection {
        val obj = el?.jsonObject ?: return HomeSection("", emptyList(), 0)
        return mapPopularSection(obj)
    }

    fun mapPopularSection(root: JsonObject): HomeSection {
        val queryPopular = root["data"]
            ?.jsonObject
            ?.get("queryPopular")
            ?.jsonObject
            ?: error("Missing queryPopular")

        val total = queryPopular["total"]
            ?.jsonPrimitive
            ?.int
            ?: 0

        val recommendations = queryPopular["recommendations"]
            ?.jsonArray
            ?: emptyList()

        val entries = recommendations.mapIndexedNotNull { index, element ->
            mapHomeEntry(element.jsonObject, rank = index + 1)
        }

        return HomeSection(
            title = "Popular Anime",
            entries = entries,
            total = total
        )
    }

    private fun mapHomeEntry(
        obj: JsonObject,
        rank: Int
    ): HomeEntry? {

        val anyCard = obj["anyCard"]?.jsonObject ?: return null
        val pageStatus = obj["pageStatus"]?.jsonObject

        val anime = mapAnimeSummaryFromCard(anyCard) ?: return null

        val views = pageStatus
            ?.get("views")
            ?.jsonPrimitive
            ?.content
            ?.toLongOrNull()
            ?: 0L

        val trendingViews = pageStatus
            ?.get("rangeViews")
            ?.jsonPrimitive
            ?.content
            ?.toLongOrNull()
            ?: 0L

        return HomeEntry(
            anime = anime,
            rank = rank,
            views = views,
            trendingViews = trendingViews
        )
    }

    private fun mapAnimeSummaryFromCard(card: JsonObject): AnimeSummary? {
        val id = card["_id"]?.jsonPrimitive?.content ?: return null
        val name = card["name"]?.jsonPrimitive?.content ?: return null

        return AnimeSummary(
            id = AnimeId(id),
            title = AnimeTitle(
                primary = name,
                english = card["englishName"]?.jsonPrimitive?.contentOrNull,
                native = card["nativeName"]?.jsonPrimitive?.contentOrNull
            ),
            poster = card["thumbnail"]
                ?.jsonPrimitive
                ?.contentOrNull
                ?.let { ImageSet(large = it) },

            type = parseType(card["type"]),
            status = parseStatus(card["status"]),
            score = card["score"]?.jsonPrimitive?.floatOrNull,
            episodeCount = parseEpisodeCount(card["availableEpisodes"])
        )
    }
}
