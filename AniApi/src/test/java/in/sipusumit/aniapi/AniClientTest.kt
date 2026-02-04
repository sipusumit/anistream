package `in`.sipusumit.aniapi

import com.apollographql.apollo.ApolloClient
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.model.AnimeStatus
import `in`.sipusumit.aniapi.model.AnimeSummary
import `in`.sipusumit.aniapi.model.AnimeTitle
import `in`.sipusumit.aniapi.model.AnimeType
import `in`.sipusumit.aniapi.model.HomeEntry
import `in`.sipusumit.aniapi.model.ImageSet
import `in`.sipusumit.aniapi.source.allanime.AllAnimeSource
import `in`.sipusumit.aniapi.source.allanime.graphql.HomePopularQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.type.VaildPopularTypeEnumType
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertTrue

class AniClientTest {
    @Test
    fun getHomeScreen() = runTest {
        val source = AllAnimeSource()
        val results = source.getHomeScreen()
        println(results)
    }

    @Test
    fun searchReturnsResult() = runTest {
        val source = AllAnimeSource()
        val results = source.search("one piece")
        results.getOrThrow().take(5).forEach {
            println("${it.title.primary} | sub=${it.episodeCount?.sub} | dub=${it.episodeCount?.dub}")
        }
        assertTrue(results.getOrThrow().isNotEmpty())
    }

    @Test
    fun debugStreams() = runTest {
        val source = AllAnimeSource()

        // 1) Search
        val searchResult = source.search("one piece")
            .getOrElse { error("Search failed: $it") }

        val anime = searchResult.firstOrNull()
            ?: error("No anime found")

        println("ANIME: ${anime.title.primary} (${anime.id.value})")

        // 2) Episodes
        val episodes = source.getEpisodes(anime.id)
            .getOrElse { error("Episode fetch failed: $it") }

        println("TOTAL EPISODES: ${episodes.size}")

        // 3) Streams (limit for speed)
        episodes.take(2).forEach { episode ->
            val streams = source.getStreams(anime.id, episode.number)
                .getOrElse { error("Streams failed for EP ${episode.number}: $it") }

            println("\nEP ${episode.number} -> ${streams.size} streams")

            streams.forEach { stream ->
                println(
                    "  ${stream.quality.label} | ${stream.format} | ${stream.url}"
                )
            }
        }
    }

    @Test
    fun getDescription() = runTest {
        val source = AllAnimeSource()

        val res = source.getAnimeDetails(AnimeId("ReooPAxPMsHM4KPMY"))
            .getOrElse { error("Anime Detail fetch failed: $it") }

        println(res)
    }


    @Test
    fun apolloClientTest() = runTest {
        val apolloClient = ApolloClient.Builder()
        .serverUrl("https://api.allanime.day/api")
        .addHttpHeader("Referer", "https://allanime.to")
        .build()

        val res = apolloClient.query(HomePopularQuery(
            type = VaildPopularTypeEnumType.anime,
            size = 20,
            page = 1,
            dateRange = 1,
            allowAdult = true,
            allowUnknown = false
        )).execute().data
        val entry = res?.queryPopular?.recommendations?.map {
            it.anyCard?._id?.let { value ->
                HomeEntry(
                    AnimeSummary(
                        id = AnimeId(value),
                        title = AnimeTitle(it.anyCard.name ?: "", it.anyCard.englishName, it.anyCard.nativeName),
                        poster = ImageSet(large = it.anyCard.thumbnail),
                        type = AnimeType.TV,
                        status = AnimeStatus.FINISHED,
                    ),
                    rank = 8,
                    views = 22,
                    trendingViews = 22
                )
            }
        }
        println(entry)
    }
}