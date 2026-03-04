package `in`.sipusumit.anistream

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.source.allanime.AllAnimeSource
import `in`.sipusumit.anistream.data.local.AppDatabase

class AniStreamApp : Application() {
    lateinit var animeSource: AnimeSource
        private set

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        animeSource = AllAnimeSource()
        db = AppDatabase.getDatabase(this)
    }
}