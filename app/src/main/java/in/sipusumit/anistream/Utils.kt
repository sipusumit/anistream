package `in`.sipusumit.anistream

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri


// Brush Extension
fun Brush.Companion.trBr(colors: List<Color>) = linearGradient(
    colors = colors,
    start = androidx.compose.ui.geometry.Offset(0f, 0f),
    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

fun openIn1DM(
    context: Context,
    url: String,
//    fileName: String,
    headers: Map<String, String>
) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setPackage("idm.internet.download.manager")
    intent.setComponent(
        ComponentName(
            "idm.internet.download.manager",
            "idm.internet.download.manager.UrlHandlerDownloader"
        )
    )
    intent.setData(url.toUri())


// Adding headers
//    intent.putExtra("com.android.extra.filename", "custom_name.zip") // Optional
    intent.putExtra("android.intent.extra.TEXT", url) // URL again
    intent.putExtra("headers", headers.entries.joinToString("\n") { "${it.key}: ${it.value}"})


// Add referrer if needed
    intent.putExtra("referrer", headers["Referer"] ?: "")

//    try {
//        context.startActivity(intent)
//    } catch (e: ActivityNotFoundException) {
//        // 1DM is not installed
//    }

//    val intent = Intent("idm.internet.download.manager").apply {
//        putExtra("link", url)
////        putExtra("title", fileName)
//
//        // 🔑 IMPORTANT
//        putExtra("referer", headers["Referer"] ?: "")
//
//        // 🔑 Headers as newline-separated string
//        putExtra(
//            "headers",
//            headers.entries.joinToString("\n") { "${it.key}: ${it.value}" }
//        )
//    }
//
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "1DM not installed",
            Toast.LENGTH_SHORT
        ).show()
    }
}