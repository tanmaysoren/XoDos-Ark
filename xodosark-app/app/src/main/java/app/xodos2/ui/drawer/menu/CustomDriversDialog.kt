package app.xodos2.ui.drawer.menu

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xodos2.ui.glass.glassBlurModifier
import app.xodos2.ui.glass.glassDialogStyle
import app.xodos2.ui.glass.GlassButton
import app.xodos2.ui.prefs.AppPrefs
import app.xodos2.ui.prefs.AppPrefs.CustomDriverInfo
import java.io.File

@Composable
fun CustomDriversDialog(
    customDrivers: List<CustomDriverInfo>,
    onAddCustomDriver: (CustomDriverInfo) -> Unit,
    onDeleteCustomDriver: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var showAddForm by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var driverFilePathInput by remember { mutableStateOf("") }
    var driverNameInput by remember { mutableStateOf("") }
    var driverTypeInput by remember { mutableStateOf("Vulkan") } // "Vulkan" or "OpenGL"

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            var displayName = ""
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIdx >= 0) {
                        displayName = cursor.getString(nameIdx)
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }

            if (displayName.isNotBlank()) {
                driverFilePathInput = displayName
                val baseName = displayName.substringBeforeLast(".")
                    .replace("-", "_")
                    .replace(".", "_")
                    .uppercase()
                if (driverNameInput.isBlank()) {
                    driverNameInput = baseName
                }
                if (displayName.endsWith(".json", ignoreCase = true) || displayName.contains("turnip", ignoreCase = true) || displayName.contains("freedreno", ignoreCase = true) || displayName.contains("vulkan", ignoreCase = true)) {
                    driverTypeInput = "Vulkan"
                } else if (displayName.endsWith(".so", ignoreCase = true) || displayName.contains("mesa", ignoreCase = true) || displayName.contains("gallium", ignoreCase = true)) {
                    driverTypeInput = "OpenGL"
                }
            }
            showAddForm = true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .glassDialogStyle(),
        containerColor = Color.Transparent,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Custom GPU Drivers",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select a driver library file (.so or .json) from storage to add custom real GPU drivers (e.g., Turnip, Freedreno, Adreno).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                Spacer(Modifier.height(12.dp))

                if (customDrivers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No custom drivers added yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                    ) {
                        items(customDrivers) { driver ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.06f))
                                    .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = driver.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (driver.type.equals("Vulkan", ignoreCase = true))
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                                else
                                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                                            ) {
                                                Text(
                                                    text = driver.type.uppercase(),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (driver.type.equals("Vulkan", ignoreCase = true))
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        if (driver.filePath.isNotBlank()) {
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = driver.filePath,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteCustomDriver(driver.name) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Delete custom driver",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                GlassButton(
                    onClick = {
                        try {
                            filePickerLauncher.launch(arrayOf("*/*"))
                        } catch (e: Exception) {
                            showAddForm = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FolderOpen,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Select Driver File", color = Color.White)
                }
            }
        },
        confirmButton = {
            GlassButton(onClick = onDismiss) {
                Text("Close", color = Color.White)
            }
        }
    )

    if (showAddForm) {
        AlertDialog(
            onDismissRequest = {
                showAddForm = false
                selectedUri = null
                driverFilePathInput = ""
                driverNameInput = ""
            },
            modifier = Modifier.glassDialogStyle(),
            containerColor = Color.Transparent,
            title = {
                Text(
                    text = "Configure Custom Driver",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Driver Name:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = driverNameInput,
                        onValueChange = { driverNameInput = it },
                        singleLine = true,
                        placeholder = { Text("e.g. TURNIP_ADRENO_730", color = Color.White.copy(alpha = 0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFA855F7),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Driver Type:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = driverTypeInput == "Vulkan",
                            onClick = { driverTypeInput = "Vulkan" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFA855F7), unselectedColor = Color.White.copy(alpha = 0.6f))
                        )
                        Text("Vulkan", color = Color.White, modifier = Modifier.padding(end = 16.dp))

                        RadioButton(
                            selected = driverTypeInput == "OpenGL",
                            onClick = { driverTypeInput = "OpenGL" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFA855F7), unselectedColor = Color.White.copy(alpha = 0.6f))
                        )
                        Text("OpenGL", color = Color.White)
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Driver File Path:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = driverFilePathInput,
                        onValueChange = { driverFilePathInput = it },
                        singleLine = true,
                        placeholder = { Text("/path/to/driver.so or .json", color = Color.White.copy(alpha = 0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFA855F7),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                GlassButton(
                    onClick = {
                        val nameClean = driverNameInput.trim().replace(Regex("[^a-zA-Z0-9_]"), "_").uppercase()
                        if (nameClean.isNotBlank()) {
                            var finalPath = driverFilePathInput.trim()
                            val uri = selectedUri
                            if (uri != null) {
                                val copiedPath = copyUriToCustomDriverFile(context, uri, nameClean)
                                if (copiedPath.isNotBlank()) {
                                    finalPath = copiedPath
                                }
                            }
                            onAddCustomDriver(
                                CustomDriverInfo(
                                    name = nameClean,
                                    type = driverTypeInput,
                                    filePath = finalPath
                                )
                            )
                            Toast.makeText(context, "Added driver $nameClean", Toast.LENGTH_SHORT).show()
                        }
                        showAddForm = false
                        selectedUri = null
                        driverFilePathInput = ""
                        driverNameInput = ""
                    }
                ) {
                    Text("Save Driver", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddForm = false
                        selectedUri = null
                        driverFilePathInput = ""
                        driverNameInput = ""
                    }
                ) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}

private fun copyUriToCustomDriverFile(context: Context, uri: Uri, driverName: String): String {
    return try {
        val dir = File(context.filesDir, "custom_drivers").apply { mkdirs() }
        var fileName = "driver"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                fileName = cursor.getString(nameIndex)
            }
        }
        val targetFile = File(dir, "${driverName.lowercase()}_$fileName")
        context.contentResolver.openInputStream(uri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        targetFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
