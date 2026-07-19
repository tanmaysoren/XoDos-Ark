package app.xodos2.ui.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DrawerAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = Color(0xFF161616),
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // Calculate the drawer width (up to 312.dp)
    val drawerWidth = 312.dp
    val remainingSpace = screenWidth - drawerWidth

    // On wide screens (tablets), position centered in the left region.
    // On narrow screens (phones), align center-left but clamp within screen.
    val dialogWidth = if (screenWidth >= 600.dp) 360.dp else (screenWidth - 48.dp).coerceAtMost(320.dp)
    
    val startPadding = if (remainingSpace > dialogWidth) {
        (remainingSpace - dialogWidth) / 2
    } else {
        16.dp
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismissRequest), // dismiss when clicking outside
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                modifier = modifier
                    .padding(start = startPadding)
                    .width(dialogWidth)
                    .clickable(enabled = false) { /* prevent clicking inside from dismissing */ },
                shape = shape,
                color = containerColor,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    if (title != null) {
                        Box(modifier = Modifier.padding(bottom = 16.dp)) {
                            ProvideTextStyle(value = MaterialTheme.typography.headlineSmall.copy(color = Color.White)) {
                                title()
                            }
                        }
                    }
                    
                    if (text != null) {
                        Box(
                            modifier = Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(bottom = 24.dp)
                        ) {
                            ProvideTextStyle(value = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))) {
                                text()
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dismissButton != null) {
                            dismissButton()
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        confirmButton()
                    }
                }
            }
        }
    }
}
