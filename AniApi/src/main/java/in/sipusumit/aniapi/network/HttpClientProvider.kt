package `in`.sipusumit.aniapi.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * Provides a shared HttpClient instance for all sources.
 *
 * This client is:
 * - Android safe
 * - Desktop JVM safe
 * - Reusable
 * - Properly configured for JSON APIs
 */
object HttpClientProvider {

    @OptIn(ExperimentalSerializationApi::class)
    val client: HttpClient by lazy {
        HttpClient(OkHttp) {

            // ---- JSON ----
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        explicitNulls = false
                    }
                )
            }

            // ---- Timeouts ----
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 30_000
            }

            // ---- Default headers ----
            defaultRequest {
                header(HttpHeaders.UserAgent, UserAgents.DEFAULT)
                header(HttpHeaders.Accept, "application/json")
            }
        }
    }
}
