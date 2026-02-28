package com.example.util_idades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.example.util_idades.ui.theme.UtilIdadesTheme
import java.util.concurrent.Executors

class QrScannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setContent {
            UtilIdadesTheme(darkTheme = isDarkMode) {
                QrScannerScreen()
            }
        }
    }
}

sealed class QrScreenState {
    object RequestingPermission : QrScreenState()
    object PermissionDenied : QrScreenState()
    object Scanning : QrScreenState()
    data class Result(val content: String) : QrScreenState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen() {
    val context = LocalContext.current
    var screenState by remember { mutableStateOf<QrScreenState>(QrScreenState.RequestingPermission) }

    // Launcher para pedir el permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        screenState = if (granted) QrScreenState.Scanning
        else QrScreenState.PermissionDenied
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                "Lector QR", onBackClick = { (context as ComponentActivity).finish() })
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = screenState) {
                // Pide permiso
                is QrScreenState.RequestingPermission -> {
                    PermissionRequestScreen(
                        onRequestPermission = {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        })
                }

                // Denegan permiso
                is QrScreenState.PermissionDenied -> {
                    PermissionDeniedScreen(
                        onRetry = {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        })
                }

                is QrScreenState.Scanning -> {
                    CameraPreviewScreen(
                        onQrDetected = { content ->
                            screenState = QrScreenState.Result(content)
                            // Si es una URL la abre automáticamente
                            if (content.startsWith("http://") || content.startsWith("https://")) {
                                // El intent funciona similar a cuando cambiamos de activity pero esta vez abre el enlace.
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(content))
                                context.startActivity(intent)
                            }
                        })
                }

                is QrScreenState.Result -> {
                    QrResultScreen(
                        content = state.content,
                        onScanAgain = { screenState = QrScreenState.Scanning })
                }
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.camera),
            contentDescription = "Camara",
            modifier = Modifier.size(150.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Permiso de cámara",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Para escanear códigos QR necesitamos acceso a tu cámara. " + "No se grabará ni almacenará ninguna imagen.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .width(300.dp)
                .height(55.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("Permitir acceso a la cámara", fontSize = 16.sp)
        }
    }
}

@Composable
fun PermissionDeniedScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.denied_svgrepo_com),
            contentDescription = "Denegado",
            modifier = Modifier.size(150.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Permiso denegado",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Sin acceso a la cámara no es posible escanear QR. " + "Puedes conceder el permiso desde Ajustes > Aplicaciones > Util-Idades > Permisos.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .width(300.dp)
                .height(55.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("Intentar de nuevo", fontSize = 16.sp)
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreviewScreen(onQrDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasDetected by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Preview de la cámara
        // Funciona como puente entre compose y las vistas clásicas de Android
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                // Procesador de la camara, puede tardar en estar listo
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    // To-do lo de dentro se ejecuta de manera asincrona una vez a camara esta disponible
                    val cameraProvider = cameraProviderFuture.get()

                    // Muestra lo que captura la camara
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // Escanea QR, desde un hilo secundario para no bloquear la camara
                    val barcodeScanner = BarcodeScanning.getClient()
                    val executor = Executors.newSingleThreadExecutor()

                    // Aqui conseguimos analizar solo el ultimo frame de la camara
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                        .also { analysis ->
                            // Importante para dejar de leer frames una vez detectado el QR
                            analysis.setAnalyzer(executor) { imageProxy ->
                                if (!hasDetected) {
                                    val mediaImage = imageProxy.image // obtiene el frame
                                    if (mediaImage != null) {
                                        // Transforma la imagen para escanear el qr
                                        val image = InputImage.fromMediaImage(
                                            mediaImage, imageProxy.imageInfo.rotationDegrees
                                        )
                                        barcodeScanner.process(image)
                                            .addOnSuccessListener { barcodes ->
                                                barcodes.firstOrNull()?.rawValue?.let { value ->
                                                    if (!hasDetected) {
                                                        hasDetected = true
                                                        onQrDetected(value)
                                                    }
                                                }
                                            }.addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }, modifier = Modifier.fillMaxSize()
        )

        // Marco de escaneo centrado
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Apunta la cámara al código QR",
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QrResultScreen(content: String, onScanAgain: () -> Unit) {
    val isUrl = content.startsWith("http://") || content.startsWith("https://")
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = if (isUrl) R.drawable.url else R.drawable.correct),
            contentDescription = "Enlace",
            modifier = Modifier.size(150.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isUrl) "URL detectada" else "Contenido del QR",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Caja con el contenido
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Text(
                text = content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Si es URL, botón para abrirla (por si el auto-open falló)
        if (isUrl) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(content))
                    context.startActivity(intent)
                }, modifier = Modifier
                    .width(300.dp)
                    .height(55.dp), shape = RoundedCornerShape(50)
            ) {
                Text("Abrir enlace", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onScanAgain,
            modifier = Modifier
                .width(300.dp)
                .height(55.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("Escanear otro QR", fontSize = 16.sp)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ScannerPreview() {
    UtilIdadesTheme {
        QrScannerScreen()
    }
}