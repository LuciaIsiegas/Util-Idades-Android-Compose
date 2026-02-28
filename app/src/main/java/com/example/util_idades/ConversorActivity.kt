package com.example.util_idades

import android.R
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util_idades.ui.theme.UtilIdadesTheme
import kotlin.math.roundToInt

/*
enum class CurrencyType(val symbol: String, val nombre: String) {
    EUR("€", "Euro"),
    USD("\$", "Dólar estadounidense"),
    KRW("₩", "Won surcoreano"),
}

object ExchangeRates {
    // Base: 1 EUR
    const val EUR_TO_USD = 1.08  // 1 EUR = 1.08 USD
    const val EUR_TO_KRW = 1.460   // 1 EUR = 1.460 KRW
    const val USD_TO_EUR = 1 / EUR_TO_USD
    const val USD_TO_KRW = USD_TO_EUR * EUR_TO_KRW
    const val KRW_TO_EUR = 1 / EUR_TO_KRW
    const val KRW_TO_USD = KRW_TO_EUR * EUR_TO_USD
}

 */
enum class CurrencyType(val symbol: String, val nombre: String) {
    EUR("€", "Euro"),
    USD("$", "Dólar"),
    GBP("£", "Libra"),
    JPY("¥", "Yen"),
    KRW("₩", "Won"),
}

object ExchangeRates {
    // Base: 1 EUR
    const val EUR_TO_USD = 1.08
    const val EUR_TO_GBP = 0.85
    const val EUR_TO_JPY = 160.0
    const val EUR_TO_KRW = 1460.0

    fun convert(amount: Double, from: CurrencyType, to: CurrencyType): Double {
        if (from == to) return amount
        // Convertir primero a EUR como moneda base
        val inEur = when (from) {
            CurrencyType.EUR -> amount
            CurrencyType.USD -> amount / EUR_TO_USD
            CurrencyType.GBP -> amount / EUR_TO_GBP
            CurrencyType.JPY -> amount / EUR_TO_JPY
            CurrencyType.KRW -> amount / EUR_TO_KRW
        }
        // De EUR a destino
        return when (to) {
            CurrencyType.EUR -> inEur
            CurrencyType.USD -> inEur * EUR_TO_USD
            CurrencyType.GBP -> inEur * EUR_TO_GBP
            CurrencyType.JPY -> inEur * EUR_TO_JPY
            CurrencyType.KRW -> inEur * EUR_TO_KRW
        }
    }
}



class ConversorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setContent {
            UtilIdadesTheme (darkTheme = isDarkMode) {
                CurrencyConverter()
            }
        }
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverter() {
    val context = LocalContext.current

    // Estados para la conversión
    var inputAmount by remember { mutableStateOf("1") }
    var fromCurrency by remember { mutableStateOf(CurrencyType.EUR) }
    var toCurrency by remember { mutableStateOf(CurrencyType.USD) }

    // Calculamos el resultado
    val convertedAmount = remember(inputAmount, fromCurrency, toCurrency) {
        calculateConversion(inputAmount, fromCurrency, toCurrency)
    }

    Scaffold(
        topBar = {
            CustomTopBar (
                "Conversor de Moneda",
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Encabezado
            Text(
                text = "Conversor de Moneda",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Convierte entre Euros (€), Yenes (\$) y Libras (₩)",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Tarjeta de entrada
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "MONEDA ORIGEN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Selector de divisa origen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CurrencyType.values().forEach { currency ->
                            FilterChip(
                                selected = fromCurrency == currency,
                                onClick = { fromCurrency = currency },
                                label = { Text("${currency.symbol} ${currency.name}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Campo de entrada
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { newValue ->
                            // Permitir solo números y punto decimal
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                inputAmount = newValue
                            }
                        },
                        label = { Text("Cantidad a convertir") },
                        placeholder = { Text("Ej: 100") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Text(
                                text = fromCurrency.symbol,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            // Flecha de conversión
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("⇄", fontSize = 32.sp, modifier = Modifier.padding(8.dp))
            }

            // Tarjeta de destino
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "MONEDA DESTINO",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Selector de divisa destino
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CurrencyType.values().forEach { currency ->
                            FilterChip(
                                selected = toCurrency == currency,
                                onClick = { toCurrency = currency },
                                label = { Text("${currency.symbol} ${currency.name}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Resultado de la conversión
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "RESULTADO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = toCurrency.symbol,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = convertedAmount,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            Text(
                                text = toCurrency.name,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Información de tasas de cambio
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tasas de Cambio (Aproximadas)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    ExchangeRateRow(from = "1 €", to = "${ExchangeRates.EUR_TO_USD} \$")
                    ExchangeRateRow(from = "1 €", to = "${ExchangeRates.EUR_TO_KRW} ₩")
                    ExchangeRateRow(from = "1 \$", to = "${String.format("%.4f", ExchangeRates.USD_TO_EUR)} €")
                    ExchangeRateRow(from = "1 \$", to = "${String.format("%.4f", ExchangeRates.USD_TO_KRW)} £")
                    ExchangeRateRow(from = "1 ₩", to = "${String.format("%.2f", ExchangeRates.KRW_TO_EUR)} €")
                    ExchangeRateRow(from = "1 ₩", to = "${String.format("%.0f", ExchangeRates.KRW_TO_USD)} ¥")
                }
            }

            // Botón para invertir divisas
            Button(
                onClick = {
                    // Intercambiar las divisas
                    val temp = fromCurrency
                    fromCurrency = toCurrency
                    toCurrency = temp
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("🔄 Invertir Divisas", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ExchangeRateRow(from: String, to: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(from, fontSize = 14.sp)
        Text("=", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        Text(to, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

fun calculateConversion(
    input: String,
    from: CurrencyType,
    to: CurrencyType
): String {
    if (input.isEmpty() || input == ".") return "0.00"

    return try {
        val amount = input.toDouble()
        val result = when {
            from == to -> amount
            from == CurrencyType.EUR && to == CurrencyType.USD -> amount * ExchangeRates.EUR_TO_USD
            from == CurrencyType.EUR && to == CurrencyType.KRW -> amount * ExchangeRates.EUR_TO_KRW
            from == CurrencyType.USD && to == CurrencyType.EUR -> amount * ExchangeRates.USD_TO_EUR
            from == CurrencyType.USD && to == CurrencyType.KRW -> amount * ExchangeRates.USD_TO_KRW
            from == CurrencyType.KRW && to == CurrencyType.EUR -> amount * ExchangeRates.KRW_TO_EUR
            from == CurrencyType.KRW && to == CurrencyType.USD -> amount * ExchangeRates.KRW_TO_USD
            else -> amount
        }

        // Formatear el resultado
        when (to) {
            CurrencyType.JPY -> result.roundToInt().toString() // Yenes sin decimales
            else -> String.format("%.2f", result)
        }
    } catch (e: NumberFormatException) {
        "0.00"
    }
}
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverter() {
    val context = LocalContext.current

    var inputAmount by remember { mutableStateOf("1") }
    var fromCurrency by remember { mutableStateOf(CurrencyType.EUR) }
    var toCurrency by remember { mutableStateOf(CurrencyType.USD) }

    val result = remember(inputAmount, fromCurrency, toCurrency) {
        val amount = inputAmount.toDoubleOrNull() ?: 0.0
        val converted = ExchangeRates.convert(amount, fromCurrency, toCurrency)
        when (toCurrency) {
            CurrencyType.JPY, CurrencyType.KRW -> converted.roundToInt().toString()
            else -> String.format("%.2f", converted)
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                "Cambio de Divisa",
                onBackClick = { (context as ComponentActivity).finish() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionLabel("De")

            CurrencySelector(
                selected = fromCurrency,
                onSelect = { fromCurrency = it }
            )

            OutlinedTextField(
                value = inputAmount,
                onValueChange = { v ->
                    if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d*$"))) inputAmount = v
                },
                label = { Text("Cantidad") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Text(
                        text = fromCurrency.symbol,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = {
                        val temp = fromCurrency
                        fromCurrency = toCurrency
                        toCurrency = temp
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("⇅  Invertir", fontSize = 14.sp)
                }
            }

            SectionLabel("A")

            CurrencySelector(
                selected = toCurrency,
                onSelect = { toCurrency = it }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${toCurrency.symbol} $result",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = toCurrency.nombre,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun CurrencySelector(
    selected: CurrencyType,
    onSelect: (CurrencyType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CurrencyType.values().forEach { currency ->
            val isSelected = currency == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(currency) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currency.symbol,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currency.nombre,
                        fontSize = 9.sp,
                        color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    UtilIdadesTheme {
        CurrencyConverter()
    }
}