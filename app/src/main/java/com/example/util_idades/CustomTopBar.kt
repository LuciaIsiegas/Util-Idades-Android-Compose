package com.example.util_idades

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    text: String,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Atrás",
                    modifier = Modifier
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =MaterialTheme.colorScheme.primaryContainer,      // color de fondo
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer // color del título
        )
    )
}