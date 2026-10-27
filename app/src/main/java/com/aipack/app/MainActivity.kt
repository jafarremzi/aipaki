package com.aipack.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aipack.app.data.Catalog
import com.aipack.app.data.CatalogParser
import com.aipack.app.ui.AIPackTheme
import com.aipack.app.ui.AppRoot
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val catalog = loadCatalog()
        setContent {
            AIPackTheme {
                AppRoot(catalog)
            }
        }
    }

    private fun loadCatalog(): Catalog = try {
        assets.open("tools.json").bufferedReader().use { CatalogParser.parse(it.readText()) }
    } catch (e: IOException) {
        Catalog.EMPTY
    }
}
