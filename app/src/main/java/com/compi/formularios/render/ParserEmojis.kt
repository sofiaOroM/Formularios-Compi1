package com.compi.formularios.render

fun parsearEmojis(texto: String): String {

    return texto
        .replace("@[:star:]", "⭐")
        .replace("@[:star:2:]", "⭐⭐")
        .replace("@[:star:3:]", "⭐⭐⭐")
        .replace("@[:))]", "🙂")
}