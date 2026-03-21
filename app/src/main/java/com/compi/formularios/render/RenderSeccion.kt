package com.compi.formularios.render

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compi.formularios.modelos.Seccion

@Composable
fun RenderSeccion(seccion: Seccion, respuestas: MutableMap<String, Any>) {
    Column(
        modifier = Modifier
            .padding(
                start = (seccion.pointX ?: 0.0).toDouble().dp,
                top = (seccion.pointY ?: 0.0).toDouble().dp
            )
            .then(
                if (seccion.width != null) Modifier.width(seccion.width!!.toDouble().dp)
                else Modifier.fillMaxWidth()
            )
    ) {
        seccion.elements.forEach { elemento ->
            RenderElemento(elemento, respuestas)
        }
    }
}