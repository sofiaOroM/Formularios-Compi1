package com.compi.formularios.render

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

private fun obtenerValorLimpio(mapa: Map<String, Any>?, llaveBuscada: String): Any? {
    if (mapa == null) return null
    for ((llave, valor) in mapa) {
        val llaveLimpia = llave.replace("\"", "").trim().uppercase()
        if (llaveLimpia == llaveBuscada.uppercase()) {
            return valor
        }
    }
    return null
}

fun obtenerColor(mapa: Map<String, Any>?, key: String, default: Color = Color.Unspecified): Color {
    val crudo = obtenerValorLimpio(mapa, key)?.toString()?.replace("\"", "")?.trim() ?: return default
    if (crudo.isEmpty()) return default

    return try {
        when {
            crudo.startsWith("#") -> Color(android.graphics.Color.parseColor(crudo))
            crudo.startsWith("(") && crudo.endsWith(")") -> {
                val rgb = crudo.removePrefix("(").removeSuffix(")").split(",").map { it.trim().toInt() }
                if (rgb.size == 3) Color(rgb[0], rgb[1], rgb[2]) else default
            }
            else -> when (crudo.uppercase()) {
                "RED" -> Color.Red
                "BLUE" -> Color.Blue
                "GREEN" -> Color.Green
                "BLACK" -> Color.Black
                "WHITE" -> Color.White
                "PURPLE" -> Color.Magenta
                "SKY" -> Color(128,0,128)
                "YELLOW" -> Color.Yellow
                "TRANSPARENT" -> Color.Transparent
                else -> default
            }
        }
    } catch (e: Exception) {
        default
    }
}

fun obtenerFuente(mapa: Map<String, Any>?): FontFamily {
    val crudo = (obtenerValorLimpio(mapa, "font family") ?: obtenerValorLimpio(mapa, "font_family"))
        ?.toString()?.replace("\"", "")?.trim()?.uppercase() ?: ""

    return when (crudo) {
        "MONO", "MONOSPACE" -> FontFamily.Monospace
        "SANS_SERIF", "SANS" -> FontFamily.SansSerif
        "SERIF" -> FontFamily.Serif
        else -> FontFamily.Default
    }
}

fun obtenerTamanioLetra(mapa: Map<String, Any>?, default: Int = 16): TextUnit {
    val crudo = (obtenerValorLimpio(mapa, "text size") ?: obtenerValorLimpio(mapa, "text_size"))
        ?.toString()?.replace("\"", "")?.trim() ?: return default.sp

    val num = crudo.toFloatOrNull() ?: return default.sp
    return num.sp
}

/*fun obtenerColor(valor: Any?): Color {
    val colorStr = valor?.toString()?.replace("\"", "")?.trim()?.uppercase() ?: return Color.Black
    return try {
        if (colorStr.startsWith("#")) {
            Color(android.graphics.Color.parseColor(colorStr))
        } else {
            when (colorStr) {
                "BLUE" -> Color.Blue
                "RED" -> Color.Red
                "GREEN" -> Color.Green
                "YELLOW" -> Color.Yellow
                "WHITE" -> Color.White
                "BLACK" -> Color.Black
                else -> Color.Black
            }
        }
    } catch (e: Exception) {
        Color.Black
    }
}*/

object PokeApiHelper {
    /**
     * Obtiene una lista de nombres de pokémones en un rango específico.
     */
    suspend fun obtenerRangoPokemones(inicio: Int, fin: Int): List<String> = withContext(Dispatchers.IO) {
        (inicio..fin).map { id ->
            try {
                // REQUEST a la PokeAPI
                val respuesta = URL("https://pokeapi.co/api/v2/pokemon/$id").readText()
                val nombre = JSONObject(respuesta).getString("name")

                nombre.replaceFirstChar { it.uppercase() }
            } catch (e: Exception) {
                "Pokémon #$id" // Si falla el internet, se devuelve el ID
            }
        }
    }
}