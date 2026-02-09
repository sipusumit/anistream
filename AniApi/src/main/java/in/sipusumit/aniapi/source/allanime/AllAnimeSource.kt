package `in`.sipusumit.aniapi.source.allanime

import `in`.sipusumit.aniapi.core.runAnimeCatching
import `in`.sipusumit.aniapi.model.*
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.core.LanguageMode
import `in`.sipusumit.aniapi.core.runSearchCatching
import `in`.sipusumit.aniapi.network.HttpClientProvider
import `in`.sipusumit.aniapi.network.UserAgents
import `in`.sipusumit.aniapi.source.allanime.AllAnimeEndpoints.REFERER
import `in`.sipusumit.aniapi.source.allanime.decoder.ProviderDecoder
import `in`.sipusumit.aniapi.util.reorderByIds
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.serialization.json.*
import java.net.URL


//TODO: Use Apollo Client
class AllAnimeSource : AnimeSource {

    override val sourceId: String = "allanime"
    override val displayName: String = "AllAnime"

    private val client = AllAnimeClient()

    private val apolloClient = AllAnimeApolloClient()

    override suspend fun getHomeScreen(): Result<HomeSection> = runAnimeCatching(null){
        //TODO: return list of HomeSection
//        val json = client.getHomeScreen()
//        AllAnimeMapper.mapHomeScreenResults(json)

        // Carousel
//        val resIds = apolloClient.query(
//            HomePopularIdsQuery(
//                type = VaildPopularTypeEnumType.anime,
//                size = 10,
//                page = 1,
//                dateRange = 1,
//                allowAdult = true,
//                allowUnknown = true
//            )
//        ).execute().data?.queryPopular?.recommendations?.toIds().orEmpty()
//
//        val res = apolloClient.query(GetAnimeDetailsQuery(resIds)).execute().data
        val res = apolloClient.getHomeScreen()
        println(res)

        HomeSection(HomeSectionType.CAROUSEL, res?.showsWithIds?.mapNotNull { it?.toHomeEntry() }.orEmpty(), 0)
    }

    override suspend fun search(
        query: String,
        filters: SearchFilters
    ): Result<List<AnimeSummary>> =
        runSearchCatching(query) {
//            val json = client.searchAnime(
//                query = query,
//                mode = LanguageMode.SUB.apiValue
//            )
            val (resIds, res) = apolloClient.searchAnime(query, LanguageMode.SUB.apiValue)
            val data = res?.showsWithIds?.mapNotNull { it?.toAnimeSummary() }.orEmpty()
            reorderByIds(ids = resIds, items = data, idSelector = {it.id.value})
//            AllAnimeMapper.mapSearchResults(json)

        }

    override suspend fun getAnimeDetails(
        id: AnimeId
    ): Result<AnimeDetails> =
        runAnimeCatching(id.value) {
//            val json = client.getAnimeDetails(id.value)
//            AllAnimeMapper.mapAnimeDetails(json)
            apolloClient.getAnimeDetails(id.value)?.show?.toAnimeDetails() as AnimeDetails
        }
    override suspend fun getEpisodes(
        id: AnimeId,
        language: LanguageMode
    ): Result<List<Episode>> =
        runAnimeCatching(id.value) {
//            val json = client.getEpisodes(
//                animeId = id.value,
////                mode = language.apiValue
//            )

//            AllAnimeMapper.mapEpisodes(json, language)
            val res = apolloClient.getEpisodes(id.value)
            val list = res?.show?.availableEpisodesDetail?.jsonObject?.get(language.apiValue)?.jsonArray ?: return@runAnimeCatching emptyList()
            list.mapNotNull { ep ->
                val num = ep.jsonPrimitive.contentOrNull ?: return@mapNotNull null
                Episode(
                    number = EpisodeNumber(num),
                    isAvailable = true,
                    releaseDate = null
                )
            }
        }
    override suspend fun getStreams(
        id: AnimeId,
        episode: EpisodeNumber,
        language: LanguageMode
    ): Result<List<Stream>> =
        runAnimeCatching(id.value) {

//            val episodeData = client.getEpisodeSources(
//                animeId = id.value,
//                episode = episode.value,
//                mode = language.apiValue
//            ) as JsonObject
            val sourceUrls = apolloClient.getEpisodeSources(id.value, episode.value, language)?.episode?.sourceUrls

            extractStreamsFromEpisode(sourceUrls)
//            extractStreamsFromEpisode(episodeData["data"]?.jsonObject["episode"]?.jsonObject as JsonObject)
        }

