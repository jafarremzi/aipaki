package com.aipack.app.data

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogParserTest {

    private fun realCatalog(): Catalog {
        val file = File("src/main/assets/tools.json")
        assertTrue("tools.json not found at ${file.absolutePath}", file.exists())
        return CatalogParser.parse(file.readText(Charsets.UTF_8))
    }

    @Test
    fun brokenJsonGivesEmptyCatalog() {
        assertTrue(CatalogParser.parse("{not json").tools.isEmpty())
        assertTrue(CatalogParser.parse("").tools.isEmpty())
    }

    @Test
    fun brokenToolEntriesAreSkipped() {
        val json = """
            {"updated":"2026-01-01","categories":[],"tools":[
              {"id":"ok","name":"Ok","description":"d","url":"https://ok.example",
               "pricing":"Free","iran_access":"Open",
               "categories":[{"category":"chat","sub":"General Assistants"}]},
              {"id":"no-url","name":"X","description":"d","pricing":"Free","iran_access":"Open",
               "categories":[{"category":"chat","sub":"General Assistants"}]},
              {"id":"bad-pricing","name":"Y","description":"d","url":"https://y.example",
               "pricing":"Cheap","iran_access":"Open",
               "categories":[{"category":"chat","sub":"General Assistants"}]},
              {"id":"no-cats","name":"Z","description":"d","url":"https://z.example",
               "pricing":"Free","iran_access":"Open","categories":[]}
            ]}
        """.trimIndent()
        val catalog = CatalogParser.parse(json)
        assertEquals(listOf("ok"), catalog.tools.map { it.id })
    }

    @Test
    fun realFileHasToolsAndUniqueIds() {
        val catalog = realCatalog()
        assertTrue("expected many tools", catalog.tools.size >= 50)
        assertEquals(catalog.tools.size, catalog.tools.map { it.id }.toSet().size)
    }

    @Test
    fun everyToolPointsToAnExistingCategoryAndSubcategory() {
        val catalog = realCatalog()
        assertTrue(catalog.categories.isNotEmpty())
        for (tool in catalog.tools) {
            assertTrue("${tool.id}: url must be https", tool.url.startsWith("https://"))
            for (ref in tool.categories) {
                val category = catalog.categories.firstOrNull { it.id == ref.categoryId }
                assertTrue("${tool.id}: unknown category ${ref.categoryId}", category != null)
                assertTrue(
                    "${tool.id}: unknown subcategory ${ref.subcategory}",
                    category!!.subcategories.contains(ref.subcategory),
                )
            }
        }
    }

    @Test
    fun everyCategoryHasAtLeastOneTool() {
        val catalog = realCatalog()
        for (category in catalog.categories) {
            assertTrue(
                "category ${category.id} is empty",
                catalog.tools.any { t -> t.categories.any { it.categoryId == category.id } },
            )
        }
    }
}
