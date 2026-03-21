package com.compi.formularios.render

object ParserEmojis{

        fun procesar(texto: String): String {
            var resultado = texto.replace("\"", "") // Limpiar comillas

            // 1. Regex para Estrellas @[:star:n:] o @[:star:]
            resultado = Regex("""@\[:star(?::(\d+))?:]""").replace(resultado) { match ->
                val cantidad = match.groups[1]?.value?.toInt() ?: 1
                "⭐".repeat(cantidad)
            }

            // 2. Regex para Corazones @[:heart:] o variantes con < y 3
            // El enunciado dice: @[<3] o @[<<<<3333]
            resultado = Regex("""@\[<+3+]""").replace(resultado) { "❤️" }
            resultado = Regex("""@\[:heart:]""").replace(resultado) { "❤️" }

            // 3. Regex para Caritas Felices @[:)] o @[:smile:] o @[:)))]
            resultado = Regex("""@\[:(\)+)]""").replace(resultado) { "😀" }
            resultado = Regex("""@\[:smile:]""").replace(resultado) { "😀" }

            // 4. Regex para Caritas Tristes @[:(] o @[:sad:] o @[:(((]
            resultado = Regex("""@\[:\(+]""").replace(resultado) { "😟" }
            resultado = Regex("""@\[:sad:]""").replace(resultado) { "😟" }

            // 5. Regex para Caritas Serias @[:|] o @[:serious:] o @[:|||]
            resultado = Regex("""@\[:\|+]""").replace(resultado) { "😐" }
            resultado = Regex("""@\[:serious:]""").replace(resultado) { "😐" }

            // 6. Gato @[:^^:] o @[:cat:]
            resultado = Regex("""@\[:\^\^:]""").replace(resultado) { "🐱" }
            resultado = Regex("""@\[:cat:]""").replace(resultado) { "🐱" }

            return resultado
        }
    }