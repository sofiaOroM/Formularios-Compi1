package com.compi.formularios.util

import com.compi.formularios.modelos.*
import com.compi.formularios.render.ParserEmojis
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SerializarForm {

    fun serialize(elementos: List<Elemento>, autor: String, descripcion: String): String {
        val sb = StringBuilder()
        sb.append("###\n    Author: $autor\n    Description: $descripcion\n###\n\n")

        elementos.forEach { sb.append(serializarElemento(it)) }
        return sb.toString()
    }

    private fun serializarElemento(e: Elemento): String {
        return when (e) {
            is Seccion -> serializarSeccion(e)
            is Tabla -> serializarTabla(e)
            is Texto -> serializarTexto(e)
            is Pregunta -> serializarPregunta(e)
            else -> ""
        }
    }

    private fun serializarTexto(t: Texto): String {
        val content = ParserEmojis.codificar(t.content)
        val attrs = t.estilos ?: emptyMap()

        // leer de la propiedad de Kotlin, si no existe, busca en el mapa de CUP
        val X = resolverDimensionReal(buscarValorCrudo(attrs, "POINTX"))
        val Y = resolverDimensionReal(buscarValorCrudo(attrs, "POINTY"))

        val sb = StringBuilder()
        sb.append("<text=$X,$Y,\"$content\">\n")
        sb.append(serializarEstilosDesdeAttrs(attrs))
        sb.append("</text>\n")
        return sb.toString()
    }

    private fun serializarSeccion(s: Seccion): String {
        val attrs = s.estilos ?: emptyMap()

        val w = s.width ?: resolverDimensionReal(buscarValorCrudo(attrs, "WIDTH"))
        val h = s.height ?: resolverDimensionReal(buscarValorCrudo(attrs, "HEIGHT"))
        val x = s.pointX ?: resolverDimensionReal(buscarValorCrudo(attrs, "POINTX"))
        val y = s.pointY ?: resolverDimensionReal(buscarValorCrudo(attrs, "POINTY"))
        val orientation = s.orientation ?: buscarValorCrudo(attrs, "ORIENTATION")?.toString() ?: "VERTICAL"

        val sb = StringBuilder()
        sb.append("<section=$w,$h,$x,$y,$orientation>\n")
        sb.append(serializarEstilosDesdeAttrs(attrs))
        sb.append("  <content>\n")

        s.elements.forEach { sb.append(serializarElemento(it).prependIndent("    ")) }

        sb.append("  </content>\n")
        sb.append("</section>\n")
        return sb.toString()
    }

    private fun serializarTabla(t: Tabla): String {
        val attrs = t.estilos ?: emptyMap()

        val w = t.width ?: resolverDimensionReal(buscarValorCrudo(attrs, "WIDTH"))
        val h = t.height ?: resolverDimensionReal(buscarValorCrudo(attrs, "HEIGHT"))
        val x = t.pointX ?: resolverDimensionReal(buscarValorCrudo(attrs, "POINTX"))
        val y = t.pointY ?: resolverDimensionReal(buscarValorCrudo(attrs, "POINTY"))

        val sb = StringBuilder()
        sb.append("<table=$w,$h,$x,$y>\n")
        sb.append(serializarEstilosDesdeAttrs(attrs))
        sb.append("  <content>\n")

        t.elements.forEach { fila ->
            sb.append("    <line>\n")
            fila.forEach { celda ->
                sb.append("      <element>\n")
                sb.append(serializarElemento(celda).prependIndent("        "))
                sb.append("      </element>\n")
            }
            sb.append("    </line>\n")
        }
        sb.append("  </content>\n")
        sb.append("</table>\n")
        return sb.toString()
    }

    private fun serializarPregunta(p: Pregunta): String {
        val attrs = p.estilos ?: emptyMap()
        val label = ParserEmojis.codificar(p.label)

        val w = resolverDimensionReal(buscarValorCrudo(attrs, "WIDTH"))
        val h = resolverDimensionReal(buscarValorCrudo(attrs, "HEIGHT"))
        val tag = p.type.lowercase()

        val opcionesPart = if (p.type.uppercase() != "OPEN") {
            ",{" + p.options.joinToString(",") { "\"$it\"" } + "},${p.correct ?: -1}"
        } else ""

        val sb = StringBuilder()
        sb.append("<$tag=\"$label\"$opcionesPart>\n")
        sb.append(serializarEstilosDesdeAttrs(attrs))
        sb.append("</$tag>\n")
        return sb.toString()
    }

    @Suppress("UNCHECKED_CAST")
    private fun serializarEstilosDesdeAttrs(attrs: Map<String, Any>): String {
        if (attrs.isEmpty()) return ""

        val sb = StringBuilder()

        // Usamos el buscarValorCrudo que ya limpia las comillas de las llaves
        val color = buscarValorCrudo(attrs, "color")
        val bgColor = buscarValorCrudo(attrs, "background color")
        val fontFamily = buscarValorCrudo(attrs, "font family")
        val textSize = buscarValorCrudo(attrs, "text size")

        if (color != null || bgColor != null || fontFamily != null || textSize != null) {
            sb.append("  <style>\n")
            color?.let { sb.append("    <color=\"$it\"/>\n") }
            bgColor?.let { sb.append("    <background_color=\"$it\"/>\n") }
            fontFamily?.let { sb.append("    <font_family=\"$it\"/>\n") }

            val size = resolverDimensionReal(textSize)
            if (size > 0) {
                sb.append("    <text_size=\"$size\"/>\n")
            }
            sb.append("  </style>\n")
        }

        return sb.toString()
    }

    private fun buscarValorCrudo(mapa: Map<String, Any>, llaveBuscada: String): Any? {
        for ((key, value) in mapa) {
            // Quitamos las comillas físicas de la llave antes de comparar
            val llaveLimpia = key.toString()
                .replace("\"", "")
                .trim()

            if (llaveLimpia.equals(llaveBuscada.trim(), ignoreCase = true)) {
                return value
            }
        }
        return null
    }

    private fun resolverDimensionReal(valor: Any?): Double {
        if (valor == null) return 0.0
        val strValor = valor.toString().trim()

        return when (strValor) {
            "anchoStd" -> 420.0
            "altoSec" -> 400.0
            "altoTabla" -> 180.0
            else -> strValor.toDoubleOrNull() ?: 0.0
        }
    }

    fun obtenerFechaActual(): String {

        return SimpleDateFormat(

            "dd/MM/yy",

            Locale.getDefault()

        ).format(Date())

    }

    fun obtenerHoraActual(): String {

        return SimpleDateFormat(

            "HH:mm",

            Locale.getDefault()

        ).format(Date())

    }
}