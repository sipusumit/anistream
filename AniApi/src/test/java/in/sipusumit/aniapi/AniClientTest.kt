package `in`.sipusumit.aniapi

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.source.allanime.AllAnimeSource
import `in`.sipusumit.aniapi.source.allanime.decoder.ProviderDecoder
import `in`.sipusumit.aniapi.source.allanime.graphql.GetAnimeDetailsQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.HomePopularIdsQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.HomeTrendingQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.SearchQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.type.Object
import `in`.sipusumit.aniapi.source.allanime.graphql.type.QueryPageInput
import `in`.sipusumit.aniapi.source.allanime.graphql.type.SearchInput
import `in`.sipusumit.aniapi.source.allanime.graphql.type.VaildPopularTypeEnumType
import `in`.sipusumit.aniapi.source.allanime.toAnimeSummary
import `in`.sipusumit.aniapi.source.allanime.toIds
import `in`.sipusumit.aniapi.util.JsonElementScalarAdapter
import `in`.sipusumit.aniapi.util.reorderByIds
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Before
import kotlin.collections.orEmpty

class AniClientTest {
    lateinit var apolloClient: ApolloClient

    @Before
    fun setup(){
        apolloClient = ApolloClient.Builder()
            .serverUrl("https://api.allanime.day/api")
            .addHttpHeader("Referer", "https://allanime.to")
            .addCustomScalarAdapter(
                customScalarType = Object.type,
                customScalarAdapter = JsonElementScalarAdapter
            )
            .build()

    }

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
//        println(ProviderDecoder.decode("--175948514e4c4f57175b54575b5307515c050f5c0a0c0f0b0f0c0e590a0c0b5b0a0c0e0c0e0a0b0f0e0c0e0a0b090d010b0a0b0d0b5e0b090b5d0b5d0b5d0b0b0b0b0b0f0b0a0e0a0b0a0e0b0e0a0b0b0e0c0b0e0b0a0b5d0e0f0b0c0e0f0b090e0d0b5d0b0a0b0f0b5d0b090e0d0b0b0a0e0f590a0e0b0c0b090b0c0e0d0b0c0b0f0e0d0e0a0b090b0d0e0d0e0a0e0a0b0d0b0f0e080b0c0b5d0e0a0e0a0b0a0e0d0b090b0f0b5d0b0e0b5d0b0a0b0b0b0f0e0f0b5e0a0e0f590a0e0e5a0e0b0e0a0e5e0e0f0a010e0c0e0a0b0f0e0c0e0a0b090d010b0a0b0d0b5e0b090b5d0b5d0b5d0b0b0b0b0b0f0b0a0e0a0b0a0e0b0e0a0b0b0e0c0b0e0b0a0b5d0e0f0b0c0e0f0b090e0d0b5d0b0a0b0f0b5d0b090e0d0b0b0e080b0e0b0e0b0f0a000e5b0f0e0e090a0e0f590a0e0a590b0a0b5d0b0e0f0e0a590b090b0c0b0e0f0e0a590b0f0b0e0b5d0b0e0f0e0a590a0c0a590a0c0f0d0f0a0f0c0e0b0e0f0e5a0e0b0f0c0c5e0e0a0a0c0b5b0a0c0d090e5e0f5d0a0c0a590a0c0e0a0e0f0f0a0e0b0a0c0b5b0a0c0b0c0b0e0b0c0b080a5a0b0e0b0c0a5a0b0e0b5e0d0a0b0f0b0d0b5b0b0d0b5e0b5b0b0e0b0e0a000b0e0b0e0b0e0d5b0a0c0a590a0c0f0a0f0c0e0f0e000f0d0e590e0f0f0a0e5e0e010e000d0a0f5e0f0e0e0b0a0c0b5b0a0c0f0d0f0b0e0c0a0c0f5a"))
//        return@runTest
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
        episodes.takeLast(2).forEach { episode ->
            val streams = source.getStreams(anime.id, episode.number)
                .getOrElse { error("Streams failed for EP ${episode.number}: $it") }

            println("\nEP ${episode.number} -> ${streams.size} streams")

            streams.forEach { stream ->
                println(
                    "  ${stream.provider} ${stream.quality.label} | ${stream.format} | ${stream.url}"
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

//    @Test
//    fun apolloClientGetAnimeDetailsTest() = runTest {
//
//        val res = apolloClient.query(GetAnimeDetailsQuery("ReooPAxPMsHM4KPMY")).execute().data
//        println(res)
//    }


    @Test
    fun apolloClientHomePopularIdsQueryTest() = runTest {
        val res = apolloClient.query(
            HomePopularIdsQuery(
                type = VaildPopularTypeEnumType.anime,
                size = 10,
                page = 1,
                dateRange = 1,
                allowAdult = true,
                allowUnknown = true
            )
        ).execute().data
//        println(res)

        val ids = res?.queryPopular?.recommendations?.toIds()
        println(ids)
//        println(res?.queryPopular?.recommendations?.first()?.anyCard?.malId)
    }

    @Test
    fun apolloClientHomePopularTest() = runTest {
        val resIds = apolloClient.query(
            HomePopularIdsQuery(
                type = VaildPopularTypeEnumType.anime,
                size = 10,
                page = 1,
                dateRange = 1,
                allowAdult = true,
                allowUnknown = true
            )
        ).execute().data

        val ids = resIds?.queryPopular?.recommendations?.toIds()?.takeIf { it.isNotEmpty() }
            ?: error("IDs list is empty")
        println("Ids: $ids")
        val res = apolloClient.query(GetAnimeDetailsQuery(
            ids = ids
        )).execute()
        res.data?.showsWithIds?.forEach {
            println(it?.englishName)
        }
        assert(!res.data?.showsWithIds.isNullOrEmpty())
    }

    @Test
    fun apolloClientHomeTrendingQueryTest() = runTest {
        

        val input = QueryPageInput(
            type = VaildPopularTypeEnumType.anime,
            size = Optional.Present(12),
            page = Optional.Present(1),
            allowAdult = Optional.Present(true),
            allowUnknown = Optional.Present(false)
        )

        val res = apolloClient.query(
            HomeTrendingQuery(
                pageSearch = input
            )
        ).execute().data

        res?.queryLatestPageStatus?.recommendations?.forEach {
            println(it.anyCard?.englishName)
        }
    }

    @Test
    fun apolloClientSearchQueryTest() = runTest {
        val resIds = apolloClient.query(
            SearchQuery(
                search = Optional.Present(SearchInput(
                    query = Optional.Present("one piece")
                ))
            )
        ).execute().data?.shows?.edges?.mapNotNull { it._id }.orEmpty()

        println("Ids: $resIds")

        val res = apolloClient.query(GetAnimeDetailsQuery(resIds)).execute().data
        val data = res?.showsWithIds?.mapNotNull { it?.toAnimeSummary() }.orEmpty()
        val orderedData = reorderByIds(ids = resIds, items = data, idSelector = {it.id.value})
        println("Data: ${orderedData}")

    }
}