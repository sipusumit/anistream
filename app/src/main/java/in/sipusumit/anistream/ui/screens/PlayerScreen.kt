package `in`.sipusumit.anistream.ui.screens

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import `in`.sipusumit.aniapi.model.Episode
import `in`.sipusumit.aniapi.model.EpisodeNumber
import `in`.sipusumit.aniapi.model.Stream
import `in`.sipusumit.anistream.formatTime
import `in`.sipusumit.anistream.ui.components.VideoPlayer
import `in`.sipusumit.anistream.ui.theme.Purple500
import `in`.sipusumit.anistream.ui.theme.Slate800
import `in`.sipusumit.anistream.ui.theme.Slate900
import `in`.sipusumit.anistream.ui.theme.Slate950
import `in`.sipusumit.anistream.ui.theme.TextGrey
import `in`.sipusumit.anistream.ui.theme.TextWhite
import `in`.sipusumit.anistream.viewmodel.PlayerUiState
import `in`.sipusumit.anistream.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToLong
import androidx.core.net.toUri
import `in`.sipusumit.anistream.openIn1DM

private const val DOUBLE_TAP_SEEK_MS = 10_000L

// --- PLAYER SCREEN ---
@Composable
fun PlayerScreen(navController: NavController, viewModel: PlayerViewModel) {
    val context = LocalContext.current
    val view = LocalView.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    // Fullscreen State
    var isFullscreen by rememberSaveable{ mutableStateOf(false) }
    DisposableEffect(isFullscreen) {
        val window = (context as? Activity)?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, view)
            if (isFullscreen) {
                // Hide system bars
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                // Force landscape
                context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                // Show system bars
                insetsController.show(WindowInsetsCompat.Type.systemBars())
                // Reset orientation
                context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }

        onDispose {
            val window = (context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
                context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.downloadUrl.collect { url ->
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = url.toUri()
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            }
//            context.startActivity(intent)
            openIn1DM(
                context = context,
                url = url,
//                fileName = "One_Piece_Ep_${episode.number}.mp4",
                headers = mapOf(
                    "Referer" to "https://allanime.day/",
                    "User-Agent" to "Mozilla/5.0"
                )
            )
        }
    }

    // Search State
    var searchQuery by remember { mutableStateOf("") }
//    val allEpisodes = remember { (1..24).map { it } } // Mock IDs for 24 episodes


//    val query by viewModel.query.collectAsStateWithLifecycle()
    val episodes by viewModel.episodes.collectAsStateWithLifecycle()

    // Filtering logic
    val filteredEpisodes = remember(searchQuery, episodes) {
        if (searchQuery.isBlank()) episodes
        else episodes.filter {
            "Episode ${it.number.value}".contains(searchQuery, ignoreCase = true) || "${it.title}".contains(searchQuery)
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val playerContent = remember {
        movableContentOf<Stream, EpisodeNumber, Boolean> { stream, current, fullScreenMode ->
            VideoPlayerContent(
                stream = stream,
                onBack = {navController.popBackStack()},
                isFullscreen = fullScreenMode,
                onToggleFullscreen = { isFullscreen = !isFullscreen },
                viewModel = viewModel,
                current = current
            )
        }
    }

    if(isFullscreen){
        // FULLSCREEN MODE: Just the player
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)) {
            when(state){ // TODO: fix this multiple when's
                is PlayerUiState.Ready -> {
                    val res = (state as PlayerUiState.Ready)
                    val streams = res.streams
                    playerContent(streams.first(), res.current.episode,false)
//                    VideoPlayerContent(
//                        stream = streams.first(),
//                        onBack = {navController.popBackStack()},
//                        isFullscreen = true,
//                        onToggleFullscreen = { isFullscreen = false }
//                    )
                }

                PlayerUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("Loading…", color = TextWhite)
                    }
                }

                is PlayerUiState.Error -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            (state as PlayerUiState.Error).message,
                            color = TextWhite
                        )
                    }
                }
            }
        }
    } else if (isLandscape) {
        Row(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)) {
            // Video Player Area (Takes remaining width)
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()) {
                when(state){
                    is PlayerUiState.Ready -> {
                        val res = (state as PlayerUiState.Ready)
                        val streams = res.streams
                        playerContent(streams.first(), res.current.episode, true)
                    }

                    PlayerUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("Loading…", color = TextWhite)
                        }
                    }

                    is PlayerUiState.Error -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text(
                                (state as PlayerUiState.Error).message,
                                color = TextWhite
                            )
                        }
                    }
                }
            }

            // Side Panel for Episode List (Fixed width)
            Column(
                modifier = Modifier
                    .width(350.dp)
                    .fillMaxHeight()
                    .background(Slate900)
                    .border(BorderStroke(1.dp, Slate800))
            ) {
                EpisodeListPanel(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    episodes = filteredEpisodes,
                    onClick = { viewModel.playEpisode(it) },
                    viewModel
                )
            }
        }
    } else {
        // Portrait Layout
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Slate950)) {
            // Video Player Area (Fixed aspect ratio 16:9)
            Box(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)) {
                when(state){
                    is PlayerUiState.Ready -> {
                        val res = (state as PlayerUiState.Ready)
                        val streams = res.streams
                        playerContent(streams.first(), res.current.episode, true)
                    }

                    PlayerUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("Loading…", color = TextWhite)
                        }
                    }

                    is PlayerUiState.Error -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text(
                                (state as PlayerUiState.Error).message,
                                color = TextWhite
                            )
                        }
                    }
                }
            }

            // Episode List Below
            EpisodeListPanel(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                episodes = filteredEpisodes,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.playEpisode(it) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun rememberControlsVisibility(
    autoHideMillis: Long = 3000L
): Pair<Boolean, () -> Unit> {

    var visible by remember { mutableStateOf(true) }
    var interactionTick by remember { mutableLongStateOf(0L) }

    // Auto-hide timer
    LaunchedEffect(visible, interactionTick) {
        if (visible) {
            delay(autoHideMillis)
            visible = false
        }
    }

    // Call this on ANY interaction
    fun onUserInteraction() {
//        interactionTick = System.currentTimeMillis()
        if (!visible) visible = true
    }

    return visible to ::onUserInteraction
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerContent(stream: Stream, current: EpisodeNumber, isFullscreen: Boolean, onBack: () -> Unit, onToggleFullscreen: () -> Unit, viewModel: PlayerViewModel) {
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()){
        VideoPlayer(stream.url, emptyMap()) { exoPlayer ->
            player = exoPlayer
        }
        // listen to playback changes
        DisposableEffect(player) {
            val exoPlayer = player ?: return@DisposableEffect onDispose {}

            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }

                @Deprecated("Deprecated in Java")
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if(playbackState == Player.STATE_ENDED){
                        viewModel.onEpisodeEnded()
                    }
                }

//                override
            }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
            }
        }

        // 🎛 CONTROLLER STATE
        val (controlsVisible, onUserInteraction) =
            rememberControlsVisibility()

        // 👆 TAP ANYWHERE TO TOGGLE CONTROLS
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .clickable(
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                ) {
//                    onUserInteraction()
//                }
                .pointerInput(player){
                    detectTapGestures(
                        onTap = {
                            onUserInteraction()
                        },
                        onDoubleTap = { offset ->
                            val exoPlayer = player ?: return@detectTapGestures
                            val width = size.width
                            val current = exoPlayer.currentPosition
                            val duration = exoPlayer.duration

                            if (offset.x < width / 2f) {
                                // ⏪ LEFT → rewind
                                val newPos = (current - DOUBLE_TAP_SEEK_MS).coerceAtLeast(0)
                                exoPlayer.seekTo(newPos)
                            } else {
                                // ⏩ RIGHT → forward
                                val newPos = (current + DOUBLE_TAP_SEEK_MS)
                                    .coerceAtMost(duration)
                                exoPlayer.seekTo(newPos)
                            }

                            onUserInteraction()
                        }
                    )
                }
        )

        var isBuffering by remember { mutableStateOf(false) }
        var currentPosition by remember { mutableLongStateOf(0L) }
        var duration by remember { mutableLongStateOf(0L) }
        var bufferedPosition by remember { mutableLongStateOf(0L) }
        val progress =
            if (duration > 0) currentPosition.toFloat() / duration else 0f
        val bufferedFraction =
            if (duration > 0) bufferedPosition.toFloat() / duration else 0f

        LaunchedEffect(player) {
            while (true) {
                player?.let {
                    currentPosition = it.currentPosition
                    bufferedPosition = it.bufferedPosition
                    duration = it.duration.coerceAtLeast(0L)
                    isBuffering = it.isLoading
                }
                delay(500) // smooth but battery-friendly
            }
        }

        // Controls Overlay
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut()
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Bar
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextWhite)
                    }
                    Icon(Icons.Rounded.Cast, null, tint = TextWhite)
                }


                // Center Play Button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
