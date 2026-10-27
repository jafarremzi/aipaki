package com.aipack.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aipack.app.R
import com.aipack.app.data.Catalog
import com.aipack.app.data.Category
import com.aipack.app.data.Tool

private val ScreenPadding = Modifier.padding(horizontal = 16.dp)

@Composable
fun HomeScreen(
    catalog: Catalog,
    query: String,
    onQueryChange: (String) -> Unit,
    filters: FilterState,
    onFiltersChange: (FilterState) -> Unit,
    onOpenCategory: (Category) -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTool: (Tool) -> Unit,
) {
    val searching = query.isNotBlank() || filters.isActive
    val results = remember(catalog, query, filters) { searchTools(catalog, query, filters) }
    val counts = remember(catalog) {
        catalog.categories.associate { c ->
            c.id to catalog.tools.count { t -> t.categories.any { it.categoryId == c.id } }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = ScreenPadding.statusBarsPadding().padding(top = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        stringResource(R.string.app_name),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Brand,
                    )
                    Text(stringResource(R.string.tagline), fontSize = 14.sp, color = InkSoft)
                }
                TextButton(onClick = onOpenAbout) { Text(stringResource(R.string.about)) }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = ScreenPadding.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        TextButton(onClick = { onQueryChange("") }) { Text("✕") }
                    }
                },
            )
        }
        item {
            Box(ScreenPadding) { FilterBar(filters, onFiltersChange) }
        }
        if (searching) {
            if (results.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_results),
                        color = InkSoft,
                        modifier = ScreenPadding.padding(vertical = 24.dp),
                    )
                }
            } else {
                items(results, key = { it.id }) { tool ->
                    Box(ScreenPadding) {
                        ToolCard(tool, styleFor(tool.primaryCategoryId()).accent, onOpenTool)
                    }
                }
            }
        } else {
            item {
                Text(
                    stringResource(R.string.categories_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkDark,
                    modifier = ScreenPadding,
                )
            }
            items(catalog.categories.chunked(2), key = { row -> row.first().id }) { row ->
                Row(
                    modifier = ScreenPadding.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    row.forEach { category ->
                        CategoryTile(
                            category = category,
                            count = counts[category.id] ?: 0,
                            onClick = { onOpenCategory(category) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        item { Spacer(Modifier.navigationBarsPadding().height(24.dp)) }
    }
}

@Composable
fun CategoryTile(category: Category, count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val style = styleFor(category.id)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.96f else 1f, label = "tile")
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(132.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(style.brush)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(category.emoji, fontSize = 34.sp)
            Column {
                Text(category.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = style.onColor)
                Text(
                    stringResource(R.string.tools_count, count),
                    fontSize = 12.sp,
                    color = style.onColor.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
fun CategoryScreen(
    category: Category,
    catalog: Catalog,
    filters: FilterState,
    onFiltersChange: (FilterState) -> Unit,
    onBack: () -> Unit,
    onOpenTool: (Tool) -> Unit,
) {
    val style = styleFor(category.id)
    val total = remember(catalog, category) {
        catalog.tools.count { t -> t.categories.any { it.categoryId == category.id } }
    }
    val sections = remember(catalog, category, filters) {
        category.subcategories
            .map { sub ->
                sub to catalog.tools.filter { t ->
                    filters.matches(t) &&
                        t.categories.any { it.categoryId == category.id && it.subcategory == sub }
                }
            }
            .filter { it.second.isNotEmpty() }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(style.brush)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            ) {
                TextButton(onClick = onBack) {
                    Text("← " + stringResource(R.string.back), color = style.onColor)
                }
                Row(
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(category.emoji, fontSize = 44.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(category.title, fontSize = 26.sp, fontWeight = FontWeight.Black, color = style.onColor)
                        Text(
                            stringResource(R.string.tools_count, total),
                            fontSize = 14.sp,
                            color = style.onColor.copy(alpha = 0.9f),
                        )
                    }
                }
            }
        }
        item { Box(ScreenPadding) { FilterBar(filters, onFiltersChange) } }
        if (sections.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.no_results),
                    color = InkSoft,
                    modifier = ScreenPadding.padding(vertical = 24.dp),
                )
            }
        }
        sections.forEach { (sub, tools) ->
            item(key = "header-$sub") {
                Text(
                    sub,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkDark,
                    modifier = ScreenPadding.padding(top = 8.dp),
                )
            }
            items(tools, key = { "$sub-${it.id}" }) { tool ->
                Box(ScreenPadding) { ToolCard(tool, style.accent, onOpenTool) }
            }
        }
        item { Spacer(Modifier.navigationBarsPadding().height(24.dp)) }
    }
}

@Composable
fun AboutScreen(catalog: Catalog, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack) { Text("← " + stringResource(R.string.back)) }
        Text(
            stringResource(R.string.about_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Brand,
        )
        Text(stringResource(R.string.about_intro), color = InkDark)
        if (catalog.updated.isNotBlank()) {
            Text(stringResource(R.string.about_updated, catalog.updated), color = InkSoft, fontSize = 13.sp)
        }
        AboutBlock(stringResource(R.string.about_access_title), stringResource(R.string.about_access_body))
        AboutBlock(stringResource(R.string.about_gateway_title), stringResource(R.string.about_gateway_body))
        AboutBlock(stringResource(R.string.about_outdated_title), stringResource(R.string.about_outdated_body))
    }
}

@Composable
private fun AboutBlock(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp),
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = InkDark)
        Text(body, color = InkSoft, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
    }
}
