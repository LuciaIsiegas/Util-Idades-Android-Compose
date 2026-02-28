package com.example.util_idades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.util_idades.model.Article
import com.example.util_idades.model.NewsApiClient
import com.example.util_idades.model.newsCategories
import com.example.util_idades.model.newsCountryLanguage
import com.example.util_idades.ui.theme.UtilIdadesTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class NewsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        setContent {
            UtilIdadesTheme(darkTheme = isDarkMode) {
                NewsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Obtiene las preferencias
    val sharedPreferences = remember { PreferenceManager.getDefaultSharedPreferences(context) }

    var articles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("breaking-news") }
    // Preferencia por país
    val newsCountry  = sharedPreferences.getString("news_country", "es") ?: "es"
    val newsLanguage = newsCountryLanguage[newsCountry] ?: "es"

    // Función para cargar noticias
    fun loadNews(topic: String) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NewsApiClient.service.getTopHeadlines(
                    topic = topic,
                    lang  = newsLanguage
                )
                if (response.isSuccessful) {
                    articles = response.body()?.articles ?: emptyList()
                } else {
                    errorMessage = if (response.code() == 403) "Error ${response.code()}: Has agotado las llamadas a esta API" else "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Sin conexión: ${e.localizedMessage}"
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadNews(selectedCategory)
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                "Noticias",
                onBackClick = { (context as ComponentActivity).finish() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(newsCategories.entries.toList()) { topic ->
                    FilterChip(
                        selected = selectedCategory == topic.value,
                        onClick = {
                            if (selectedCategory != topic.value) {
                                selectedCategory = topic.value
                                loadNews(topic.value)
                            }
                        },
                        label = {
                            Text(
                                text = topic.key,   // ← directamente el nombre en español
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.caution),
                                contentDescription = "Atención",
                                modifier = Modifier
                                    .size(80.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage!!, // Not null asserted
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { loadNews(selectedCategory) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                articles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay noticias disponibles")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(4.dp)) }

                        items(articles) { article ->
                            NewsCard(
                                article = article,
                                onClick = {
                                    // Abre la noticia en el navegador
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                                    context.startActivity(intent)
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCard(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.source.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatNewsDate(article.publishedAt),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Título
            Text(
                text = article.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Imagen (si existe)
            if (!article.image.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = article.image,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// Formatea la fecha ISO 8601 a algo legible
fun formatNewsDate(isoDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
        val date = parser.parse(isoDate)
        formatter.format(date!!)
    } catch (e: Exception) {
        isoDate.take(10)
    }
}

@Preview(showBackground = true)
@Composable
fun NewsPreview() {
    UtilIdadesTheme {
        NewsScreen()
    }
}