package com.compi.formularios.parser

import android.content.ContentValues.TAG
import android.util.Log
import com.compi.formularios.modelos.*
import java.io.StringReader

object ParserBridge {

    fun parsearFormulario(codigo: String): List<Elemento> {
        Log.d(TAG, "Iniciando parsearFormulario. Código de entrada:\n$codigo")
        val lexer = Lexer(StringReader(codigo))
        val parser = Parser(lexer)

        return try {
            val resultado = parser.parse().value

            Log.d(TAG, "CUP terminó. Tipo de resultado: ${resultado?.javaClass?.simpleName}")
            Log.d(TAG, "Estructura cruda de CUP: $resultado")

            val erroresLexicos = lexer.lexicalErrors
            val erroresSintacticos = parser.syntaxErrors

            if (erroresLexicos.isNotEmpty() || erroresSintacticos.isNotEmpty()) {
                val reporte = (erroresLexicos + erroresSintacticos).joinToString("\n")
                throw Exception(reporte)
            }

            @Suppress("UNCHECKED_CAST")
            val lista = resultado as? List<Map<String, Any>>

            Log.d(TAG, "Iniciando conversión de ${lista?.size ?: 0} elementos principales...")
            val elementosMapeados = lista?.mapNotNull { convertir(it) } ?: emptyList()

            Log.d(TAG, "Salida final del puente: Se cargaron ${elementosMapeados.size} elementos.")
            Log.d(TAG, "Salida final del puente: Se cargaron ${elementosMapeados} elementos.")

            elementosMapeados

        } catch (e: Exception) {
            throw Exception(e.message ?: "Error desconocido")
        }
    }

    fun convertir(obj: Any?): Elemento? {
        val mapa = obj as? Map<*, *> ?: return null

        val type = mapa["type"] as? String ?: return null
        val attrs = (mapa["attrs"] as? Map<*, *>) ?: mapa

        return when (type) {
            "SECTION" -> convertirSeccion(attrs)
            "TEXT" -> convertirTexto(attrs)
            "TABLE" -> convertirTabla(attrs)
            "OPEN", "DROP", "SELECT", "MULTIPLE" -> convertirPregunta(type, attrs)
            else -> null
        }
    }

    private fun convertirSeccion(attrs: Map<*, *>): Seccion {
        val elementosRaw = attrs["ELEMENTS"] as? List<List<*>> // CUP envía lista de listas
        val elementos = elementosRaw?.flatten()?.mapNotNull { convertir(it) } ?: emptyList()

        return Seccion(
            elements = elementos,
            orientation = attrs["ORIENTATION"]?.toString() ?: "VERTICAL",
            width = getDouble(attrs["WIDTH"]) ?: 380.0,
            height = getDouble(attrs["HEIGHT"]) ?: 400.0,
            pointX = getDouble(attrs["POINTX"]) ?: 10.0,
            pointY = getDouble(attrs["POINTY"]) ?: 130.0,
            estilos = attrs["STYLES"] as? Map<String, Any>
        )
    }

    private fun convertirTexto(attrs: Map<*, *>): Texto {
        return Texto(
            content = attrs["CONTENT"]?.toString() ?: "",
            pointX = getDouble(attrs["POINTX"]) ?: 10.0,
            pointY = getDouble(attrs["POINTY"]) ?: 10.0,
            estilos = attrs["STYLES"] as? Map<String, Any>
        )
    }

    private fun convertirTabla(attrs: Map<*, *>): Tabla {
        val filasRaw = attrs["ELEMENTS"] as? List<*>

        val filasConvertidas = filasRaw?.mapNotNull { fila ->
            (fila as? List<*>)?.mapNotNull { celda ->
                convertir(celda)
            }
        } ?: emptyList()

        return Tabla(
            elements = filasConvertidas,
            width = getDouble(attrs["WIDTH"]) ?: 380.0,
            height = getDouble(attrs["HEIGHT"]) ?: 300.0,
            pointX = getDouble(attrs["POINTX"]) ?: 10.0,
            pointY = getDouble(attrs["POINTY"]) ?: 10.0,
            estilos = attrs["STYLES"] as? Map<String, Any>
        )
    }

    private fun convertirPregunta(type: String, attrs: Map<*, *>): Pregunta {
        val optionsRaw = attrs["OPTIONS"]
        val listaFinal = mutableListOf<String>()
        if (optionsRaw is Map<*, *>) {
            val inicio = getDouble(optionsRaw["start"])?.toInt() ?: 1
            val final = getDouble(optionsRaw["end"])?.toInt() ?: 10
            listaFinal.add("POKEMON_API:$inicio:$final")
        } else if (optionsRaw is List<*>) {
            listaFinal.addAll(optionsRaw.map { it.toString() })
        }
        return Pregunta(
            type = type,
            label = attrs["LABEL"]?.toString() ?: "",
            options = listaFinal,
            correct = attrs["CORRECT"],
            pointX = getDouble(attrs["POINTX"]) ?: 10.0,
            pointY = getDouble(attrs["POINTY"]) ?: 10.0,
            estilos = attrs["STYLES"] as? Map<String, Any>
        )
    }

    private fun getDouble(value: Any?): Double? {
        return when (value) {
            is Number -> value.toDouble()
            else -> null
        }
    }
}