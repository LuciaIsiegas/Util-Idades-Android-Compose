package com.example.util_idades

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util_idades.ui.theme.UtilIdadesTheme

data class AppFeature(
    val id: Int,
    val title: String,
    val description: String,
    val icon: Int,
    val activityClass: Class<*>
)

class MainActivity : ComponentActivity() {
    private var currentDarkMode by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        currentDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setContent {
            UtilIdadesTheme(darkTheme = currentDarkMode) {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val newDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        if (newDarkMode != currentDarkMode) {
            currentDarkMode = newDarkMode
            recreate() // Forzar recreación
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current

    val appFeatures = remember {
        listOf(
            AppFeature(
                id = 1,
                title = "Calculadora",
                description = "Realiza operaciones matemáticas básicas",
                icon = R.drawable.calculator,
                activityClass = CalculatorActivity::class.java
            ),
            AppFeature(
                id = 2,
                title = "Cambio de Divisa",
                description = "Convierte entre diferentes divisas",
                icon = R.drawable.euro,
                activityClass = ConversorActivity::class.java
            ),
            AppFeature(
                id = 3,
                title = "Lista de la Compra",
                description = "Organiza tus compras",
                icon = R.drawable.cart,
                activityClass = ShoppingListActivity::class.java
            ),
            AppFeature(
                id = 4,
                title = "Noticias",
                description = "Últimas noticias del día",
                icon = R.drawable.news,
                activityClass = NewsActivity::class.java
            ),
            AppFeature(
                id = 4,
                title = "Lector QR",
                description = "Escanea códigos QR",
                icon = R.drawable.qr,
                activityClass = QrScannerActivity::class.java
            ),
            AppFeature(
                id = 5,
                title = "Bola del 8",
                description = "Pregunta y agita",
                icon = R.drawable.pool_8,
                activityClass = Magic8BallActivity::class.java
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Util-Idades",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,      // color de fondo
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer // color del título
                ),
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, SettingsActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Configuración",
                            modifier = Modifier
                                .size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        ) // O usa una imagen: Image(painterResource(R.drawable.settings), ...)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera ocupa las 2 columnas
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Simplemente Útil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Soluciones rápidas, sin complicaciones",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Cards en cuadrícula
            items(appFeatures.size) { index ->
                val feature = appFeatures[index]
                FeatureCard(
                    feature = feature,
                    onClick = {
                        val intent = Intent(context, feature.activityClass)
                        context.startActivity(intent)
                    }
                )
            }

            // Espaciado final
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FeatureCard(
    feature: AppFeature,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = feature.icon),
                contentDescription = feature.title,
                modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 30.dp)
                    .size(150.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = feature.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    UtilIdadesTheme {
        MainScreen()
    }
}