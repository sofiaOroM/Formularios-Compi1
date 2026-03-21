package com.compi.formularios.modelos

data class Pregunta(
    val type: String, // OPEN, DROP, SELECT, MULTIPLE
    val label: String, // CUP usa LABEL
    val options: List<String> = emptyList(), // CUP usa OPTIONS
    val correct: Any? = null, // CUP usa CORRECT (puede ser Double o List)
    override val pointX: Double? = 0.0,
    override val pointY: Double? = 0.0,
    override val estilos: Map<String, Any>? = null
) : Elemento()