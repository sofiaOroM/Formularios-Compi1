package com.compi.formularios.render

import androidx.compose.ui.graphics.Color

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