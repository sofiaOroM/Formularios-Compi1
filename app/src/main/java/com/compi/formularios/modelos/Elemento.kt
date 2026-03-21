package com.compi.formularios.modelos

sealed class Elemento {
    abstract val estilos: Map<String, Any>?
    abstract val pointX: Double?
    abstract val pointY: Double?
}