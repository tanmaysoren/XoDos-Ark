package app.xodos2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.glassDialogStyle(): Modifier = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xE6131124),
                Color(0xF20B0F19)
            )
        ),
        shape = RoundedCornerShape(24.dp)
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.28f),
                Color.White.copy(alpha = 0.05f)
            )
        ),
        shape = RoundedCornerShape(24.dp)
    )