package com.compi.formularios.render

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compi.formularios.modelos.*

@Composable
fun RenderElemento(elemento: Elemento, respuestas: MutableMap<String, Any>) {
    Column(modifier = Modifier.padding(8.dp)) {
        when (elemento) {
            is Seccion -> RenderSeccion(elemento, respuestas)
            is Texto -> RenderTexto(elemento)
            is Tabla -> RenderTabla(elemento, respuestas)
            is Pregunta -> RenderPregunta(elemento, respuestas)
        }
    }
}