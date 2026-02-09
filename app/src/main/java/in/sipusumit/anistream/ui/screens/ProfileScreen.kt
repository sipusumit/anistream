package `in`.sipusumit.anistream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import `in`.sipusumit.anistream.ui.theme.Pink500
import `in`.sipusumit.anistream.ui.theme.Purple500
import `in`.sipusumit.anistream.ui.theme.Purple600
import `in`.sipusumit.anistream.ui.theme.Slate800
import `in`.sipusumit.anistream.ui.theme.Slate900
import `in`.sipusumit.anistream.ui.theme.Slate950
import `in`.sipusumit.anistream.ui.theme.TextGrey
import `in`.sipusumit.anistream.ui.theme.TextWhite

// Using the same package-level colors from MainActivity
// If these are not visible, ensure both files are in the same package or move colors to a Theme file.

@Composable
fun ProfileScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        // --- Header ---
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- User Card ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Slate900)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Avatar with Ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Purple600, Pink500)))
                        .padding(3.dp) // Ring width
                        .clip(CircleShape)
                        .background(Slate900) // Gap
                ) {
                    AsyncImage(
                        model = "https://i.pravatar.cc/300?img=12", // Placeholder Avatar
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp) // Gap width
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Otaku Master",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "otaku.master@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Premium Badge
                Surface(
                    color = Purple600.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Purple600.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.WorkspacePremium, null, tint = Purple500, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Premium Member", color = Purple500, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Stats Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                label = "Watched",
                value = "124",
                icon = Icons.Rounded.PlayCircleOutline,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Hours",
                value = "382h",
                icon = Icons.Rounded.Schedule,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Favorites",
                value = "48",
                icon = Icons.Rounded.FavoriteBorder,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Settings / Menu ---
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Slate900)
        ) {
            ProfileMenuItem(icon = Icons.Rounded.Person, title = "Account Settings") {}
            HorizontalDivider(color = Slate800, thickness = 1.dp)
            ProfileMenuItem(icon = Icons.Rounded.Notifications, title = "Notifications") {}
            HorizontalDivider(color = Slate800, thickness = 1.dp)
            ProfileMenuItem(icon = Icons.Rounded.Download, title = "Downloads") {}
            HorizontalDivider(color = Slate800, thickness = 1.dp)
            ProfileMenuItem(icon = Icons.Rounded.Language, title = "App Language") {}
            HorizontalDivider(color = Slate800, thickness = 1.dp)
            ProfileMenuItem(icon = Icons.Rounded.Help, title = "Help & Support") {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Logout Button ---
        Button(
            onClick = { /* Handle Logout */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
        ) {
            Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Log Out", color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(100.dp)) // Bottom padding for nav bar
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = Purple500, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextGrey)
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Slate800),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = TextGrey, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = TextWhite, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Icon(Icons.Rounded.ChevronRight, null, tint = Slate800)
    }
}