package com.compi.formularios.render

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.compi.formularios.modelos.*

@Composable
fun RenderFormulario(
    elementos: List<Elemento>,
    respuestas: MutableMap<String, Any>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        elementos.forEach { elemento ->
            RenderElemento(elemento, respuestas)
        }
    }
}