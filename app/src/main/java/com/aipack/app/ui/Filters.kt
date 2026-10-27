package com.aipack.app.ui

import com.aipack.app.data.Catalog
import com.aipack.app.data.IranAccess
import com.aipack.app.data.PersianSupport
import com.aipack.app.data.Pricing
import com.aipack.app.data.Tool

/** Iran access as shown to the user: the tested value, or the estimate if untested. */
data class AccessInfo(val access: IranAccess, val estimated: Boolean)

fun Tool.accessInfo(): AccessInfo {
    if (iranAccess != IranAccess.Unverified) return AccessInfo(iranAccess, false)
    val guess = iranAccessGuess
    return if (guess != null && guess != IranAccess.Unverified) AccessInfo(guess, true)
    else AccessInfo(IranAccess.Unverified, false)
}

data class FilterState(
    val pricing: Set<Pricing> = emptySet(),
    val access: Set<IranAccess> = emptySet(),
    val persianOnly: Boolean = false,
) {
    val isActive: Boolean get() = pricing.isNotEmpty() || access.isNotEmpty() || persianOnly

    fun matches(tool: Tool): Boolean {
        if (pricing.isNotEmpty() && tool.pricing !in pricing) return false
        if (access.isNotEmpty() && tool.accessInfo().access !in access) return false
        if (persianOnly &&
            tool.persianSupport != PersianSupport.Yes &&
            tool.persianSupport != PersianSupport.Partial
        ) return false
        return true
    }
}

fun searchTools(catalog: Catalog, query: String, filters: FilterState): List<Tool> {
    val q = query.trim()
    return catalog.tools.filter { tool ->
        filters.matches(tool) && (q.isEmpty() || matchesQuery(catalog, tool, q))
    }
}

private fun matchesQuery(catalog: Catalog, tool: Tool, q: String): Boolean {
    if (tool.name.contains(q, ignoreCase = true)) return true
    if (tool.description.contains(q, ignoreCase = true)) return true
    if (tool.bestFor?.contains(q, ignoreCase = true) == true) return true
    return tool.categories.any { ref ->
        ref.subcategory.contains(q, ignoreCase = true) ||
            catalog.categories.any { it.id == ref.categoryId && it.title.contains(q, ignoreCase = true) }
    }
}

/** The colour accent for a tool card: its first category's colour. */
fun Tool.primaryCategoryId(): String = categories.first().categoryId
