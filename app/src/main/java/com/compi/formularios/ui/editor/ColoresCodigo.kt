package com.compi.formularios.ui.editor

import androidx.compose.ui.graphics.Color

object ColoresCodigo {
    val emoji = Color(0xFFFFEB3B)        // amarillo
    val operador = Color(0xFF4CAF50)     // verde
    val variable = Color.Companion.White
    val string = Color(0xFFFF9800)       // naranja
    val numero = Color(0xFF4FC3F7)       // celeste
    val reservada = Color(0xFF9C27B0)    // morado
    val simbolo = Color(0xFF2196F3)      // azul
    val normal = Color.Companion.White
}

val palabrasReservadas = setOf(
    "IF",
    "ELSE",
    "WHILE",
    "DO",
    "FOR",
)