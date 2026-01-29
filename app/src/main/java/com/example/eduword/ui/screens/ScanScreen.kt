package com.example.eduword.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.eduword.data.ocr.DetectedWordRow
import com.example.eduword.data.ocr.GermanWordParser
import com.example.eduword.data.ocr.TextScanner
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(onResult: (List<DetectedWordRow>) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bmp ->
            imageBitmap = bmp
        }
    )

    val pickFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                imageBitmap = uri.toBitmap(context)
            }
        }
    )

    fun scanBitmap() {
        val bmp = imageBitmap ?: return
        isScanning = true
        error = null
        scope.launch {
            try {
                val text = TextScanner.scan(bmp)
                val rows = GermanWordParser.parse(text)
                onResult(rows)
            } catch (t: Throwable) {
                error = t.message
            } finally {
                isScanning = false
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Import from image") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Selected image",
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
                Button(
                    onClick = { scanBitmap() },
                    enabled = !isScanning,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Scan")
                    }
                }
            } ?: Box(modifier = Modifier.weight(1f)) // Spacer

            if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { takePictureLauncher.launch() }, modifier = Modifier.weight(1f).height(52.dp)) {
                    Text("Take Photo")
                }
                Button(onClick = { pickFromGalleryLauncher.launch("image/*") }, modifier = Modifier.weight(1f).height(52.dp)) {
                    Text("From Gallery")
                }
            }
        }
    }
}

private fun Uri.toBitmap(context: Context): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source)
    }
}
