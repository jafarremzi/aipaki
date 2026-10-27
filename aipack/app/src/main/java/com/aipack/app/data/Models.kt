package com.aipack.app.data

enum class Pricing { Free, Freemium, Paid }

enum class IranAccess { Open, VPN, Varies, Unverified }

enum class PersianSupport { Yes, Partial, No, Unverified }

data class CategoryRef(val categoryId: String, val subcategory: String)

data class Category(
    val id: String,
    val title: String,
    val emoji: String,
    val subcategories: List<String>,
)

data class Tool(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val categories: List<CategoryRef>,
    val pricing: Pricing,
    val iranAccess: IranAccess,
    val iranAccessGuess: IranAccess?,
    val bestFor: String?,
    val freeTierType: String?,
    val watermarkOnFree: Boolean?,
    val commercialUseOnFree: Boolean?,
    val persianSupport: PersianSupport,
    val type: String?,
    val signup: String?,
    val payment: String?,
    val lastChecked: String?,
    val verified: Boolean,
    val note: String?,
)

data class Catalog(
    val updated: String,
    val categories: List<Category>,
    val tools: List<Tool>,
) {
    companion object {
        val EMPTY = Catalog(updated = "", categories = emptyList(), tools = emptyList())
    }
}
