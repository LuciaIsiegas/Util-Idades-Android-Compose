package com.example.util_idades.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.util_idades.CustomTopBar
import com.example.util_idades.shoppinglist.presentation.listImages
import com.example.util_idades.ui.theme.UtilIdadesTheme

class ImagesActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        setContent {
            UtilIdadesTheme(darkTheme = isDarkMode) {
                val context = LocalContext.current
                var selectedImage by remember { mutableStateOf("") }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CustomTopBar(
                            "Seleccionar imagen",
                            onBackClick = {
                                (context as ComponentActivity).finish()
                            }
                        )
                    }
                ) { innerPadding ->
                    // envia datos en este cso la url de la imagen
                    val resultIntent = Intent()
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                            .padding(8.dp)
                    ) {
                        items(listImages) { photo ->
                            Box(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .then(
                                        if (selectedImage == photo)
                                            Modifier.border(
                                                width = 3.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        else Modifier
                                    )
                                    .clickable {
                                        selectedImage = photo
                                        resultIntent.putExtra("image", photo)
                                        setResult(RESULT_OK, resultIntent)
                                        finish()
                                    }
                            ) {
                                AsyncImage(
                                    model = photo,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,  // ← rellena sin deformar
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
