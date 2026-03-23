package com.compi.formularios.util

object LectorGuardado {

    fun traducirAParserOriginal(contenidoGuardado: String): String {
        val lineas = contenidoGuardado.lines()
        val builderCUP = StringBuilder()

        var nivelAnidamiento = 0
        var esPrimerElementoEnBloque = true
        val pilaEstructuras = java.util.Stack<String>()

        fun prepararNuevoElemento() {
            if (nivelAnidamiento > 0 && !esPrimerElementoEnBloque) {
                builderCUP.append(",\n")
            }
            esPrimerElementoEnBloque = false
        }

        for (linea in lineas) {
            val trimLinea = linea.trim()

            if (trimLinea.startsWith("###") || trimLinea.startsWith("Author:") ||
                trimLinea.startsWith("Description:") || trimLinea == "<content>" ||
                trimLinea == "</content>" || trimLinea == "<element>" || trimLinea == "</element>" ||
                trimLinea == "<style>" || trimLinea == "</style>" ) {
                continue
            }

            // TEXTO
            if (trimLinea.startsWith("<text=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "text")
                if (valores.isNotEmpty()) {
                    prepararNuevoElemento()

                    val esSinDimensiones = valores[0].startsWith("\"")
                    val content = if (esSinDimensiones) limpiarComillasDobles(valores[0]) else limpiarComillasDobles(valores[2])

                    builderCUP.append("TEXT [\n")
                    builderCUP.append("    content: \"$content\"")

                    // Se fuerzan dimensiones estándar en celdas de texto para que no midan 0 dp
                    val x = limpiarNumeroPosicion(valores[0])
                    val y = limpiarNumeroPosicion(valores[1])

                    builderCUP.append(",\n    width: 350")
                    builderCUP.append(",\n    height: 80")
                    builderCUP.append(",\n    pointX: $x" )
                    builderCUP.append(",\n    pointY: $y\n]")
                }
            }

            // PREGUNTA ABIERTA (OPEN)
            else if (trimLinea.startsWith("<open=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "open")
                if (valores.isNotEmpty()) {
                    prepararNuevoElemento()

                    val esSinDimensiones = valores[0].startsWith("\"")

                    // Extraemos los datos: Si no trae dimensiones, usamos valores por defecto
                    val label = if (esSinDimensiones) limpiarComillasDobles(valores[0]) else limpiarComillasDobles(valores[2])
                    val w = if (!esSinDimensiones) limpiarNumeroDimension(valores[0], 350) else 350
                    val h = if (!esSinDimensiones) limpiarNumeroDimension(valores[1], 80) else 80

                    builderCUP.append("OPEN_QUESTION [\n")
                    builderCUP.append("    label: \"$label\",\n")
                    builderCUP.append("    width: $w,\n")  // Usamos el ancho del archivo (blindado)
                    builderCUP.append("    height: $h\n")  // Usamos el alto del archivo (blindado)
                    builderCUP.append("]")
                }
            }

            // SECCIÓN
            else if (trimLinea.startsWith("<section=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "section")
                if (valores.isNotEmpty()) {
                    prepararNuevoElemento()

                    val esSinDimensiones = valores[0].startsWith("\"") || valores[0].equals("VERTICAL", ignoreCase = true) || valores[0].equals("HORIZONTAL", ignoreCase = true)

                    builderCUP.append("SECTION [\n")

                    val w = if (!esSinDimensiones && valores.size >= 5) limpiarNumeroDimension(valores[0], 380) else 380
                    val h = if (!esSinDimensiones && valores.size >= 5) limpiarNumeroDimension(valores[1], 450) else 450
                    val x = limpiarNumeroPosicion(valores[2])
                    val y = limpiarNumeroPosicion(valores[3])
                    val orientation = if (!esSinDimensiones && valores.size >= 5) valores[4].trim() else "VERTICAL"

                    builderCUP.append("    width: $w,\n")
                    builderCUP.append("    height: $h,\n")
                    builderCUP.append("    pointX: $x,\n")
                    builderCUP.append("    pointY: $y,\n")
                    builderCUP.append("    orientation: $orientation,\n")
                    builderCUP.append("    elements: { [\n")

                    pilaEstructuras.push("SECTION")
                    nivelAnidamiento++
                    esPrimerElementoEnBloque = true
                }
            }

            else if (trimLinea == "</section>") {
                if (pilaEstructuras.isNotEmpty() && pilaEstructuras.peek() == "SECTION") {
                    pilaEstructuras.pop()
                    builderCUP.append("\n    ] }\n]")
                    nivelAnidamiento--
                    esPrimerElementoEnBloque = false
                }
            }

            // TABLA
            else if (trimLinea.startsWith("<table=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "table")
                if (valores.isNotEmpty()) {
                    prepararNuevoElemento()

                    val esSinDimensiones = valores[0].startsWith("\"") || valores[0].startsWith("{")

                    builderCUP.append("TABLE [\n")

                    val w = if (!esSinDimensiones && valores.size >= 4) limpiarNumeroDimension(valores[0], 380) else 380
                    val h = if (!esSinDimensiones && valores.size >= 4) limpiarNumeroDimension(valores[1], 300) else 300
                    val x = limpiarNumeroPosicion(valores[2])
                    val y = limpiarNumeroPosicion(valores[3])

                    builderCUP.append("    width: $w,\n")
                    builderCUP.append("    height: $h,\n")
                    builderCUP.append("    pointX: $x,\n")
                    builderCUP.append("    pointY: $y,\n")
                    builderCUP.append("    elements: { \n") // Lista de matrices bidimensionales de CUP

                    pilaEstructuras.push("TABLE")
                    nivelAnidamiento++
                    esPrimerElementoEnBloque = true
                }
            }

            else if (trimLinea == "<line>") {
                prepararNuevoElemento()
                builderCUP.append("[\n")
                nivelAnidamiento++
                esPrimerElementoEnBloque = true
            }

            else if (trimLinea == "</line>") {
                builderCUP.append("\n]")
                nivelAnidamiento--
                esPrimerElementoEnBloque = false
            }

            else if (trimLinea == "</table>") {
                if (pilaEstructuras.isNotEmpty() && pilaEstructuras.peek() == "TABLE") {
                    pilaEstructuras.pop()
                    builderCUP.append("\n    }\n]")
                    nivelAnidamiento--
                    esPrimerElementoEnBloque = false
                }
            }

            // SELECT_QUESTION
            else if (trimLinea.startsWith("<select=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "select")
                if (valores.isNotEmpty()) {
                    prepararNuevoElemento()

                    val esSinDimensiones = valores[0].startsWith("\"")
                    val label = if (esSinDimensiones) limpiarComillasDobles(valores[0]) else limpiarComillasDobles(valores[2])
                    val options = if (esSinDimensiones) limpiarOpciones(valores[1]) else limpiarOpciones(valores[3])
                    val correct = if (esSinDimensiones) limpiarNumero(valores[2]) else limpiarNumero(valores[4])

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
                if (valores.isNotEmpty()) {
                    prepararNuevoElemento()

                    val esSinDimensiones = valores[0].startsWith("\"")
                    val label = if (esSinDimensiones) limpiarComillasDobles(valores[0]) else limpiarComillasDobles(valores[2])
                    val options = if (esSinDimensiones) limpiarOpciones(valores[1]) else limpiarOpciones(valores[3])

                    builderCUP.append("DROP_QUESTION [\n")
                    builderCUP.append("    label: \"$label\",\n")
                    builderCUP.append("    options: { $options }\n")
                    builderCUP.append("]")
                }
            }

            // MULTIPLE_QUESTION
            else if (trimLinea.startsWith("<multiple=")) {
                val valores = extraerValoresEntrePicos(trimLinea, "multiple")
                if (valores.isNotEmpty()) {
                    prepararNuevoElemento()

                    val esSinDimensiones = valores[0].startsWith("\"")
                    val label = if (esSinDimensiones) limpiarComillasDobles(valores[0]) else limpiarComillasDobles(valores[2])
                    val options = if (esSinDimensiones) limpiarOpciones(valores[1]) else limpiarOpciones(valores[3])
                    val correctList = if (esSinDimensiones) limpiarArregloNumerico(valores[2]) else limpiarArregloNumerico(valores[4])

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

    private fun limpiarNumeroDimension(s: String, valorMinimoSeguro: Int): Int {
        val numero = s.toDoubleOrNull()?.toInt() ?: 0
        return if (numero < 20) valorMinimoSeguro else numero //  Evita 0 dp que rompan Layouts
    }

    private fun limpiarNumero(s: String): Int {
        return s.toDoubleOrNull()?.toInt() ?: 0
    }

    private fun limpiarNumeroPosicion(s: String): Int {
        val numero = s.toDoubleOrNull()?.toInt() ?: 10
        return if (numero < 5) 10 else numero
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