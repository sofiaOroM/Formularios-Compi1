package com.compi.formularios.parser

import com.compi.formularios.modelos.*
import java.io.StringReader

object ParserBridge {

    fun parsearFormulario(codigo: String): List<Elemento> {
        val lexer = Lexer(StringReader(codigo))
        val parser = Parser(lexer)

        return try {
            val resultado = parser.parse().value

            val erroresLexicos = lexer.lexicalErrors
            val erroresSintacticos = parser.syntaxErrors

            if (erroresLexicos.isNotEmpty() || erroresSintacticos.isNotEmpty()) {
                val reporte = (erroresLexicos + erroresSintacticos).joinToString("\n")
                throw Exception(reporte)
            }

            @Suppress("UNCHECKED_CAST")
            val lista = resultado as? List<Map<String, Any>>
            lista?.mapNotNull { convertir(it) } ?: emptyList()

        } catch (e: Exception) {
            throw Exception(e.message ?: "Error desconocido")
        }
    }

    fun convertir(obj: Any?): Elemento? {
        if (obj !is Map<*, *>) return null

        // CUP genera: {type="SECTION", attrs={...}}
        val type = obj["type"] as? String ?: return null
        val attrs = obj["attrs"] as? Map<*, *> ?: emptyMap<Any, Any>()

        return when (type) {
            "SECTION" -> convertirSeccion(attrs)
            "TEXT" -> convertirTexto(attrs)
            "TABLE" -> convertirTabla(attrs)
            "OPEN", "DROP", "SELECT", "MULTIPLE" -> convertirPregunta(type, attrs)
            else -> null
        }
    }

    private fun convertirSeccion(attrs: Map<*, *>): Seccion {
        val elementosRaw = attrs["ELEMENTS"] as? List<*>
        val elementos = elementosRaw?.mapNotNull { convertir(it) } ?: emptyList()

        return Seccion(
            elements = elementos,
            orientation = attrs["ORIENTATION"]?.toString() ?: "VERTICAL",
            width = getDouble(attrs["WIDTH"]),
            height = getDouble(attrs["HEIGHT"]),
            pointX = getDouble(attrs["POINTX"]),
            pointY = getDouble(attrs["POINTY"]),
            estilos = attrs["STYLES"] as? Map<String, Any>
        )
    }

    private fun convertirTexto(attrs: Map<*, *>): Texto {
        return Texto(
            content = attrs["CONTENT"]?.toString() ?: "",
            pointX = getDouble(attrs["POINTX"]),
            pointY = getDouble(attrs["POINTY"]),
            estilos = attrs["STYLES"] as? Map<String, Any>
        )
    }

    private fun convertirTabla(attrs: Map<*, *>): Tabla {
        val elementosRaw = attrs["ELEMENTS"] as? List<*>
        val elementos = elementosRaw?.mapNotNull { convertir(it) } ?: emptyList()

        return Tabla(
            elements = elementos,
            width = getDouble(attrs["WIDTH"]),
            height = getDouble(attrs["HEIGHT"]),
            pointX = getDouble(attrs["POINTX"]),
            pointY = getDouble(attrs["POINTY"]),
            estilos = attrs["STYLES"] as? Map<String, Any>
        )
    }

    private fun convertirPregunta(type: String, attrs: Map<*, *>): Pregunta {
        return Pregunta(
            type = type,
            label = attrs["LABEL"]?.toString() ?: "",
            options = if (attrs["OPTIONS"] is List<*>) attrs["OPTIONS"] as List<String> else emptyList(),
            correct = attrs["CORRECT"],
            pointX = getDouble(attrs["POINTX"]),
            pointY = getDouble(attrs["POINTY"]),
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