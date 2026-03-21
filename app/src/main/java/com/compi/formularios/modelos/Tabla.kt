package com.compi.formularios.modelos

data class Tabla(
    val elements: List<Elemento>, // CUP usa ELEMENTS
    val width: Double? = null,
    val height: Double? = null,
    override val pointX: Double? = 0.0,
    override val pointY: Double? = 0.0,
    override val estilos: Map<String, Any>? = null
) : Elemento()