    suspend fun extractStreamsFromEpisode(
        episodeData: JsonElement?
    ): List<Stream> {

        val streams = mutableListOf<Stream>()

//        val sourceUrls = episodeData["sourceUrls"]
        println("episodeData: $episodeData")
        val sourceUrls = episodeData
            ?.toSourceUrlsSafe()
            ?.sortedByDescending { it.priority ?: 0.0 }
            ?: return emptyList()

        for (src in sourceUrls) {

            val encoded = src.sourceUrl
            if (!encoded.startsWith("--")) continue

            val decoded = ProviderDecoder.decode(encoded.removePrefix("--"))

            val providerUrl =
                if (decoded.startsWith("http")) decoded
                else "https://allanime.day$decoded"

            // ---------- FAST DIRECT PROVIDERS ----------
            if (providerUrl.contains("fast4speed")) {
                streams += Stream(
                    url = providerUrl,
                    quality = StreamQuality.AUTO,
                    format = StreamFormat.MP4,
                    headers = mapOf("Referer" to REFERER),
                    provider = src.sourceName
                )
                continue // ❗ changed from break
            }

            val response = HttpClientProvider.client.get(providerUrl) {
                header("Referer", REFERER)
                header("User-Agent", UserAgents.DEFAULT)
                timeout { requestTimeoutMillis = 15_000 }
            }

            val contentType = response.headers["Content-Type"].orEmpty()

            // ---------- CASE 1: DIRECT VIDEO ----------
            if (
                contentType.startsWith("video") ||
                contentType == "application/octet-stream"
            ) {
                streams += Stream(
                    url = providerUrl,
                    quality = StreamQuality.AUTO,
                    format = StreamFormat.MP4,
                    headers = mapOf("Referer" to REFERER),
                    provider = src.sourceName
                )
                continue
            }

            // ---------- CASE 2: JSON (clock.json) ----------
            if (contentType.startsWith("application/json")) {

                val clock = response.body<JsonObject>()
                val links = clock["links"]?.jsonArray.orEmpty()

                for (item in links) {
                    val link = item.jsonObject["link"]
                        ?.jsonPrimitive
                        ?.content
                        ?: continue

                    val resLabel = item.jsonObject["resolutionStr"]
                        ?.jsonPrimitive
                        ?.content

                    streams += Stream(
                        url = link,
                        quality = resLabel?.let {
                            StreamQuality(it, it.filter(Char::isDigit).toIntOrNull())
                        } ?: StreamQuality.AUTO,
                        format = if (link.endsWith(".m3u8")) StreamFormat.HLS else StreamFormat.MP4,
                        headers = mapOf("Referer" to REFERER),
                        provider = src.sourceName
                    )
                }
            }
        }

        return streams
    }

//    suspend fun extractStreamsFromEpisode(
//        episodeData: JsonObject
//    ): List<Stream> {
////        println("Episode Data: $episodeData")
//
//        val streams = mutableListOf<Stream>()
//
//        val sources = episodeData["sourceUrls"]
//            ?.jsonArray
//            ?: return emptyList()
////        println("Sources: $sources")
//
//
//        for (src in sources) {
//
//            val encoded = src.jsonObject["sourceUrl"]
//                ?.jsonPrimitive
//                ?.content
//                ?: continue
//
//            // Only encoded providers
//            if (!encoded.startsWith("--")) continue
//
//            val decoded = ProviderDecoder.decode(encoded.removePrefix("--"))
//
//            val providerUrl =
//                if (decoded.startsWith("http")) decoded
//                else "https://allanime.day/$decoded"
//
////            println("PROVIDER: $providerUrl")
//
//            if(providerUrl.contains("fast4speed")){
//                streams += Stream(
//                    url = providerUrl,
//                    quality = StreamQuality.AUTO,
//                    format = StreamFormat.MP4,
//                    headers = mapOf("Referer" to REFERER)
//                )
//                break // ani-cli behavior
//            }
//            val response = HttpClientProvider.client.get(providerUrl) {
//                header("Referer", REFERER)
//                header("User-Agent", UserAgents.DEFAULT)
//                timeout {
//                    requestTimeoutMillis = 15_000
//                }
//            }
//
//            val contentType = response.headers["Content-Type"] ?: ""
//
//            /* ---------------------------------------------------------
//             * CASE 1: DIRECT VIDEO (fast4speed, etc.)
//             * --------------------------------------------------------- */
//            if (
//                contentType.startsWith("video") ||
//                contentType == "application/octet-stream"
//            ) {
//                streams += Stream(
//                    url = providerUrl,
//                    quality = StreamQuality.AUTO,
//                    format = StreamFormat.MP4,
//                    headers = mapOf("Referer" to REFERER)
//                )
//                break // ani-cli behavior
//            }
//
//            /* ---------------------------------------------------------
//             * CASE 2: clock.json (JSON response)
//             * --------------------------------------------------------- */
//            if (contentType.startsWith("application/json")) {
//
//                val clock = response.body<JsonObject>()
//
//                val links = clock["links"]
//                    ?.jsonArray
//                    ?: emptyList()
//
//                for (item in links) {
//                    val link = item.jsonObject["link"]
//                        ?.jsonPrimitive
//                        ?.content
//                        ?: continue
//
//                    val resLabel = item.jsonObject["resolutionStr"]
//                        ?.jsonPrimitive
//                        ?.content
//
//                    streams += Stream(
//                        url = link,
//                        quality = resLabel?.let {
//                            StreamQuality(it, it.filter(Char::isDigit).toIntOrNull())
//                        } ?: StreamQuality.AUTO,
//                        format = if (link.endsWith(".m3u8"))
//                            StreamFormat.HLS
//                        else
//                            StreamFormat.MP4,
//                        headers = mapOf("Referer" to REFERER)
//                    )
//                }
//
//                break // ani-cli behavior
//            }
//        }
//
//        return streams
//    }


    override suspend fun getRelated(
        id: AnimeId
    ): Result<List<RelatedAnime>> =
        runAnimeCatching(sourceId) {
            emptyList()
        }

}
