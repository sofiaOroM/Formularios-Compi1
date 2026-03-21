package com.compi.formularios.modelos

data class Seccion(
    val elements: List<Elemento>, // CUP usa ELEMENTS
    val orientation: String = "VERTICAL", // CUP usa ORIENTATION
    val width: Double? = null,
    val height: Double? = null,
    override val pointX: Double? = 0.0,
    override val pointY: Double? = 0.0,
    override val estilos: Map<String, Any>? = null
) : Elemento()