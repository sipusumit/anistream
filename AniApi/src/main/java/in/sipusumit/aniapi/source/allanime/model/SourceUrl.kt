package `in`.sipusumit.aniapi.source.allanime.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SourceUrl(
    val sourceUrl: String,
    val priority: Double? = null,
    val sourceName: String? = null,
    val type: String? = null,
    val className: String? = null,
    val streamerId: String? = null
)
