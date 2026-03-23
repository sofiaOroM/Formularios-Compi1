package com.compi.formularios.parser.guardado

import android.util.Log
import com.compi.formularios.modelos.Elemento
import com.compi.formularios.parserGuardado.LexerGuardado
import com.compi.formularios.parserGuardado.ParserGuardado
import java.io.StringReader

object ParserBridgeGuardado {

    private const val TAG = "PARSER_GUARDADO"

    fun parsearArchivoGuardado(contenidoPkm: String): List<Elemento> {
        val contenidoLimpio = contenidoPkm.lines()
            .map { it.trim() }
            .filter { line ->
                line.isNotEmpty() &&
                        !line.startsWith("###") &&
                        !line.startsWith("Author:") &&
                        !line.startsWith("Description:")
            }
            .joinToString("\n")
            .replace("\"\"", "\"")
            .replace("font_family", "font-family")
            .replace("background_color", "background-color")
            .replace("text_size", "text-size")
            .trim()

        Log.d(TAG, "Contenido final enviado a CUP:\n$contenidoLimpio")

        val lexer = LexerGuardado(StringReader(contenidoLimpio))
        val parser = ParserGuardado(lexer)

        return try {
            val resultado = parser.parse().value

            val erroresLexicos = lexer.lexicalErrors
            val erroresSintacticos = parser.syntaxErrors

            if (erroresLexicos.isNotEmpty() || erroresSintacticos.isNotEmpty()) {
                val reporte = (erroresLexicos + erroresSintacticos).joinToString("\n")
                throw Exception(reporte)
            }

            @Suppress("UNCHECKED_CAST")
            val lista = resultado as? List<Map<String, Object>> ?: emptyList()

            Log.d(TAG, "Total de elementos detectados por CUP: ${lista.size}")

            lista.forEachIndexed { index, elemento ->
                val tipo = elemento["type"] ?: "DESCONOCIDO"
                val atributos = elemento["attrs"] as? Map<String, Any> ?: emptyMap()

                Log.d(TAG, "--------------------------------------------------")
                Log.d(TAG, "Elemento [$index] - Tipo: $tipo")

                // Imprime cada atributo interno (POINTX, LABEL, WIDTH, etc.)
                atributos.forEach { (clave, valor) ->
                    Log.d(TAG, "   🔹 $clave: $valor")
                }
            }
            Log.d(TAG, "--------------------------------------------------")

            lista

        } catch (e: Exception) {
            Log.e(TAG, "Fallo leyendo .pkm: ${e.message}", e)
            throw Exception(e.message ?: "Error desconocido al leer archivo")
        } as List<Elemento>
    }
}