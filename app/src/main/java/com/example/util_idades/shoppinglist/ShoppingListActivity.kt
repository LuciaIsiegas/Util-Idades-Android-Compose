package com.example.util_idades

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util_idades.shoppinglist.FormActivity
import com.example.util_idades.shoppinglist.data.DataManager
import com.example.util_idades.shoppinglist.data.ProductUIModel
import com.example.util_idades.shoppinglist.presentation.Products
import java.util.Locale

class ShoppingListActivity : ComponentActivity() {
    private val addProductLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val productName = result.data?.getStringExtra("name") ?: ""
            val productQuantity = result.data?.getIntExtra("quantity", 1) ?: 1
            val productPrice = result.data?.getDoubleExtra("price", 0.0) ?: 0.0
            val productImage = result.data?.getStringExtra("image") ?: ""

            if (productName.isNotBlank()) {
                val db = DataManager(this)
                db.insert(productName, productQuantity, productPrice, productImage)
                recreate()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setContent {
            com.example.util_idades.ui.theme.UtilIdadesTheme(darkTheme = isDarkMode) {
                MainContent()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainContent() {
        val context = LocalContext.current
        val db = remember { DataManager(context) }
        var products by remember { mutableStateOf(emptyList<ProductUIModel>()) }
        var refreshTrigger by remember { mutableIntStateOf(0) }
        val sharedPreferences = remember { PreferenceManager.getDefaultSharedPreferences(context) }
        val orderPreference = remember(refreshTrigger) {
            sharedPreferences.getString("shopping_list_order", "Alfabético") ?: "Alfabético"
        }
        val showPricesPreference = remember(refreshTrigger) {
            sharedPreferences.getBoolean("show_prices", true)
        }

        LaunchedEffect(refreshTrigger) {
            products = db.getProducts()
        }

        fun getSortedProducts(): List<ProductUIModel> {
            return when (orderPreference) {
                "Alfabético" -> products.sortedBy { it.name.lowercase(Locale.getDefault()) }
                "Precio" -> products.sortedByDescending { it.price * it.quantity } // Precio total descendente
                "Fecha" -> products // Los más nuevos primero (ya vienen ordenados por ID que es autoincremental)
                else -> products
            }
        }
        val sortedProducts = getSortedProducts()


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CustomTopBar(
                    "Lista de la Compra",
                    onBackClick = {
                        (context as ComponentActivity).finish()
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                if (sortedProducts.isEmpty()) {
                    Text(
                        text = "Tu lista de la compra está vacía.\n" +
                                "¡Hora de llenarla!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp)
                        .width(400.dp)
                        .align(Alignment.Center)
                ) {
                    Spacer(modifier = Modifier.height(75.dp))
                    Box(
                        modifier = Modifier
                            .height(680.dp)
                    ) {
                        Products(
                            sortedProducts,
                            onProductSwipe = { productIndex ->
                                val name = sortedProducts.get(productIndex).name
                                db.delete(sortedProducts[productIndex].id)
                                refreshTrigger++
                            },
                            showPrices = showPricesPreference
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    ElevatedButton(
                        modifier = Modifier
                            .width(300.dp)
                            .height(55.dp)
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            val intent = Intent(context, FormActivity::class.java)
                            addProductLauncher.launch(intent)
                        }
                    ) {
                        Text(
                            text = "Añadir producto",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

