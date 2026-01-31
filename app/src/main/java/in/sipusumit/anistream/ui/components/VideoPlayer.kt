package `in`.sipusumit.anistream.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    streamUrl: String,
    headers: Map<String, String>,
    onPlayerReady: (ExoPlayer) -> Unit
) {
    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {

            val httpFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setDefaultRequestProperties(
                    mapOf(
                        "Referer" to "https://allanime.day/",
                        "User-Agent" to "Mozilla/5.0"
                    )
                )

            val dataSourceFactory =
                DefaultDataSource.Factory(context, httpFactory)

            val mediaSource = if (streamUrl.endsWith(".m3u8")) {
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(streamUrl))
            } else {
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(streamUrl))
            }

            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onPlayerReady(player)
        onDispose { player.release() }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(context).apply {
                this.player = player
                useController = false
                setShutterBackgroundColor(android.graphics.Color.BLACK)
            }
        }
    )
}