package `in`.sipusumit.anistream

import android.app.Application
import `in`.sipusumit.aniapi.core.AnimeSource
import `in`.sipusumit.aniapi.source.allanime.AllAnimeSource

class AniStreamApp : Application() {
    lateinit var animeSource: AnimeSource
        private set

    override fun onCreate() {
        super.onCreate()
        animeSource = AllAnimeSource()
    }
}