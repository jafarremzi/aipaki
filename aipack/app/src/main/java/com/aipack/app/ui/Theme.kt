package com.aipack.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.aipack.app.data.IranAccess
import com.aipack.app.data.Pricing

val Brand = Color(0xFF6C47FF)
val InkDark = Color(0xFF14142B)
val InkSoft = Color(0xFF5A5A72)

/** Each category has a colour "role": a gradient and a readable text colour. */
data class CategoryStyle(val start: Color, val end: Color, val onColor: Color) {
    val brush: Brush get() = Brush.linearGradient(listOf(start, end))
    /** Solid accent used for the strip on tool cards. */
    val accent: Color get() = start
}

fun styleFor(categoryId: String): CategoryStyle = when (categoryId) {
    "chat" -> CategoryStyle(Color(0xFF2563EB), Color(0xFF60A5FA), Color.White)
    "image" -> CategoryStyle(Color(0xFFC026D3), Color(0xFFEC4899), Color.White)
    "video" -> CategoryStyle(Color(0xFFDC2626), Color(0xFFF97316), Color.White)
    "audio" -> CategoryStyle(Color(0xFFFACC15), Color(0xFFFDE68A), Color(0xFF2B2000))
    "code" -> CategoryStyle(Color(0xFF16A34A), Color(0xFF4ADE80), Color.White)
    "productivity" -> CategoryStyle(Color(0xFF0D9488), Color(0xFF2DD4BF), Color.White)
    "research" -> CategoryStyle(Color(0xFF4F46E5), Color(0xFF818CF8), Color.White)
    "design" -> CategoryStyle(Color(0xFF0E7490), Color(0xFF22D3EE), Color.White)
    else -> CategoryStyle(Color(0xFF6B7280), Color(0xFF9CA3AF), Color.White)
}

// Badge colours are fixed and meaningful, and never reused for categories.
fun pricingColor(p: Pricing): Color = when (p) {
    Pricing.Free -> Color(0xFF15803D)
    Pricing.Freemium -> Color(0xFF1D4ED8)
    Pricing.Paid -> Color(0xFFC2410C)
}

fun accessColor(a: IranAccess): Color = when (a) {
    IranAccess.Open -> Color(0xFF15803D)
    IranAccess.VPN -> Color(0xFFB91C1C)
    IranAccess.Varies -> Color(0xFF92400E)
    IranAccess.Unverified -> Color(0xFF6B7280)
}

@Composable
fun AIPackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Brand,
            background = Color(0xFFF7F7FB),
            surface = Color.White,
            onBackground = InkDark,
            onSurface = InkDark,
        ),
        content = content,
    )
}
