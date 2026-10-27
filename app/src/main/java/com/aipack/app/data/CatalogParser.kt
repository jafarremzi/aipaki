package com.aipack.app.data

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Parses tools.json. Tolerant by design: a broken file gives an empty catalog,
 * and a broken tool entry is skipped instead of crashing the app.
 */
object CatalogParser {

    fun parse(json: String): Catalog {
        return try {
            val root = JSONObject(json)
            Catalog(
                updated = root.optString("updated", ""),
                categories = parseCategories(root.optJSONArray("categories")),
                tools = parseTools(root.optJSONArray("tools")),
            )
        } catch (e: JSONException) {
            Catalog.EMPTY
        }
    }

    private fun parseCategories(array: JSONArray?): List<Category> {
        if (array == null) return emptyList()
        val result = ArrayList<Category>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val id = obj.optStringOrNull("id") ?: continue
            val title = obj.optStringOrNull("title") ?: continue
            val subs = ArrayList<String>()
            val subArray = obj.optJSONArray("subcategories")
            if (subArray != null) {
                for (j in 0 until subArray.length()) {
                    val s = subArray.optString(j, "")
                    if (s.isNotBlank()) subs.add(s)
                }
            }
            result.add(Category(id, title, obj.optString("emoji", ""), subs))
        }
        return result
    }

    private fun parseTools(array: JSONArray?): List<Tool> {
        if (array == null) return emptyList()
        val result = ArrayList<Tool>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val tool = parseTool(obj)
            if (tool != null) result.add(tool)
        }
        return result
    }

    private fun parseTool(obj: JSONObject): Tool? {
        val id = obj.optStringOrNull("id") ?: return null
        val name = obj.optStringOrNull("name") ?: return null
        val description = obj.optStringOrNull("description") ?: return null
        val url = obj.optStringOrNull("url") ?: return null
        if (!url.startsWith("https://") && !url.startsWith("http://")) return null
        val pricing = enumOrNull<Pricing>(obj.optStringOrNull("pricing")) ?: return null
        val iranAccess = enumOrNull<IranAccess>(obj.optStringOrNull("iran_access")) ?: return null

        val refs = ArrayList<CategoryRef>()
        val refArray = obj.optJSONArray("categories")
        if (refArray != null) {
            for (j in 0 until refArray.length()) {
                val r = refArray.optJSONObject(j) ?: continue
                val c = r.optStringOrNull("category") ?: continue
                val s = r.optStringOrNull("sub") ?: continue
                refs.add(CategoryRef(c, s))
            }
        }
        if (refs.isEmpty()) return null

        return Tool(
            id = id,
            name = name,
            description = description,
            url = url,
            categories = refs,
            pricing = pricing,
            iranAccess = iranAccess,
            iranAccessGuess = enumOrNull<IranAccess>(obj.optStringOrNull("iran_access_guess")),
            bestFor = obj.optStringOrNull("best_for"),
            freeTierType = obj.optStringOrNull("free_tier_type"),
            watermarkOnFree = obj.optBooleanOrNull("watermark_on_free"),
            commercialUseOnFree = obj.optBooleanOrNull("commercial_use_on_free"),
            persianSupport = enumOrNull<PersianSupport>(obj.optStringOrNull("persian_support"))
                ?: PersianSupport.Unverified,
            type = obj.optStringOrNull("type"),
            signup = obj.optStringOrNull("signup"),
            payment = obj.optStringOrNull("payment"),
            lastChecked = obj.optStringOrNull("last_checked"),
            verified = obj.optBoolean("verified", false),
            note = obj.optStringOrNull("note"),
        )
    }

    private inline fun <reified T : Enum<T>> enumOrNull(value: String?): T? {
        if (value == null) return null
        return enumValues<T>().firstOrNull { it.name.equals(value, ignoreCase = true) }
    }

    private fun JSONObject.optStringOrNull(key: String): String? {
        if (!has(key) || isNull(key)) return null
        val value = optString(key, "")
        return if (value.isBlank()) null else value
    }

    private fun JSONObject.optBooleanOrNull(key: String): Boolean? {
        if (!has(key) || isNull(key)) return null
        return optBoolean(key)
    }
}
