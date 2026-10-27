@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.aipack.app.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aipack.app.R
import com.aipack.app.data.IranAccess
import com.aipack.app.data.PersianSupport
import com.aipack.app.data.Pricing
import com.aipack.app.data.Tool

fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, R.string.no_browser, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun TagChip(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

private fun accessWord(a: IranAccess): String = when (a) {
    IranAccess.Open -> "Open"
    IranAccess.VPN -> "VPN"
    IranAccess.Varies -> "Varies"
    IranAccess.Unverified -> "untested"
}

@Composable
fun ToolCard(tool: Tool, accent: Color, onOpen: (Tool) -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.97f else 1f, label = "press")
    val info = tool.accessInfo()
    val shownNote = tool.note
        ?.replace("Details not yet researched.", "")
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    Card(
        onClick = { onOpen(tool) },
        interactionSource = interaction,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(accent),
            )
            Column(Modifier.weight(1f).padding(14.dp)) {
                Text(tool.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = InkDark)
                Text(tool.description, fontSize = 14.sp, color = InkSoft, modifier = Modifier.padding(top = 2.dp))
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TagChip(tool.pricing.name, pricingColor(tool.pricing))
                    TagChip(
                        "Iran: " + accessWord(info.access) + if (info.estimated) " (est.)" else "",
                        accessColor(info.access),
                    )
                    if (tool.persianSupport == PersianSupport.Yes) TagChip("Persian", Color(0xFF15803D))
                    if (tool.persianSupport == PersianSupport.Partial) TagChip("Persian (partial)", Color(0xFF15803D))
                    tool.type?.let { TagChip(it, Color(0xFF6B21A8)) }
                    if (tool.payment == "Rial") TagChip("Pay in Rial", Color(0xFF15803D))
                    if (tool.commercialUseOnFree == false) TagChip("Free plan: non-commercial", Color(0xFF6B7280))
                    if (tool.watermarkOnFree == true) TagChip("Watermark on free", Color(0xFF6B7280))
                    tool.bestFor?.let { TagChip(it, Brand) }
                }
                if (shownNote != null) {
                    Text(
                        shownNote,
                        fontSize = 12.sp,
                        color = InkSoft,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

private fun <T> Set<T>.toggle(item: T): Set<T> = if (item in this) this - item else this + item

@Composable
fun FilterBar(filters: FilterState, onChange: (FilterState) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Pricing.values().forEach { p ->
            val label = when (p) {
                Pricing.Free -> stringResource(R.string.filter_free)
                Pricing.Freemium -> stringResource(R.string.filter_freemium)
                Pricing.Paid -> stringResource(R.string.filter_paid)
            }
            FilterChip(
                selected = p in filters.pricing,
                onClick = { onChange(filters.copy(pricing = filters.pricing.toggle(p))) },
                label = { Text(label) },
            )
        }
        listOf(IranAccess.Open, IranAccess.VPN, IranAccess.Varies).forEach { a ->
            val label = when (a) {
                IranAccess.Open -> stringResource(R.string.filter_open)
                IranAccess.VPN -> stringResource(R.string.filter_vpn)
                else -> stringResource(R.string.filter_varies)
            }
            FilterChip(
                selected = a in filters.access,
                onClick = { onChange(filters.copy(access = filters.access.toggle(a))) },
                label = { Text(label) },
            )
        }
        FilterChip(
            selected = filters.persianOnly,
            onClick = { onChange(filters.copy(persianOnly = !filters.persianOnly)) },
            label = { Text(stringResource(R.string.filter_persian)) },
        )
    }
}
