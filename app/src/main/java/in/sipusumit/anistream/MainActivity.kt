package `in`.sipusumit.anistream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import `in`.sipusumit.anistream.ui.theme.Purple600
import `in`.sipusumit.anistream.ui.theme.Slate900
import `in`.sipusumit.anistream.ui.theme.Slate950
import `in`.sipusumit.anistream.ui.theme.TextWhite


// --- MOCK DATA ---
data class Anime(
    val id: Int,
    val title: String,
    val image: String,
    val rating: Double,
    val genre: String,
    val description: String
)

val trendingAnime = listOf(
    Anime(1, "Neon Genesis: Rebirth", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&q=80", 9.8, "Mecha • Psychological", "In a post-apocalyptic future, humanity's last hope lies in the hands of pilots who sync with bio-machines."),
    Anime(2, "Blade of the Phantom", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&q=80", 9.5, "Action • Supernatural", "A young swordsman must navigate a world of spirits to reclaim his family's honor."),
    Anime(3, "Cyber City 3000", "https://images.unsplash.com/photo-1535905557558-afc4877a26fc?w=800&q=80", 9.2, "Sci-Fi • Cyberpunk", "In a city ruled by AI, one hacker discovers the truth behind the code."),
    Anime(4, "Titan's Fall", "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?w=400&q=80", 8.9, "Action • Drama", "Huge walls protect the last humans from giants."),
    Anime(5, "Jujutsu Kaisen", "https://images.unsplash.com/photo-1607604276583-eef5f0b7e990?w=400&q=80", 9.1, "Supernatural", "Curses run rampant in modern Tokyo.")
)

val genres = listOf("Action", "Romance", "Isekai", "Sci-Fi", "Horror", "Slice of Life")


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Slate950,
                    surface = Slate900,
                    primary = Purple600,
                    onBackground = TextWhite,
                    onSurface = TextWhite
                )
            ) {
                AnimeApp()
            }
        }
    }
}

