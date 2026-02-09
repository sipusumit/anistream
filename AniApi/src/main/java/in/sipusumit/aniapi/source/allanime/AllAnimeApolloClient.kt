package `in`.sipusumit.aniapi.source.allanime

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import `in`.sipusumit.aniapi.core.LanguageMode
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.source.allanime.graphql.GetAnimeDetailQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.GetAnimeDetailsQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.GetEpisodeSourcesQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.GetEpisodesQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.HomePopularIdsQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.SearchQuery
import `in`.sipusumit.aniapi.source.allanime.graphql.type.Object
import `in`.sipusumit.aniapi.source.allanime.graphql.type.SearchInput
import `in`.sipusumit.aniapi.source.allanime.graphql.type.SortBy
import `in`.sipusumit.aniapi.source.allanime.graphql.type.VaildPopularTypeEnumType
import `in`.sipusumit.aniapi.source.allanime.graphql.type.VaildTranslationTypeEnumType
import `in`.sipusumit.aniapi.util.JsonElementScalarAdapter

class AllAnimeApolloClient {
    private val client = ApolloClient.Builder()
        .serverUrl("https://api.allanime.day/api")
        .addHttpHeader("Referer", "https://allanime.to")
        .addCustomScalarAdapter(
            customScalarType = Object.type,
            customScalarAdapter = JsonElementScalarAdapter
        )
        .build()

    suspend fun getHomeScreen(): GetAnimeDetailsQuery.Data?{
        val resIds = client.query(
            HomePopularIdsQuery(
                type = VaildPopularTypeEnumType.anime,
                size = 10,
                page = 1,
                dateRange = 1,
                allowAdult = true,
                allowUnknown = true
            )
        ).execute().data?.queryPopular?.recommendations?.toIds().orEmpty()

        return client.query(GetAnimeDetailsQuery(resIds)).execute().data
    }

    suspend fun searchAnime(
        query: String,
        mode: String
    ): Pair<List<String>, GetAnimeDetailsQuery.Data?>{
        val resIds = client.query(
            SearchQuery(
                search = Optional.Present(SearchInput(
                    query = Optional.Present(query),
                    sortBy = Optional.Present(SortBy.List)
                )),

                )
        ).execute().data?.shows?.edges?.mapNotNull { it._id }.orEmpty()

        return Pair(resIds, client.query(GetAnimeDetailsQuery(resIds)).execute().data)
    }

    suspend fun getAnimeDetails(
        animeId: String
    ): GetAnimeDetailQuery.Data?{
        return client.query(GetAnimeDetailQuery(
            id = animeId
        )).execute().data
    }

    suspend fun getEpisodes(
        animeId: String
    ): GetEpisodesQuery.Data?{
        return client.query(
            GetEpisodesQuery(animeId)
        ).execute().data
    }

    suspend fun getEpisodeSources(animeId: String, episodeNumber: String, languageMode: LanguageMode): GetEpisodeSourcesQuery.Data? {
        return client.query(GetEpisodeSourcesQuery(animeId, languageMode.toApiEnum(), episodeNumber)).execute().data
    }
}

fun LanguageMode.toApiEnum(): VaildTranslationTypeEnumType{
    return when(this){
        LanguageMode.SUB -> VaildTranslationTypeEnumType.sub
        LanguageMode.DUB -> VaildTranslationTypeEnumType.dub
        LanguageMode.RAW -> VaildTranslationTypeEnumType.raw
    }
}