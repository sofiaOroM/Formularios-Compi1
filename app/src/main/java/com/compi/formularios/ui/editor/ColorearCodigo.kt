package com.compi.formularios.ui.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle

fun colorearCodigo(texto: String): AnnotatedString {
    val builder = AnnotatedString.Builder()

    val pattern = Regex("(@\\[[^\\]]*\\])|(\"[^\"]*\")|(\\d+)|([a-zA-Z_][a-zA-Z0-9_]*)|([{}()\\[\\]+\\-*/=])|(\\s+)|(.)")

    pattern.findAll(texto).forEach { result ->
        val token = result.value

        val color = when {

            token.startsWith("@") && token.endsWith("]") -> ColoresCodigo.emoji // Amarillo

            // Strings (comillas)
            token.startsWith("\"") -> ColoresCodigo.string

            // Palabras reservadas
            palabrasReservadas.contains(token) -> ColoresCodigo.reservada

            // Números
            token.matches(Regex("[0-9]+")) -> ColoresCodigo.numero

            // Símbolos de agrupación (Solo si no son parte del emoji)
            token in listOf("{", "}", "[", "]", "(", ")") -> ColoresCodigo.simbolo

            // Operadores
            token in listOf("+", "-", "*", "/", "=") -> ColoresCodigo.operador

            else -> ColoresCodigo.variable
        }

        builder.withStyle(style = SpanStyle(color = color)) {
            append(token)
        }
    }
    return builder.toAnnotatedString()
}
/*fun colorearCodigo(texto: String): AnnotatedString {

    val builder = AnnotatedString.Builder()

    val tokens = texto.split(" ")

    tokens.forEach { token ->

        val color = when {

            palabrasReservadas.contains(token) ->
                ColoresCodigo.reservada

            token.matches(Regex("[0-9]+")) ->
                ColoresCodigo.numero

            token.startsWith("\"") && token.endsWith("\"") ->
                ColoresCodigo.string

            token in listOf("{", "}", "[", "]", "(", ")") ->
                ColoresCodigo.simbolo

            token in listOf("+", "-", "*", "/", "=") ->
                ColoresCodigo.operador

            else ->
                ColoresCodigo.variable
        }

        builder.withStyle(style = SpanStyle(color = color)) {
            append("$token ")
        }

    }

    return builder.toAnnotatedString()
}*/