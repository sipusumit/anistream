package `in`.sipusumit.anistream

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import `in`.sipusumit.aniapi.model.AnimeId
import `in`.sipusumit.aniapi.model.EpisodeNumber
import `in`.sipusumit.anistream.ui.theme.Pink500
import `in`.sipusumit.anistream.ui.theme.Purple500
import `in`.sipusumit.anistream.ui.theme.Slate800
import `in`.sipusumit.anistream.ui.theme.Slate950
import `in`.sipusumit.anistream.ui.theme.TextGrey
import `in`.sipusumit.anistream.ui.theme.TextWhite
import `in`.sipusumit.anistream.ui.components.NavRailItem
import `in`.sipusumit.anistream.ui.screens.DetailsScreen
import `in`.sipusumit.anistream.ui.screens.HomeScreen
import `in`.sipusumit.anistream.ui.screens.ListScreen
import `in`.sipusumit.anistream.ui.screens.PlayerScreen
import `in`.sipusumit.anistream.ui.screens.ProfileScreen
import `in`.sipusumit.anistream.ui.screens.SearchScreen
import `in`.sipusumit.anistream.viewmodel.DetailsViewModel
import `in`.sipusumit.anistream.viewmodel.DetailsViewModelFactory
import `in`.sipusumit.anistream.viewmodel.HomeViewModel
import `in`.sipusumit.anistream.viewmodel.HomeViewModelFactory
import `in`.sipusumit.anistream.viewmodel.PlayerViewModel
import `in`.sipusumit.anistream.viewmodel.PlayerViewModelFactory
import `in`.sipusumit.anistream.viewmodel.SearchViewModel
import `in`.sipusumit.anistream.viewmodel.SearchViewModelFactory

// --- ROOT COMPOSABLE ---
@Composable
fun AnimeApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Responsive Logic
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > 600

    val context = LocalContext.current
    val app = context.applicationContext as AniStreamApp
    val homeScreenViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(app.animeSource))
    Row(modifier = Modifier.fillMaxSize().background(Slate950)) {

        // Desktop/Tablet Sidebar (Visible only on wide screens and NOT in player)
//        if (isLandscape && currentRoute != "player") {
        if (isLandscape && !(currentRoute != null && currentRoute.contains("player"))) {
            NavigationRail(
                containerColor = Slate950.copy(alpha = 0.95f),
                contentColor = TextGrey,
                modifier = Modifier.width(80.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                // Logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.trBr(listOf(Purple500, Pink500))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(48.dp))

                NavRailItem(Icons.Filled.Home, "Home", true) { navController.navigate("home") }
                NavRailItem(
                    Icons.Rounded.Search,
                    "Search",
                    false
                ) { navController.navigate("search") }
                NavRailItem(Icons.Filled.FavoriteBorder, "List", false) {}
                NavRailItem(Icons.Rounded.Settings, "Settings", false) {}
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                // Mobile Bottom Bar (Visible only on narrow screens and NOT in player)
//                if (!isLandscape && currentRoute != "player") {
                if (!isLandscape && !(currentRoute != null && currentRoute.contains("player"))) {
                    NavigationBar(
                        containerColor = Slate950.copy(alpha = 0.9f),
                        contentColor = TextWhite,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            selected = currentRoute == "home",
                            onClick = { navController.navigate("home") },
                            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Purple500,
                                selectedTextColor = Purple500,
                                indicatorColor = Slate800
                            )
                        )
                        NavigationBarItem(
                            selected = currentRoute?.contains("search") ?: false,
                            onClick = { navController.navigate("search") },
                            icon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                            label = { Text("Search") },
                            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                        )
                        // Floating Action Button Style in Middle
//                        Box(modifier = Modifier.offset(y = (-10).dp)) {
//                            Box(modifier = Modifier
//                                .size(50.dp)
//                                .clip(CircleShape)
//                                .background(Brush.linearGradient(listOf(Purple600, Pink500)))
//                                .clickable { },
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Icon(Icons.Rounded.PlayArrow, "Play", tint = Color.White)
//                            }
//                        }
                        NavigationBarItem(
                            selected = currentRoute == "myList",
                            onClick = { navController.navigate("myList")},
                            icon = { Icon(Icons.Filled.FavoriteBorder, contentDescription = "List") },
                            label = { Text("My List") },
                            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                        )
                        NavigationBarItem(
                            selected = currentRoute == "profile",
                            onClick = { navController.navigate("profile")},
                            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home", // TODO: home
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen(navController, homeScreenViewModel) }
                composable("search?query={query}") { backStackEntry ->
                    val query = backStackEntry.arguments?.getString("query")
                    val searchScreenViewModel: SearchViewModel = viewModel(
                        backStackEntry,
                        factory = SearchViewModelFactory(app.animeSource, query)
                    )
                    SearchScreen(navController, searchScreenViewModel)
                }
                composable("details/{animeId}") { backStackEntry ->
                    val animeId = backStackEntry.arguments?.getString("animeId") ?: error("Should not happen")
//                    val anime = trendingAnime.find { it.id == animeId } ?: trendingAnime[0]
                    Log.d("ANI", animeId)
                    val detailsViewModel: DetailsViewModel = viewModel(
                        factory = DetailsViewModelFactory(
                            source = app.animeSource,
                            animeId = AnimeId(animeId)
                        )
                    )
                    DetailsScreen(navController, detailsViewModel)
                }
                composable(
                    route = "player/{animeId}/{episode}",
                    arguments = listOf(
                        navArgument("animeId"){type = NavType.StringType},
                        navArgument("episode"){type = NavType.StringType}
                    )
                ) { backStackEntry ->
                    val animeId = backStackEntry.arguments!!.getString("animeId")!!
                    val episode = backStackEntry.arguments!!.getString("episode")!!

                    val viewModel: PlayerViewModel = viewModel(
                        factory = PlayerViewModelFactory(
                            source = app.animeSource,
                            animeId = AnimeId(animeId),
                            episode = EpisodeNumber(episode)
                        )
                    )

                    PlayerScreen(navController, viewModel)
                }
                composable("myList") {
                    ListScreen(navController)
                }
                composable("profile") {
                    ProfileScreen(navController)
                }
            }
        }
    }
}