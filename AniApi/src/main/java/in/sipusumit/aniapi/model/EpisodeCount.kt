package `in`.sipusumit.aniapi.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class EpisodeCount(
    val sub: Int? = null,
    val dub: Int? = null,
    val raw: Int? = null
)