package com.example.util_idades

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util_idades.ui.theme.UtilIdadesTheme
import kotlin.math.sqrt

val responses = listOf(
    "Sí, definitivamente",
    "Por supuesto",
    "Sin ninguna duda",
    "Puedes contar con ello",
    "Es cierto",
    "Como yo lo veo, sí",
    "Lo más probable",
    "Las perspectivas son buenas",
    "Sí",
    "Las señales apuntan a que sí",
    "Mejor no decirte ahora",
    "No puedo predecirlo ahora",
    "Concéntrate y pregunta de nuevo",
    "No cuentes con ello",
    "Mi respuesta es no",
    "Mis fuentes dicen que no",
    "Las perspectivas no son buenas",
    "Muy dudoso",
    "¿En serio me preguntas eso?",
    "La bola necesita un café antes de responder",
    "Pregúntale a tu madre",
    "Ni con un telescopio lo veo claro",
    "El universo se ha quedado sin señal",
    "Error 404: respuesta no encontrada",
    "Sí... o no... quizás... ¿qué preguntabas?",
    "La bola del 8 está ocupada, llame más tarde",
    "Solo si invitas a pizza",
    "Mis poderes mágicos dicen... que mires por la ventana",
    "Depende de a quién le preguntes",
    "La bola ríe y no responde"
)

class Magic8BallActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setContent {
            UtilIdadesTheme(darkTheme = isDarkMode) {
                Magic8BallScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Magic8BallScreen() {
    val context = LocalContext.current
    var currentResponse by remember { mutableStateOf<String?>(null) }
    var isShaking by remember { mutableStateOf(false) }

    // Animación al agitar
    val scale by animateFloatAsState(
        targetValue = if (isShaking) 1.08f else 1f,
        animationSpec = tween(150),
        label = "shake_scale"
    )

    // Efecto de agitar
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        var lastShakeTime = 0L
        val shakeThreshold = 12f

        val listener = object : SensorEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

                if (acceleration > shakeThreshold) {
                    val now = System.currentTimeMillis()
                    if (now - lastShakeTime > 1000) {
                        lastShakeTime = now
                        isShaking = true
                        currentResponse = responses.random()

                        // Vibración feedback
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                "Bola del 8",
                onBackClick = { (context as ComponentActivity).finish() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // BOLA 8
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A2E)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16213E)),
                    contentAlignment = Alignment.Center
                ) {
                    // Cuando se agite y por lo tanto cambie la respuesta, se hará la animacion
                    AnimatedContent(
                        targetState = currentResponse,
                        transitionSpec = {
                            (fadeIn(spring(50F)) + scaleIn(spring(50F), initialScale = 0.7f))
                                .togetherWith(fadeOut(tween(200)) + scaleOut(tween(200)))
                        },
                        label = "response_anim"
                    ) { response ->
                        if (response == null) {
                            Text(
                                text = "8",
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = response,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (currentResponse == null) "Piensa en tu pregunta\ny agita el móvil"
                else "¡Agita de nuevo para\notra respuesta!",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Pool8Preview() {
    UtilIdadesTheme {
        Magic8BallScreen()
    }
}
