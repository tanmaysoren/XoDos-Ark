package app.xodos2.ui.glass

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val FloatingGlassCornerDp = 20.dp
val FloatingGlassRimDp = 1.dp
const val FloatingGlassRimAlpha = 0.55f

val GlassDialogScreenInsetDp = 8.dp

val GlassDialogWidthStandardDp = 400.dp
val GlassDialogWidthPickerDp = 280.dp

const val FloatingOverlayScrimAlpha = 0.22f

fun floatingOverlayScrimColor(): Color = Color.Black.copy(alpha = FloatingOverlayScrimAlpha)

internal val FloatingGlassBlurDp = 26.dp

private const val GlassHighlightApi31 = 0.120f
private const val GlassFillApi31 = 0.070f
private const val GlassHighlightLegacy = 0.144f
private const val GlassFillLegacy = 0.081f

fun glassBlurModifier(): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.blur(FloatingGlassBlurDp)
    } else {
        Modifier
    }

fun floatingGlassBrush(): Brush =
    Brush.verticalGradient(
        colors = listOf(
            Color(0xFFD300F9).copy(alpha = 0.18f), // Neon Purple top highlight
            Color(0xFF0D051C).copy(alpha = 0.90f)  // Deep cyber indigo-slate fill
        )
    )
