package com.example.util_idades

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util_idades.ui.theme.UtilIdadesTheme

class CalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setContent {
            UtilIdadesTheme(darkTheme = isDarkMode) {
                Calculator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calculator() {
    val context = LocalContext.current

    // Estados para la calculadora
    var display by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf(0.0) }
    var currentOperator by remember { mutableStateOf("") }
    var waitingForOperand by remember { mutableStateOf(false) }
    var hasDecimal by remember { mutableStateOf(false) }

    fun handleNumberClick(number: String) {
        if (waitingForOperand) {
            display = number
            waitingForOperand = false
        } else {
            display = if (display == "0") {
                number
            } else {
                display + number
            }
        }
    }

    fun handleOperatorClick(operator: String) {
        val inputValue = display.toDoubleOrNull() ?: 0.0

        when {
            currentOperator.isNotEmpty() && !waitingForOperand -> {
                val result = performCalculation(
                    firstOperand,
                    inputValue,
                    currentOperator
                )
                display = formatResult(result)
                firstOperand = result
            }
            else -> {
                firstOperand = inputValue
            }
        }

        currentOperator = operator
        waitingForOperand = true
        hasDecimal = false
    }

    fun handleEquals() {
        if (currentOperator.isEmpty() || waitingForOperand) return

        val secondOperand = display.toDoubleOrNull() ?: 0.0
        val result = performCalculation(firstOperand, secondOperand, currentOperator)
        display = formatResult(result)

        firstOperand = result
        currentOperator = ""
        waitingForOperand = true
        hasDecimal = display.contains('.')
    }

    fun handleClear() {
        display = "0"
        firstOperand = 0.0
        currentOperator = ""
        waitingForOperand = false
        hasDecimal = false
    }

    fun handleDecimal() {
        if (!hasDecimal) {
            if (waitingForOperand) {
                display = "0."
                waitingForOperand = false
            } else if (!display.contains('.')) {
                display += "."
            }
            hasDecimal = true
        }
    }

    fun handleBackspace() {
        when {
            display.length > 1 -> {
                display = display.dropLast(1)
                if (display.endsWith('.')) {
                    display = display.dropLast(1)
                    hasDecimal = false
                }
            }
            else -> {
                display = "0"
                hasDecimal = false
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                "Calculadora",
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
        ) {
            // Display de la calculadora
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(24.dp))                          // ← recorta las esquinas
                    .background(MaterialTheme.colorScheme.surfaceVariant)     // ← necesitas un fondo para que se vean
                    .padding(horizontal = 24.dp, vertical = 16.dp),           // ← padding interior
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                // Operación en curso (pequeña, tenue)
                Text(
                    text = if (currentOperator.isNotEmpty())
                        "${formatDouble(firstOperand)} $currentOperator"
                    else "",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Número principal (grande)
                Text(
                    text = display,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 24.sp,
                        maxFontSize = 96.sp,   // ← antes 72.sp
                        stepSize = 2.sp
                    )
                )
            }

            // Teclado de la calculadora
            CalculatorKeyboard(
                onNumberClick = { handleNumberClick(it) },
                onOperatorClick = { handleOperatorClick(it) },
                onEqualsClick = { handleEquals() },
                onClearClick = { handleClear() },
                onDecimalClick = { handleDecimal() },
                onBackspaceClick = { handleBackspace() }
            )
        }
    }
}

@Composable
fun CalculatorKeyboard(
    onNumberClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit,
    onDecimalClick: () -> Unit,
    onBackspaceClick: () -> Unit
) {
    val colorNumber     = MaterialTheme.colorScheme.surfaceVariant
    val colorNumberText = MaterialTheme.colorScheme.onSurfaceVariant
    val colorOperator   = MaterialTheme.colorScheme.secondaryContainer
    val colorOperatorText = MaterialTheme.colorScheme.onSecondaryContainer
    val colorAction     = MaterialTheme.colorScheme.tertiaryContainer
    val colorActionText = MaterialTheme.colorScheme.onTertiaryContainer
    val colorEquals     = MaterialTheme.colorScheme.primary
    val colorEqualsText = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Fila 1: C  ⌫  ÷
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcBtn("C",  Modifier.weight(1f), colorAction,   colorActionText)   { onClearClick() }
            CalcBtn("⌫", Modifier.weight(1f), colorAction,   colorActionText)   { onBackspaceClick() }
            CalcBtn("÷",  Modifier.weight(1f), colorOperator, colorOperatorText) { onOperatorClick("/") }
        }
        // Fila 2: 7 8 9 ×
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcBtn("7", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("7") }
            CalcBtn("8", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("8") }
            CalcBtn("9", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("9") }
            CalcBtn("×", Modifier.weight(1f), colorOperator, colorOperatorText) { onOperatorClick("×") }
        }
        // Fila 3: 4 5 6 -
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcBtn("4", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("4") }
            CalcBtn("5", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("5") }
            CalcBtn("6", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("6") }
            CalcBtn("-", Modifier.weight(1f), colorOperator, colorOperatorText) { onOperatorClick("-") }
        }
        // Fila 4: 1 2 3 +
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcBtn("1", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("1") }
            CalcBtn("2", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("2") }
            CalcBtn("3", Modifier.weight(1f), colorNumber, colorNumberText) { onNumberClick("3") }
            CalcBtn("+", Modifier.weight(1f), colorOperator, colorOperatorText) { onOperatorClick("+") }
        }
        // Fila 5: 0  .  =
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcBtn("0", Modifier.weight(2f), colorNumber, colorNumberText) { onNumberClick("0") }
            CalcBtn(".", Modifier.weight(1f), colorNumber, colorNumberText) { onDecimalClick() }
            CalcBtn("=", Modifier.weight(1f), colorEquals, colorEqualsText) { onEqualsClick() }
        }
    }
}

@Composable
fun CalcBtn(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(35)
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Medium)
    }
}

// Funciones de utilidad para la calculadora
fun performCalculation(
    first: Double,
    second: Double,
    operator: String
): Double {
    return when (operator) {
        "+" -> first + second
        "-" -> first - second
        "×" -> first * second
        "/" -> {
            if (second == 0.0) {
                Double.NaN
            } else {
                first / second
            }
        }
        else -> second
    }
}

fun formatResult(value: Double): String {
    return when {
        value.isNaN() -> "Error"
        value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY -> "Error"
        value % 1 == 0.0 -> value.toLong().toString()
        else -> {
            // Limitar a 8 decimales máximo
            val formatted = String.format("%.8f", value)
            formatted.trimEnd('0').trimEnd('.')
        }
    }
}

fun formatDouble(value: Double): String {
    return if (value % 1 == 0.0) {
        value.toLong().toString()
    } else {
        val formatted = String.format("%.4f", value)
        formatted.trimEnd('0').trimEnd('.')
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    UtilIdadesTheme {
        Calculator()
    }
}