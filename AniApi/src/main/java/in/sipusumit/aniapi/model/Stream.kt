package `in`.sipusumit.aniapi.model

/**
 * Represents a playable video stream.
 *
 * This is intentionally minimal and player-friendly.
 */
data class Stream(

    /** Direct playable URL (m3u8 / mp4) */
    val url: String,

    /** Quality information (label + resolution) */
    val quality: StreamQuality = StreamQuality.AUTO,

    /** Stream format */
    val format: StreamFormat,

    /**
     * Optional headers required to play the stream
     * (e.g. Referer, User-Agent)
     */
    val headers: Map<String, String> = emptyMap()
)

/**
 * Represents stream quality.
 */
data class StreamQuality(
    val label: String,
    val height: Int?
) {
    companion object {
        val AUTO = StreamQuality("Auto", null)
    }
}

/**
 * Stream container / protocol type.
 */
enum class StreamFormat {
    HLS,   // .m3u8
    MP4
}