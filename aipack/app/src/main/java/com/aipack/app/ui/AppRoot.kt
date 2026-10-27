package com.aipack.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aipack.app.R
import com.aipack.app.data.Catalog
import com.aipack.app.data.Tool

@Composable
fun AppRoot(catalog: Catalog) {
    val context = LocalContext.current
    var categoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var filters by remember { mutableStateOf(FilterState()) }

    BackHandler(enabled = showAbout || categoryId != null) {
        if (showAbout) showAbout = false else categoryId = null
    }

    val onOpenTool: (Tool) -> Unit = { tool -> openUrl(context, tool.url) }
    val category = catalog.categories.firstOrNull { it.id == categoryId }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when {
            catalog.tools.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.load_error), modifier = Modifier.padding(24.dp))
            }
            showAbout -> AboutScreen(catalog = catalog, onBack = { showAbout = false })
            category != null -> CategoryScreen(
                category = category,
                catalog = catalog,
                filters = filters,
                onFiltersChange = { filters = it },
                onBack = { categoryId = null },
                onOpenTool = onOpenTool,
            )
            else -> HomeScreen(
                catalog = catalog,
                query = query,
                onQueryChange = { query = it },
                filters = filters,
                onFiltersChange = { filters = it },
                onOpenCategory = { categoryId = it.id },
                onOpenAbout = { showAbout = true },
                onOpenTool = onOpenTool,
            )
        }
    }
}
