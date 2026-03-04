package `in`.sipusumit.anistream.data.local

import androidx.room.Entity

@Entity(tableName = "watch_history", primaryKeys = ["animeId", "episodeNumber"])
data class WatchHistory(
    val animeId: String,
    val episodeNumber: String, // Value of EpisodeNumber
    val position: Long,
    val duration: Long,
    val timestamp: Long = System.currentTimeMillis()
)