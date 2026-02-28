package com.example.util_idades.shoppinglist.presentation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.util_idades.shoppinglist.data.ProductUIModel

/*
Sólo se podrá interactuar con los productos para borrarlos
 */
@Composable
fun Products(
    products: List<ProductUIModel>,
    modifier: Modifier = Modifier,
    onProductSwipe: (Int) -> Unit = {},
    showPrices: Boolean = true
) {
    val columnState = rememberLazyListState()

    LazyColumn(
        state = columnState,
        modifier = modifier
    ) {
        items(
            count = products.size,
            key = { index -> products[index].id }
        ) { index ->
            Product(
                product = products[index],
                onSwipe = { onProductSwipe(index) },
                showPrices = showPrices
            )
        }
    }
}