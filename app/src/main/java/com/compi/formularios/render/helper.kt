package com.compi.formularios.render

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

fun obtenerColor(valor: Any?): Color {
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
}

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