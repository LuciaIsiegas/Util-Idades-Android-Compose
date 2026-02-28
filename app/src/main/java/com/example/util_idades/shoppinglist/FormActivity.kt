package com.example.util_idades.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.util_idades.CustomTopBar
import com.example.util_idades.shoppinglist.presentation.listImages

class FormActivity : ComponentActivity() {
    // Representa el valor que va a recoger de la activity que llama en AsyncImage
    private var imageSelected: String by mutableStateOf(listImages[0])
    private val addImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == RESULT_OK) {
                imageSelected = result.data?.getStringExtra("image") ?: listImages[0]
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        imageSelected = intent.getStringExtra("image") ?: ""
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        setContent {
            com.example.util_idades.ui.theme.UtilIdadesTheme(darkTheme = isDarkMode) {
                FormProduct()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FormProduct() {
        var productName by remember { mutableStateOf("") }
        var productQuantity by remember { mutableStateOf("") }
        var productPrice by remember { mutableStateOf("") }
        val context = LocalContext.current

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CustomTopBar(
                    "Añadir producto",
                    onBackClick = {
                        (context as ComponentActivity).finish()
                    }
                )
            }
        ) { innerPadding ->
            BackHandler {
                setResult(RESULT_CANCELED)
                finish()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp)
                        .width(300.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Spacer(modifier = Modifier.height(95.dp))

                    Box(
                        modifier = Modifier
                            .size(300.dp, 300.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable {
                                val intent = Intent(context, ImagesActivity::class.java)
                                addImageLauncher.launch(intent)
                            }
                    ) {
                        AsyncImage(
                            model = imageSelected,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        value = productName,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        ),
                        label = {
                            Text(
                                text = "Producto",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        onValueChange = {
                            if (it.length <= 20) {
                                productName = it
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = productQuantity,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = {
                            Text(
                                text = "Cantidad",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        onValueChange = {
                            if (it.matches(Regex("^\\d*$"))) {
                                productQuantity = it
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = productPrice,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        label = {
                            Text(
                                text = "Precio",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        onValueChange = {
                            if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                productPrice = it.replace(",", ".")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(132.dp))

                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                            disabledContentColor = MaterialTheme.colorScheme.secondary
                        ),
                        onClick = {
                            if (productName.isBlank()
                                || productQuantity.isBlank()
                                || productPrice.isBlank()
                            ) {
                                Toast.makeText(
                                    context,
                                    "Por favor rellene todos los campos",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                // Le enviamos loa datos con el setResult y volvemos a la pagina anterior con finish
                                intent.putExtra("name", productName.trim())
                                intent.putExtra("quantity", productQuantity.toInt())
                                intent.putExtra("price", productPrice.toDouble())
                                intent.putExtra("image", imageSelected)
                                setResult(RESULT_OK, intent)
                                Toast.makeText(
                                    context,
                                    "${productName} añadido con éxito",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                        }
                    ) {
                        Text(
                            text = "Añadir producto",
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

