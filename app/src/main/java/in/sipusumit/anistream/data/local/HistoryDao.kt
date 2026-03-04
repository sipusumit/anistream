package `in`.sipusumit.anistream.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @JvmSuppressWildcards
    @Query("SELECT * FROM watch_history WHERE animeId = :animeId AND episodeNumber = :episodeVal")
    suspend fun getHistory(animeId: String, episodeVal: String): WatchHistory?

    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHistory(history: WatchHistory): Long

    @JvmSuppressWildcards
    @Query("SELECT * FROM watch_history ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWatched(): Flow<WatchHistory?>
}