//                            .background(Purple600.copy(alpha = 0.8f))
                            .clickable {
                                onUserInteraction()
                                player?.let {
                                    if (it.isPlaying) it.pause() else it.play()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if(isBuffering && !isPlaying){
                            CircularProgressIndicator(color = Purple500)
                        }else {
                            Icon(
                                imageVector = if (isPlaying)
                                    Icons.Rounded.Pause
                                else
                                    Icons.Rounded.PlayArrow,
                                null,
                                tint = TextWhite,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Bottom Controls
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            formatTime(currentPosition),
                            color = TextWhite,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            formatTime(duration),
                            color = TextGrey,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(TextGrey.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                            .pointerInput(duration) {
                                detectTapGestures { offset ->
                                    val percent = offset.x / size.width
                                    val seekTo = (duration * percent).roundToLong()
                                    player?.seekTo(seekTo)
                                }
                            }
                    ) {
                        // 🟡 Buffered progress
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(bufferedFraction.coerceIn(0f, 1f))
                                .height(6.dp)
                                .background(TextGrey.copy(alpha = 0.5f))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .height(6.dp)
                                .background(Purple500, RoundedCornerShape(2.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "EP: ${current.value}",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {

                            // Skip Intro
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(
                                        alpha = 0.2f
                                    )
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Skip Intro", fontSize = 12.sp)
                            }
//                        FullScreen Button
                            IconButton(onClick = onToggleFullscreen) {
                                Icon(
                                    imageVector = if (isFullscreen)
                                        Icons.Rounded.FullscreenExit
                                    else
                                        Icons.Rounded.Fullscreen,
                                    contentDescription = "Fullscreen",
                                    tint = TextWhite
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EpisodeListPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    episodes: List<Episode>,
    onClick: (EpisodeNumber) -> Unit,
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Up Next", style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search episode...", color = TextGrey, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = TextGrey) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Slate800),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Slate800,
                unfocusedContainerColor = Slate800,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                cursorColor = Purple500
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(episodes) { episode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .clickable { } // Handle episode click
                        .padding(8.dp)
                        .clickable { onClick(episode.number) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Thumbnail Placeholder
//                    Box(
//                        modifier = Modifier
//                            .width(100.dp)
//                            .height(60.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                    ) {
//                        AsyncImage(
//                            model = "https://images.unsplash.com/photo-1542206395-9feb3edaa68d?w=400&q=80",
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .alpha(0.7f)
//                        )
//                        Icon(Icons.Rounded.PlayArrow, null, tint = TextWhite, modifier = Modifier.align(Alignment.Center))
//                    }
//
//                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Episode ${episode.number.value}", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
//                        Text("The journey continues...", color = TextGrey, fontSize = 12.sp, maxLines = 1)
                    }
                    IconButton(onClick ={viewModel.downloadEpisode(episode.number)}) {
                        Icon(Icons.Rounded.Download, null,  tint = TextWhite)
                    }
                }
            }
        }
    }
}