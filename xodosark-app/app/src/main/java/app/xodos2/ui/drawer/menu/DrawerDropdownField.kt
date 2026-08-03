package app.xodos2.ui.drawer.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xodos2.ui.glass.glassBlurModifier

@Composable
fun DrawerDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    customOptions: List<String> = emptyList(),
    onAddCustomOption: ((String) -> Unit)? = null,
    onDeleteCustomOption: ((String) -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var customDriverInput by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.White.copy(alpha = 0.01f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.20f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expanded = true }
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                        )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                if (onAddCustomOption != null) {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add custom $label driver",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                }

                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { expanded = true }
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = (-140).dp, y = (-40).dp),
            modifier = Modifier
                .then(glassBlurModifier())
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCE18132B),
                            Color(0xE50B0F1E)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            options.forEach { opt ->
                val isCustom = customOptions.contains(opt)
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opt,
                                color = if (isCustom) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (opt == value) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (isCustom && onDeleteCustomOption != null) {
                                IconButton(
                                    onClick = {
                                        onDeleteCustomOption(opt)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Delete custom driver $opt",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelect(opt)
                    }
                )
            }

            if (onAddCustomOption != null) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "+ Add Custom Driver",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        showAddDialog = true
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                customDriverInput = ""
            },
            title = {
                Text(
                    text = "Add Custom $label Driver",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter custom $label driver name (e.g., PANFROST, LIMA, CUSTOM_DRIVER):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customDriverInput,
                        onValueChange = { customDriverInput = it },
                        singleLine = true,
                        placeholder = { Text("Driver name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = customDriverInput.trim().uppercase()
                        if (name.isNotBlank()) {
                            onAddCustomOption?.invoke(name)
                        }
                        showAddDialog = false
                        customDriverInput = ""
                    },
                    enabled = customDriverInput.trim().isNotBlank()
                ) {
                    Text("Add", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        customDriverInput = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
