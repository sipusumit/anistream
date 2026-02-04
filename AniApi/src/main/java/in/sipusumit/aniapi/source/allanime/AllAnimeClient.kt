package `in`.sipusumit.aniapi.source.allanime

import `in`.sipusumit.aniapi.network.HttpClientProvider
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonElement

internal object AllAnimeEndpoints {
    const val BASE = "https://api.allanime.day"
    const val API = "$BASE/api"
    const val REFERER = "https://allmanga.to"
}

internal class AllAnimeClient {

    private val client = HttpClientProvider.client


    suspend fun getHomeScreen(): JsonElement{
        return client.get{
            url(AllAnimeEndpoints.API)
            header(HttpHeaders.Referrer, AllAnimeEndpoints.REFERER)

            url {
//                parameters.append("query", gql)
//                {"type":"anime","size":20,"dateRange":1,"page":1,"allowAdult":false,"allowUnknown":false}
                parameters.append(
                    "variables",
                    """
                    {
                      "type":"anime",
                      "size":10,
                      "dateRange":1,
                      "page":1,
                      "allowAdult":true,
                      "allowUnknown":false
                    }
                    """.trimIndent()
                )
                parameters.append(
                    "extensions",
                    """
                            {
                              "persistedQuery": {
                                "version": 1,
                                "sha256Hash": "1fc9651b0d4c3b9dfd2fa6e1d50b8f4d11ce37f988c23b8ee20f82159f7c1147"
                              }
                            }
                            """.trimIndent()
                )
            }
        }.body()
    }

    suspend fun searchAnime(
        query: String,
        mode: String
    ): JsonElement {

        val gql = """
            query (
              ${'$'}search: SearchInput,
              ${'$'}limit: Int,
              ${'$'}page: Int,
              ${'$'}translationType: VaildTranslationTypeEnumType
            ) {
              shows(
                search: ${'$'}search,
                limit: ${'$'}limit,
                page: ${'$'}page,
                translationType: ${'$'}translationType
              ) {
                edges {
                  _id
                  name
                  englishName
                  nativeName
                  availableEpisodes
                  score
                  status
                  type
                  thumbnail
                }
              }
            }
        """.trimIndent()

        return client.get {
            url(AllAnimeEndpoints.API)
            header(HttpHeaders.Referrer, AllAnimeEndpoints.REFERER)

            url {
                parameters.append("query", gql)
                parameters.append(
                    "variables",
                    """
                    {
                      "search": {
                        "query": "$query",
                        "allowAdult": false,
                        "allowUnknown": false
                      },
                      "limit": 40,
                      "page": 1,
                      "translationType": "$mode"
                    }
                    """.trimIndent()
                )
            }
        }.body()
    }

    suspend fun getAnimeDetails(
        animeId: String
    ): JsonElement {

        val gql = """
            query (${'$'}_id: String!) {
              show(_id: ${'$'}_id) {
                _id
                name
                englishName
                nativeName
                description
                genres
                tags
                status
                type
                score
                season
                episodeDuration
                thumbnail
                banner
                studios
                rating
              }
            }
        """.trimIndent()

//        season {
//            year
//            quarter
//        }

        return client.get {
            url(AllAnimeEndpoints.API)
            header(HttpHeaders.Referrer, AllAnimeEndpoints.REFERER)

            url {
                parameters.append("query", gql)
                parameters.append(
                    "variables",
                    """{ "_id": "$animeId" }"""
                )
            }
        }.body()
    }

    suspend fun getEpisodes(
        animeId: String,
//        mode: String
    ): JsonElement {

        val gql = """
            query (${'$'}showId: String!) {
              show(_id: ${'$'}showId) {
                availableEpisodesDetail
              }
            }
        """.trimIndent()

        return client.get {
            url(AllAnimeEndpoints.API)
            header(HttpHeaders.Referrer, AllAnimeEndpoints.REFERER)

            url {
                parameters.append("query", gql)
                parameters.append(
                    "variables",
                    """{ "showId": "$animeId" }"""
                )
            }
        }.body()
    }

    suspend fun getEpisodeSources(
        animeId: String,
        episode: String,
        mode: String
    ): JsonElement {

        val gql = """
            query (
              ${'$'}showId: String!,
              ${'$'}translationType: VaildTranslationTypeEnumType!,
              ${'$'}episodeString: String!
            ) {
              episode(
                showId: ${'$'}showId,
                translationType: ${'$'}translationType,
                episodeString: ${'$'}episodeString
              ) {
                sourceUrls
              }
            }
        """.trimIndent()

        return client.get {
            url(AllAnimeEndpoints.API)
            header(HttpHeaders.Referrer, AllAnimeEndpoints.REFERER)

            url {
                parameters.append("query", gql)
                parameters.append(
                    "variables",
                    """
                    {
                      "showId": "$animeId",
                      "translationType": "$mode",
                      "episodeString": "$episode"
                    }
                    """.trimIndent()
                )
            }
        }.body()
    }
}
