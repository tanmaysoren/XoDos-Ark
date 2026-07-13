package app.xodos2.ui.drawer.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerMenu(
    title: String,
    labels: DrawerMenuLabels,
    options: DrawerMenuOptions,
    actions: DrawerMenuActions,
    modifier: Modifier = Modifier,
    showDebianDesktop: Boolean = false,
    extraContent: (@Composable () -> Unit)? = null,
) {
    var boostEnabled by remember { mutableStateOf(false) }
    var latencyMode by remember { mutableStateOf("ULTRA-LOW") }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            // High Tech Cyber Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "XODOS2 // CONTAINER NODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50)
                        )
                )
            }

            Spacer(Modifier.height(8.dp))

            // NEW SECTION: Core Diagnostics & Telemetry
            DrawerExpandableSection(title = "Diagnostics & Performance", defaultExpanded = true) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    // CPU Load
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CPU LOAD:",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (boostEnabled) "[||||||||░░] 78%" else "[||||░░░░░░] 38%",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = if (boostEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // Core Memory
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CORE MEM:",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "1.8G / 8.0G",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // System Link Integrity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ARK STABILITY:",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "SECURED // 99.9%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Interactive Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // BOOST TOGGLE
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (boostEnabled) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (boostEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable { boostEnabled = !boostEnabled }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (boostEnabled) "BOOST: ON" else "BOOST: OFF",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (boostEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // LATENCY MODE TOGGLE
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    latencyMode = if (latencyMode == "ULTRA-LOW") "BALANCED" else "ULTRA-LOW"
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NET: $latencyMode",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            DrawerExpandableSection(title = "Display Platforms", defaultExpanded = true) {
                DrawerPrimaryItem(
                    title = "Wayland Environment",
                    subtitle = "High performance modular windowing system",
                    onTap = actions.onDesktopClick,
                    onLongPress = actions.onDesktopLongPress,
                )

                if (showDebianDesktop) {
                    DrawerPrimaryItem(
                        title = "X11 Server Core",
                        subtitle = "Classic stable Xorg rendering server",
                        onTap = actions.onDebianDesktopClick,
                        onLongPress = actions.onDebianDesktopLongPress,
                    )
                }

                DrawerPrimaryItem(
                    title = "System Terminal",
                    subtitle = "Direct interactive container shell command interface",
                    onTap = actions.onTerminalClick,
                )

                DrawerDropdownField(
                    label = "Default Boot display",
                    value = labels.launcherDefaultLabel,
                    options = options.launcherDefaultOptions,
                    onSelect = {
                        actions.onLauncherDefaultSelect(it)
                        actions.onCloseDrawerRequest()
                    },
                )
            }

            Spacer(Modifier.height(6.dp))

            DrawerExpandableSection(title = "System Drivers", defaultExpanded = true) {
                DrawerDropdownField(
                    label = "Vulkan Driver",
                    value = labels.desktopVulkanLabel,
                    options = options.desktopVulkanOptions,
                    onSelect = {
                        actions.onDesktopVulkanSelect(it)
                        actions.onCloseDrawerRequest()
                    },
                )

                DrawerDropdownField(
                    label = "OpenGL Driver",
                    value = labels.desktopOpenGLLabel,
                    options = options.desktopOpenGLOptions,
                    onSelect = {
                        actions.onDesktopOpenGLSelect(it)
                        actions.onCloseDrawerRequest()
                    },
                )

                DrawerExpandableSection(title = "View Parameters", defaultExpanded = true) {
                    DrawerDropdownField(
                        label = "Mouse emulation",
                        value = labels.mouseModeLabel,
                        options = options.mouseModeOptions,
                        onSelect = {
                            actions.onMouseModeSelect(it)
                            actions.onCloseDrawerRequest()
                        },
                    )
                    DrawerDropdownField(
                        label = "Render Resolution",
                        value = labels.resolutionPercentLabel,
                        options = options.resolutionPercentOptions,
                        onSelect = {
                            actions.onResolutionPercentSelect(it)
                            actions.onCloseDrawerRequest()
                        },
                    )
                    DrawerDropdownField(
                        label = "Display Scaling",
                        value = labels.scalePercentLabel,
                        options = options.scalePercentOptions,
                        onSelect = {
                            actions.onScalePercentSelect(it)
                            actions.onCloseDrawerRequest()
                        },
                    )
                }
                
                Spacer(Modifier.height(4.dp))
                DrawerTextItem(title = "▶ TRIGGER SYSTEM KEYBOARD", onClick = actions.onKeyboardClick)
            }

            Spacer(Modifier.height(6.dp))

            DrawerExpandableSection(title = "Terminal Parameters", defaultExpanded = true) {
                DrawerDropdownField(
                    label = "Terminal font",
                    value = labels.terminalFontLabel,
                    options = options.terminalFontOptions,
                    onSelect = {
                        actions.onTerminalFontSelect(it)
                        actions.onCloseDrawerRequest()
                    },
                )

                DrawerDropdownField(
                    label = "Active Session",
                    value = labels.terminalSessionLabel,
                    options = options.terminalSessionOptions,
                    onSelect = {
                        actions.onTerminalSessionSelect(it)
                        actions.onCloseDrawerRequest()
                    },
                )
            }

            if (extraContent != null) {
                Spacer(Modifier.height(6.dp))
                extraContent()
            }
        }
    }
}

@Composable
private fun DrawerPrimaryItem(
    title: String,
    subtitle: String,
    onTap: () -> Unit,
    onLongPress: (() -> Unit)?,
) {
    val buttonShape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .clip(buttonShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = buttonShape
            )
            .pointerInput(title) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress?.invoke() }
                )
            }
            .padding(vertical = 12.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun DrawerPrimaryItem(
    title: String,
    subtitle: String,
    onTap: () -> Unit,
) = DrawerPrimaryItem(title = title, subtitle = subtitle, onTap = onTap, onLongPress = null)

@Composable
private fun DrawerTextItem(
    title: String,
    onClick: () -> Unit,
) {
    val buttonShape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 2.dp)
            .clip(buttonShape)
            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f),
                shape = buttonShape
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
            ),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}
