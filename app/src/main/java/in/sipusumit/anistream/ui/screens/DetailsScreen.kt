package `in`.sipusumit.anistream.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import `in`.sipusumit.aniapi.model.AnimeDetails
import `in`.sipusumit.aniapi.model.Episode
import `in`.sipusumit.anistream.ui.theme.Purple600
import `in`.sipusumit.anistream.ui.theme.Slate800
import `in`.sipusumit.anistream.ui.theme.Slate900
import `in`.sipusumit.anistream.ui.theme.Slate950
import `in`.sipusumit.anistream.ui.theme.TextGrey
import `in`.sipusumit.anistream.ui.theme.TextWhite
import `in`.sipusumit.anistream.viewmodel.DetailsUiState
import `in`.sipusumit.anistream.viewmodel.DetailsViewModel

// --- DETAILS SCREEN ---
@Composable
fun DetailsScreen(navController: NavController, viewModel: DetailsViewModel) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsStateWithLifecycle()
    when(state){
        DetailsUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...", color = TextGrey)
            }
        }

        is DetailsUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    (state as DetailsUiState.Error).message,
                    color = TextGrey
                )
            }
        }

        is DetailsUiState.Success -> {
            val result = (state as DetailsUiState.Success).result
            val anime = result.anime
            DetailsUi(navController, anime, result.epList)
//            Box(modifier = Modifier
//                .fillMaxSize()
//                .background(Slate950)
//                .padding(10.dp))
//            {
////                Column(modifier = Modifier.verticalScroll(scrollState)) {
//                LazyColumn(){
//                    // Header Image
//                    item{
//                        Box(
//                            modifier = Modifier
//                                .height(400.dp)
//                                .fillMaxWidth()
//                        ) {
//                            AsyncImage(
//                                model = anime.posters?.large,
//                                contentDescription = null,
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier.fillMaxSize()
//                            )
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(
//                                        Brush.verticalGradient(
//                                            listOf(
//                                                Color.Transparent,
//                                                Slate950
//                                            )
//                                        )
//                                    )
//                            )
//
//                            Column(
//                                modifier = Modifier
//                                    .align(Alignment.BottomStart)
//                                    .padding(24.dp)
//                            ) {
//                                Text(
//                                    anime.title.primary,
//                                    style = MaterialTheme.typography.displaySmall,
//                                    fontWeight = FontWeight.Black,
//                                    color = TextWhite
//                                )
//                                Row(
//                                    modifier = Modifier.padding(top = 8.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(
//                                        "98% Match",
//                                        color = Color(0xFF4ADE80),
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                    Spacer(modifier = Modifier.width(12.dp))
//                                    Text("2024", color = TextGrey)
//                                    Spacer(modifier = Modifier.width(12.dp))
//                                    Box(
//                                        modifier = Modifier
//                                            .border(1.dp, TextGrey, RoundedCornerShape(4.dp))
//                                            .padding(horizontal = 4.dp)
//                                    ) {
//                                        Text("16+", color = TextGrey, fontSize = 12.sp)
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    // Content
////                    Column(modifier = Modifier.padding(24.dp)) {
//                        // Action Buttons
//                    item {
//                        Row(modifier = Modifier.fillMaxWidth()) {
//                            Button(
//                                onClick = { navController.navigate("player") },
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .height(50.dp),
//                                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
//                                shape = RoundedCornerShape(12.dp)
//                            ) {
//                                Icon(Icons.Rounded.PlayArrow, null)
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Text("Play S1 E1")
//                            }
//                            Spacer(modifier = Modifier.width(16.dp))
//                            OutlinedButton(
//                                onClick = { },
//                                modifier = Modifier
//                                    .width(60.dp)
//                                    .height(50.dp),
//                                shape = RoundedCornerShape(12.dp),
//                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
//                                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
//                            ) {
//                                Icon(Icons.Filled.FavoriteBorder, null)
//                            }
//                        }
//                    }
//
//                        // Description
//                    item {
//                        Text(
//                            anime.description,
//                            color = TextGrey,
//                            modifier = Modifier.padding(vertical = 24.dp),
//                            lineHeight = 24.sp
//                        )
//                    }
//
//                        // Episodes
//                    item {
//                        Text(
//                            "Episodes",
//                            style = MaterialTheme.typography.titleLarge,
//                            fontWeight = FontWeight.Bold,
//                            color = TextWhite
//                        )
//                    }
//                    item {
//                        LazyVerticalGrid(
//                            columns = GridCells.Adaptive(minSize = 56.dp),
//                            horizontalArrangement = Arrangement.spacedBy(12.dp),
//                            verticalArrangement = Arrangement.spacedBy(12.dp),
//                            contentPadding = PaddingValues(24.dp),
//                            modifier = Modifier.heightIn(max = 600.dp) // important
//                        ) {
//                            items(result.epList) { ep ->
//                                EpisodeCell(ep) {
//                                    navController.navigate("player")
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // Back Button
//                IconButton(
//                    onClick = { navController.popBackStack() },
//                    modifier = Modifier
//                        .padding(top = 48.dp, start = 16.dp)
//                        .background(Color.Black.copy(0.3f), CircleShape)
//                ) {
//                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextWhite)
//                }
//            }
        }
    }
}

@Composable
private fun EpisodeCell(
    ep: Episode,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Slate900)
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            ep.number.toString(),
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun DetailsUi(navController: NavController, anime: AnimeDetails, epList: List<Episode>){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = 24.dp

    Box(modifier = Modifier.fillMaxSize().background(Slate950)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
//            contentPadding = PaddingValues(horizontal = horizontalPadding, bottom = 24.dp),
            contentPadding = PaddingValues(start = horizontalPadding, end = horizontalPadding, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Image (Spans full width)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        // Break out of parent grid padding to be full-bleed
                        .requiredWidth(screenWidth)
                        .offset(x = -horizontalPadding)
                ) {
                    AsyncImage(
                        model = anime.posters?.large,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Slate950))))

                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                    ) {
                        Text(
                            anime.title.primary,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = TextWhite,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                        Row(
                            modifier = Modifier.padding(top = 8.dp, start = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("98% Match", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("2024", color = TextGrey)
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(modifier = Modifier.border(1.dp, TextGrey, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp)) {
                                Text("16+", color = TextGrey, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Content (Description + Buttons) - Spans full width
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)) {
                    // Action Buttons
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { navController.navigate("player/${anime.id}/${epList.first().number}") },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.PlayArrow, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Play S1 E1")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.width(60.dp).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = BorderStroke(1.dp, Slate800)
                        ) {
                            Icon(Icons.Filled.FavoriteBorder, null)
                        }
                    }

                    // Description
                    Text(
                        anime.description,
                        color = TextGrey,
                        modifier = Modifier.padding(vertical = 24.dp),
                        lineHeight = 24.sp
                    )

                    // Episodes Title
                    Text("Episodes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }

            // Episodes Items (Native Grid Items)
//            items(12) { i ->
//                val epNum = i + 1
            items(epList){ ep ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                        .clickable { navController.navigate("player/${anime.id}/${ep.number.value}") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${ep.number}",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 48.dp, start = 16.dp).background(Color.Black.copy(0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextWhite)
        }
    }
}