package app.xodos2.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalDensity
import app.xodos2.ui.glass.FloatingGlassRimAlpha
import app.xodos2.ui.glass.FloatingGlassRimDp
import app.xodos2.ui.glass.floatingGlassBrush
import app.xodos2.ui.glass.glassBlurModifier

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    drawerWidth: Dp = 312.dp,
    drawerShape: Shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp),
    drawerBackgroundColor: Color = Color(0xCC101010),
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        BoxWithConstraints(modifier = modifier) {
            val density = LocalDensity.current
            val maxW = maxWidth
            val computedWidth: Dp = with(density) {
                val px = (maxW.toPx() * 0.88f)
                px.toDp()
            }.coerceAtMost(drawerWidth)

            ModalNavigationDrawer(
                drawerState = drawerState,
                // Disable edge-swipe to open, but keep swipe-to-dismiss once open.
                gesturesEnabled = drawerState.isOpen,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(computedWidth)
                                .padding(start = 8.dp, top = 12.dp, bottom = 12.dp)
                        ) {
                            // Blurry glass background layer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(drawerShape)
                                    .then(glassBlurModifier())
                                    .background(brush = floatingGlassBrush(), shape = drawerShape)
                                    .border(
                                        width = FloatingGlassRimDp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = FloatingGlassRimAlpha),
                                        shape = drawerShape
                                    )
                            )
                            // Unblurred content layer on top
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                            ) {
                                drawerContent()
                            }
                        }
                    }
                },
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                        content()
                    }
                }
            }
        }
    }
}

