package com.compi.formularios.modelos

data class Texto(
    val content: String, // CUP usa CONTENT
    override val pointX: Double? = 0.0,
    override val pointY: Double? = 0.0,
    override val estilos: Map<String, Any>? = null
) : Elemento()