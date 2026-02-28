package com.example.util_idades

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.util_idades.ui.theme.UtilIdadesTheme

val newsCountries = linkedMapOf(
    "España"        to "es",
    "México"        to "mx",
    "Argentina"     to "ar",
    "Estados Unidos" to "us",
    "Reino Unido"   to "gb",
)

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setContent {
            UtilIdadesTheme (darkTheme = isDarkMode) {
                SettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    // Estados para las preferencias
    var shoppingListOrder by remember {
        mutableStateOf(
            sharedPreferences.getString("shopping_list_order", "Alfabético") ?: "Alfabético"
        )
    }
    var showPrices by remember {
        mutableStateOf(sharedPreferences.getBoolean("show_prices", true))
    }
    var darkMode by remember {
        mutableStateOf(sharedPreferences.getBoolean("dark_mode", false))
    }
    var newsCountryName by remember {
        mutableStateOf(
            sharedPreferences.getString("news_country_name", "España") ?: "España"
        )
    }

    fun savePreference(key: String, value: Any) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
            }
            apply()
        }
    }

    fun applyDarkMode(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar (
                "Configuración",
                onBackClick = {
                    (context as ComponentActivity).finish()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección: Apariencia
            SettingsSection(title = "Apariencia") {
                SwitchPreference(
                    title = "Modo oscuro",
                    checked = darkMode,
                    onCheckedChange = {
                        darkMode = it
                        savePreference("dark_mode", it)
                        (context as ComponentActivity).recreate()
                    }
                )
            }

            // Sección: Lista de la Compra
            SettingsSection(title = "Lista de la Compra") {
                DropdownPreference(
                    title = "Orden de lista",
                    currentValue = shoppingListOrder,
                    options = listOf("Alfabético", "Fecha", "Precio"),
                    onOptionSelected = {
                        shoppingListOrder = it
                        savePreference("shopping_list_order", it)
                    }
                )

                SwitchPreference(
                    title = "Mostrar precios",
                    checked = showPrices,
                    onCheckedChange = {
                        showPrices = it
                        savePreference("show_prices", it)
                    }
                )
            }

            SettingsSection(title = "Noticias") {
                DropdownPreference(
                    title = "País de las noticias",
                    currentValue = newsCountryName,
                    options = newsCountries.keys.toList(),
                    onOptionSelected = { selectedName ->
                        newsCountryName = selectedName
                        savePreference("news_country_name", selectedName)
                        // Guardamos también el código para usarlo directamente en NewsActivity
                        savePreference("news_country", newsCountries[selectedName] ?: "es")
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            content()
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SwitchPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownPreference(
    title: String,
    currentValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = currentValue,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InfoPreference(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    UtilIdadesTheme {
        SettingsScreen()
    }
}