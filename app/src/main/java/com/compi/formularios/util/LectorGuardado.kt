package com.compi.formularios.util

object LectorGuardado {

    fun traducirAParserOriginal(contenidoGuardado: String): String {
        val lineas = contenidoGuardado.lines()
        val builderCUP = StringBuilder()

        var nivelAnidamiento = 0 // 0 = Raíz (sin comas), >0 = Sección/Tabla (con comas)
        var esPrimerElementoEnBloque = true

        fun prepararNuevoElemento() {
            // SOLO se pone coma si estamos dentro de una sección/tabla y NO es el primer elemento
            if (nivelAnidamiento > 0 && !esPrimerElementoEnBloque) {
                builderCUP.append(",\n")
            }
            esPrimerElementoEnBloque = false
        }

        for (linea in lineas) {
            val trimLinea = linea.trim()

            // Ignoramos etiquetas de cierre estructurales del XML intermedio
            if (trimLinea.startsWith("###") || trimLinea.startsWith("Author:") ||
                trimLinea.startsWith("Description:") || trimLinea == "<content>" ||
                trimLinea == "</content>" || trimLinea == "<element>" || trimLinea == "</element>" ||
                trimLinea == "<style>" || trimLinea == "</style>" || trimLinea == "<line>" || trimLinea == "</line>") {
                continue
            }

            // TEXTO
            if (trimLinea.startsWith("<text=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "text")
                if (valores.size >= 3) {
                    prepararNuevoElemento()
                    // Filtramos el ancho y alto para que no asfixien a Compose
                    val width = limpiarNumeroDimension(valores[0], 350)
                    val height = limpiarNumeroDimension(valores[1], 50)
                    val content = limpiarComillasDobles(valores[2])

                    builderCUP.append("TEXT [\n")
                    builderCUP.append("    content: \"$content\"")
                    if (width > 0) builderCUP.append(",\n    width: $width")
                    if (height > 0) builderCUP.append(",\n    height: $height")
                    builderCUP.append("\n]")
                }
            }

            // PREGUNTA ABIERTA
            else if (trimLinea.startsWith("<open=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "open")
                if (valores.size >= 3) {
                    prepararNuevoElemento()
                    val label = limpiarComillasDobles(valores[2])

                    builderCUP.append("OPEN_QUESTION [\n")
                    builderCUP.append("    label: \"$label\"\n")
                    builderCUP.append("]")
                }
            }

            // SECCIÓN
            else if (trimLinea.startsWith("<section=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "section")
                if (valores.size >= 5) {
                    prepararNuevoElemento()
                    val width = limpiarNumeroDimension(valores[0], 380) // Secciones usan el ancho completo
                    val height = limpiarNumeroDimension(valores[1], 450) // Altura generosa para el scroll
                    val x = limpiarNumero(valores[2])
                    val y = limpiarNumero(valores[3])
                    val orientation = valores[4].trim()

                    builderCUP.append("SECTION [\n")
                    builderCUP.append("    width: $width,\n")
                    builderCUP.append("    height: $height,\n")
                    builderCUP.append("    pointX: $x,\n")
                    builderCUP.append("    pointY: $y,\n")
                    builderCUP.append("    orientation: $orientation,\n")
                    builderCUP.append("    elements: { [\n")

                    nivelAnidamiento++
                    esPrimerElementoEnBloque = true
                }
            }

            else if (trimLinea == "</section>") {
                builderCUP.append("\n    ] }\n]")
                nivelAnidamiento--
                esPrimerElementoEnBloque = false
            }

            // TABLA
            else if (trimLinea.startsWith("<table=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "table")
                if (valores.size >= 4) {
                    prepararNuevoElemento()
                    val width = limpiarNumeroDimension(valores[0], 360) // Ajuste a la pantalla
                    val height = limpiarNumeroDimension(valores[1], 300)
                    val x = limpiarNumero(valores[2])
                    val y = limpiarNumero(valores[3])

                    builderCUP.append("TABLE [\n")
                    builderCUP.append("    width: $width,\n")
                    builderCUP.append("    height: $height,\n")
                    builderCUP.append("    pointX: $x,\n")
                    builderCUP.append("    pointY: $y,\n")
                    builderCUP.append("    elements: { [\n")

                    nivelAnidamiento++
                    esPrimerElementoEnBloque = true
                }
            }

            else if (trimLinea == "</table>") {
                builderCUP.append("\n    ] }\n]")
                nivelAnidamiento--
                esPrimerElementoEnBloque = false
            }

            //  SELECT_QUESTION
            else if (trimLinea.startsWith("<select=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "select")
                if (valores.size >= 5) {
                    prepararNuevoElemento()
                    val label = limpiarComillasDobles(valores[2])
                    val options = limpiarOpciones(valores[3])
                    val correct = limpiarNumero(valores[4])

                    builderCUP.append("SELECT_QUESTION [\n")
                    builderCUP.append("    label: \"$label\",\n")
                    builderCUP.append("    options: { $options },\n")
                    builderCUP.append("    correct: $correct\n")
                    builderCUP.append("]")
                }
            }

            // DROP_QUESTION
            else if (trimLinea.startsWith("<drop=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "drop")
                if (valores.size >= 4) {
                    prepararNuevoElemento()
                    val label = limpiarComillasDobles(valores[2])
                    val options = limpiarOpciones(valores[3])

                    builderCUP.append("DROP_QUESTION [\n")
                    builderCUP.append("    label: \"$label\",\n")
                    builderCUP.append("    options: { $options }\n")
                    builderCUP.append("]")
                }
            }

            //MULTIPLE_QUESTION
            else if (trimLinea.startsWith("<multiple=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "multiple")
                if (valores.size >= 5) {
                    prepararNuevoElemento()
                    val label = limpiarComillasDobles(valores[2])
                    val options = limpiarOpciones(valores[3])
                    val correctList = limpiarArregloNumerico(valores[4])

                    builderCUP.append("MULTIPLE_QUESTION [\n")
                    builderCUP.append("    label: \"$label\",\n")
                    builderCUP.append("    options: { $options },\n")
                    builderCUP.append("    correct: { $correctList }\n")
                    builderCUP.append("]")
                }
            }
        }

        return builderCUP.toString()
    }

    private fun extraerValoresEntrePicos(linea: String, tag: String): List<String> {
        val pattern = "<$tag=([^>]+)>".toRegex()
        val match = pattern.find(linea) ?: return emptyList()
        val contenido = match.groups[1]?.value?.trim()?.trimEnd('/') ?: return emptyList()

        return separarPorComasGlobales(contenido)
    }

    private fun separarPorComasGlobales(texto: String): List<String> {
        val output = mutableListOf<String>()
        val buffer = StringBuilder()
        var scopeLlaves = 0
        var scopeCorchetes = 0
        var scopeComillas = false

        var i = 0
        while (i < texto.length) {
            val c = texto[i]

            if (c == '"' && i + 1 < texto.length && texto[i + 1] == '"') {
                buffer.append("\"\"")
                i += 2
                continue
            }

            if (c == '"') scopeComillas = !scopeComillas

            if (!scopeComillas) {
                if (c == '{') scopeLlaves++
                if (c == '}') scopeLlaves--
                if (c == '[') scopeCorchetes++
                if (c == ']') scopeCorchetes--
            }

            if (c == ',' && scopeLlaves == 0 && scopeCorchetes == 0 && !scopeComillas) {
                output.add(buffer.toString().trim())
                buffer.clear()
            } else {
                buffer.append(c)
            }
            i++
        }
        output.add(buffer.toString().trim())
        return output
    }

    // Nueva función de sanitización para dimensiones gráficas de Compose
    private fun limpiarNumeroDimension(s: String, valorMinimoSeguro: Int): Int {
        val numero = s.toDoubleOrNull()?.toInt() ?: 0
        return if (numero < 100) valorMinimoSeguro else numero
    }

    private fun limpiarNumero(s: String): Int {
        return s.toDoubleOrNull()?.toInt() ?: 0
    }

    private fun limpiarComillasDobles(s: String): String {
        return s.replace("\"\"", "\"").trim('"')
    }

    private fun limpiarOpciones(s: String): String {
        val sinLlaves = s.trim().removePrefix("{").removeSuffix("}")
        return sinLlaves.split(",").map { limpiarComillasDobles(it) }.joinToString(", ") { "\"$it\"" }
    }

    private fun limpiarArregloNumerico(s: String): String {
        val sinCorchetes = s.trim().removePrefix("[").removeSuffix("]")
        return sinCorchetes.split(",").map { limpiarNumero(it) }.joinToString(", ")
    }
}