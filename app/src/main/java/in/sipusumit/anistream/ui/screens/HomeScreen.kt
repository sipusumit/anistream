package `in`.sipusumit.anistream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import `in`.sipusumit.anistream.genres
import `in`.sipusumit.anistream.trBr
import `in`.sipusumit.anistream.trendingAnime
import `in`.sipusumit.anistream.ui.theme.Pink500
import `in`.sipusumit.anistream.ui.theme.Purple500
import `in`.sipusumit.anistream.ui.theme.Purple600
import `in`.sipusumit.anistream.ui.theme.Slate800
import `in`.sipusumit.anistream.ui.theme.Slate900
import `in`.sipusumit.anistream.ui.theme.Slate950
import `in`.sipusumit.anistream.ui.theme.TextGrey
import `in`.sipusumit.anistream.ui.theme.TextWhite
import `in`.sipusumit.anistream.viewmodel.HomeUiState
import `in`.sipusumit.anistream.viewmodel.HomeViewModel

// --- HOME SCREEN ---
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {

    val state by viewModel.state.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > 600

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.trBr(listOf(Purple500, Pink500))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AniStream",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.Cast, contentDescription = "Cast", tint = TextGrey)
                }
            }
        }

        // Hero Section
        item {
            val featured = trendingAnime[0]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLandscape) 500.dp else 450.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { navController.navigate("details/${featured.id}") }
            ) {
                AsyncImage(
                    model = featured.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Slate950)))
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("TRENDING #1", color = TextWhite) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Purple600.copy(alpha = 0.8f))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = featured.title,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Text(" ${featured.rating}", color = TextWhite, fontWeight = FontWeight.Bold)
                        Text(" • ${featured.genre}", color = TextGrey, modifier = Modifier.padding(start = 8.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(
                            onClick = { navController.navigate("details/${featured.id}") },
                            colors = ButtonDefaults.buttonColors(containerColor = TextWhite, contentColor = Slate950),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.Rounded.PlayArrow, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Watch Now", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        FilledIconButton(
                            onClick = {},
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Slate800.copy(alpha = 0.8f))
                        ) {
                            Icon(Icons.Filled.FavoriteBorder, null, tint = TextWhite)
                        }
                    }
                }
            }
        }

        // Genres
        item {
            Text(
                "Top Genres",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(genres) { genre ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Slate900)
                            .border(1.dp, Slate800, RoundedCornerShape(50))
                            .clickable { }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(genre, color = TextWhite, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Recommended Grid
        item {
            Text(
                "Recommended For You",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
            )
        }

        // Note: Nested LazyVerticalGrid inside LazyColumn is tricky.
        // For simplicity in this snippet, we use items with a custom layout or a fixed height grid.
        // Or simply stick to a columnar list for recommendations to scroll naturally.
        when(state){
            HomeUiState.Loading -> item { Text(
                "Loading",
                style = MaterialTheme.typography.titleSmall,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
            ) }

            is HomeUiState.Error -> item { Text(
                "Error",
                style = MaterialTheme.typography.titleSmall,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
            ) }
            is HomeUiState.Success -> items((state as HomeUiState.Success).section.entries.take(10)) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate900.copy(alpha = 0.5f))
                        .clickable { navController.navigate("details/${entry.anime.id.value}") }
                        .padding(12.dp)
                ) {
                    AsyncImage(
                        model = entry.anime.poster?.large,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(90.dp)
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                        Text(
                            entry.anime.title.primary,
//                            anime.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
//                        TODO: Add Description
//                        Text(
//                            entry.anime.
//                            anime.description,
//                            style = MaterialTheme.typography.bodySmall,
//                            color = TextGrey,
//                            maxLines = 2,
//                            overflow = TextOverflow.Ellipsis
//                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Slate800, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("HD", color = TextGrey, style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Filled.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                            Text(" ${entry.anime.score}", color = TextGrey, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

    }
}
