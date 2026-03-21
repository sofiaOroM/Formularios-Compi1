package com.compi.formularios.modelos

data class Pregunta(
    val type: String, // OPEN, DROP, SELECT, MULTIPLE
    val label: String, //LABEL
    val options: List<String> = emptyList(), //OPTIONS
    val correct: Any? = null, //CORRECT
    override val pointX: Double? = 0.0,
    override val pointY: Double? = 0.0,
    override val estilos: Map<String, Any>? = null
) : Elemento()