package app.xodos2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Futuristic Cyberpunk Theme with high-contrast Neon Cyan, Hot Pink and Cyber Yellow. */
@Composable
fun xodos2Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF04010A),          
            surface = Color(0xFF0D051C),             
            surfaceVariant = Color(0xFF1E0E38), 
            primary = Color(0xFFD300F9),       // Glowing Neon Purple
            secondary = Color(0xFF00F0FF),     // Cyber Neon Cyan
            tertiary = Color(0xFFFF007F),      // Cyber Neon Magenta / Pink
            onPrimary = Color(0xFF04010A),           
            onBackground = Color(0xFFE5D5FF),       
            onSurface = Color(0xFFECE5FF),         
            outline = Color(0xFFD300F9),       // Neon Purple outline
            error = Color(0xFFFF2B6D),
            onError = Color.White
        ),
        content = content
    )
}