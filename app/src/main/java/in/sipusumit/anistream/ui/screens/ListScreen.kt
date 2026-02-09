package `in`.sipusumit.anistream.ui.screens

import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ViewList
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import `in`.sipusumit.anistream.ui.theme.Purple500
import `in`.sipusumit.anistream.ui.theme.Slate800
import `in`.sipusumit.anistream.ui.theme.Slate900
import `in`.sipusumit.anistream.ui.theme.Slate950
import `in`.sipusumit.anistream.ui.theme.TextGrey
import `in`.sipusumit.anistream.ui.theme.TextWhite
import androidx.core.content.edit

// TODO: Use Real Data

// --- MOCK DATA FOR LIST SCREEN ---
// Extending the concept of Anime with user-specific data
data class ListEntry(
    val id: String,
    val title: String,
    val image: String,
    val totalEpisodes: Int,
    val progress: Int,
    val status: String, // Watching, Completed, etc.
    val score: Double
)

val mockUserList = listOf(
    ListEntry("1", "Neon Genesis: Rebirth", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&q=80", 24, 12, "Watching", 9.8),
    ListEntry("2", "Blade of the Phantom", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&q=80", 12, 12, "Completed", 9.5),
    ListEntry("3", "Cyber City 3000", "https://images.unsplash.com/photo-1535905557558-afc4877a26fc?w=800&q=80", 24, 5, "Paused", 9.2),
    ListEntry("4", "Titan's Fall", "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?w=400&q=80", 75, 70, "Watching", 8.9),
    ListEntry("5", "Jujutsu Kaisen", "https://images.unsplash.com/photo-1607604276583-eef5f0b7e990?w=400&q=80", 24, 0, "Plan to Watch", 9.1),
    ListEntry("6", "Cyberpunk: Edgerunners", "https://images.unsplash.com/photo-1620553750247-817de25c4235?w=800&q=80", 10, 10, "Completed", 9.6)
)

@Composable
fun ListScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }

    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("All") }

    var isGridView by remember { mutableStateOf(prefs.getBoolean("is_grid_view", true)) }

    // Update User Preference
    LaunchedEffect(isGridView) {
        prefs.edit { putBoolean("is_grid_view", isGridView) }
    }

    val filters = listOf("All", "Watching", "Completed", "Paused", "Plan to Watch", "Dropped")

    // Filter Logic
    val filteredList = remember(searchQuery, activeFilter) {
        mockUserList.filter { anime ->
            val matchesSearch = anime.title.contains(searchQuery, ignoreCase = true)
            val matchesFilter = activeFilter == "All" || anime.status == activeFilter
            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        containerColor = Slate950,
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = { /* TODO: Add new anime to list */ },
                containerColor = Purple500,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Header Section ---
            Column(
                modifier = Modifier
                    .background(Slate950)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My List",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    // View Toggle
                    Row(
                        modifier = Modifier
                            .background(Slate900, RoundedCornerShape(12.dp))
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { isGridView = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Rounded.GridView,
                                contentDescription = "Grid",
                                tint = if (isGridView) Purple500 else TextGrey,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = { isGridView = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Rounded.ViewList,
                                contentDescription = "List",
                                tint = if (!isGridView) Purple500 else TextGrey,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
                    placeholder = { Text("Search your list...", color = TextGrey) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = TextGrey) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Slate900,
                        unfocusedContainerColor = Slate900,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Purple500,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = activeFilter == filter,
                            onClick = { activeFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple500,
                                selectedLabelColor = Color.White,
                                containerColor = Slate900,
                                labelColor = TextGrey
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = Slate800,
                                selectedBorderColor = Purple500,
                                borderWidth = 1.dp,
                                enabled = true,
                                selected = false
                            ),
                            shape = CircleShape
                        )
                    }
                }
            }

            // --- Content List ---
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = null,
                            tint = Slate800,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No anime found", color = TextGrey)
                    }
                }
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
//                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp, bottom = 100.dp), // Extra bottom padding for nav bar
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top= 8.dp, bottom = 100.dp), // Extra bottom padding for nav bar
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredList) { anime ->
                            GridListCard(anime, navController)
                        }
                    }
                } else {
                    LazyColumn(
//                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp, bottom = 100.dp),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top= 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredList) { anime ->
                            RowListCard(anime, navController)
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun GridListCard(anime: ListEntry, navController: NavController) {
    Column(
        modifier = Modifier
            .clickable { navController.navigate("details/${anime.id}") }
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(16.dp))
                .background(Slate900)
        ) {
            AsyncImage(
                model = anime.image,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 200f
                        )
                    )
            )

            // Episode Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${anime.progress}/${anime.totalEpisodes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            // Play Icon Overlay (Optional)
            if (anime.status == "Watching") {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Purple500.copy(alpha = 0.8f), CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = "Resume",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = anime.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Progress Bar
        val progress = if (anime.totalEpisodes > 0) anime.progress.toFloat() / anime.totalEpisodes else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = getStatusColor(anime.status),
            trackColor = Slate800,
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(anime.status, style = MaterialTheme.typography.labelSmall, color = TextGrey, fontSize = 10.sp)
            if(anime.status == "Watching") {
                Text(
                    "Ep ${anime.progress + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Purple500,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RowListCard(anime: ListEntry, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900)
            .clickable { navController.navigate("details/${anime.id}") }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = anime.image,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = anime.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = anime.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = getStatusColor(anime.status)
                )
                Text(" • ", color = TextGrey)
                Text(
                    text = "${anime.progress} / ${anime.totalEpisodes} EP",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            val progress = if (anime.totalEpisodes > 0) anime.progress.toFloat() / anime.totalEpisodes else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = getStatusColor(anime.status),
                trackColor = Slate800,
            )
        }

        // Actions
        IconButton(onClick = { /* Open Menu */ }) {
            Icon(Icons.Rounded.MoreVert, contentDescription = "More", tint = TextGrey)
        }
    }
}

fun getStatusColor(status: String): Color {
    return when (status) {
        "Watching" -> Purple500
        "Completed" -> Color(0xFF4ADE80) // Green
        "Plan to Watch" -> TextGrey
        "Dropped" -> Color(0xFFEF4444) // Red
        "Paused" -> Color(0xFFF59E0B) // Amber
        else -> TextGrey
    }
}