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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NorthWest
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import `in`.sipusumit.anistream.genres
import `in`.sipusumit.anistream.ui.theme.Purple500
import `in`.sipusumit.anistream.ui.theme.Slate800
import `in`.sipusumit.anistream.ui.theme.Slate900
import `in`.sipusumit.anistream.ui.theme.Slate950
import `in`.sipusumit.anistream.ui.theme.TextGrey
import `in`.sipusumit.anistream.ui.theme.TextWhite
import `in`.sipusumit.anistream.viewmodel.SearchUiState
import `in`.sipusumit.anistream.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

// --- SEARCH SCREEN ---
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val recentSearches = listOf<String>()

    LaunchedEffect(query) {
        if (query.isBlank()) {
            viewModel.clear()
            return@LaunchedEffect
        }

        delay(400)
        viewModel.search(query)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(top = 24.dp)
    ) {
        // Search Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
                placeholder = { Text("Search anime...", color = TextGrey) },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = null,
                        tint = TextGrey
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" ; viewModel.clear()}) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = TextGrey)
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Purple500,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
//                        viewModel.search(query)
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content
        when(state){
            SearchUiState.Idle -> {
                if(query.isBlank()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    )
                    {
                        // Recent Searches
                        item {
                            Text(
                                "Recent Searches",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }

                        items(recentSearches) { search ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { query = search }
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.History,
                                    contentDescription = null,
                                    tint = TextGrey,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(search, color = TextGrey, modifier = Modifier.weight(1f))
                                Icon(
                                    Icons.Rounded.NorthWest,
                                    contentDescription = null,
                                    tint = Slate800,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }

                        // Popular Tags
                        item {
                            Text(
                                "Popular Tags",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(genres) { genre ->
                                    SuggestionChip(
                                        onClick = { query = genre },
                                        label = { Text(genre, color = TextWhite) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = Slate900
                                        ),
//                                border = SuggestionChipDefaults.suggestionChipBorder(borderColor = Slate800)
                                        border = BorderStroke(1.dp, Slate800)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SearchUiState.Loading ->{
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Icon(Icons.Rounded.SearchOff, contentDescription = null, tint = Slate800, modifier = Modifier.size(64.dp))
                        CircularProgressIndicator(color = Purple500)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Searching...", color = TextGrey)
                    }
                }
            }

            is SearchUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.SearchOff, contentDescription = null, tint = Slate800, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Error: ${(state as SearchUiState.Error).message}", color = TextGrey)
                    }
                }
            }

            // Search Results
            is SearchUiState.Success -> {
                val success = state as SearchUiState.Success

                Text(
                    "Results for \"$query\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                if (success.result.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.SearchOff, contentDescription = null, tint = Slate800, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No results found", color = TextGrey)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
//                          contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp, bottom = 100.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(success.result) { anime ->
                            Column(
                                modifier = Modifier
                                    .clickable { navController.navigate("details/${anime.id}") }
                            ) {
                                AsyncImage(
                                    model = anime.poster?.large,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.7f)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    anime.title.primary,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
//                                        anime.genre.split("•")[0],
                                    anime.type.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrey
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}