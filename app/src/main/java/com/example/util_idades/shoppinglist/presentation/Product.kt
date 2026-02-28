package com.example.util_idades.shoppinglist.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.util_idades.shoppinglist.data.ProductUIModel
import kotlin.math.roundToInt

/*
Cómo se mostrará cada producto en la lista
 */
@Composable
fun Product(
    product: ProductUIModel,
    onSwipe: () -> Unit = {},
    showPrices: Boolean = true
) {
    // recordamos el estado inicial del draggable
    val dragState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.START
        )
    }

    // Efecto realizado si se desplaza el elemento
    LaunchedEffect(dragState.settledValue) {
        if (dragState.settledValue == DragAnchors.END) {
            onSwipe()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(
                color = MaterialTheme.colorScheme.onErrorContainer,
                shape = CircleShape
            )
    ) {
        Text(
            text = "Eliminar",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp, 0.dp, 0.dp, 0.dp)
                .align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .onSizeChanged() { layoutSize ->
                    dragState.updateAnchors(
                        DraggableAnchors {
                            DragAnchors.START at 0f
                            DragAnchors.END at layoutSize.width.toFloat()
                        }
                    )
                }
                .offset() {
                    IntOffset(dragState.requireOffset().roundToInt(), 0)
                }
                .anchoredDraggable(
                    state = dragState,
                    orientation = Orientation.Horizontal
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        CircleShape
                    )
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = CircleShape
                    )
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .align(Alignment.CenterVertically)
                        .clip(CircleShape)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = CircleShape
                        )
                )

                Column(
                    modifier = Modifier
                        .padding(20.dp, 0.dp)
                        .width(120.dp)
                        .wrapContentHeight()
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = product.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    Text(
                        text = "x " + product.quantity.toString(),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }

                if (showPrices) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .wrapContentHeight()
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "${"%.2f".format(product.price)}€/ud",
                            fontSize = 15.sp,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .width(100.dp)
                        )
                        Text(
                            text = "${"%.2f".format(product.price * product.quantity)} €",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .width(100.dp)
                        )
                    }
                }


            }
        }
    }
}

private enum class DragAnchors {
    START,
    END
}