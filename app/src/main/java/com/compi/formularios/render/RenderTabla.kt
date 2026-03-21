package com.compi.formularios.render

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compi.formularios.modelos.Tabla

@Composable
fun RenderTabla(
    tabla: Tabla,
    respuestas: MutableMap<String, Any>
) {
    val elementosPorFila = 2
    val filas = tabla.elements.chunked(elementosPorFila)

    Column(
        modifier = Modifier
            .padding(
                start = (tabla.pointX ?: 0.0).toDouble().dp,
                top = (tabla.pointY ?: 0.0).toDouble().dp
            )
            .width((tabla.width ?: 300.0).toDouble().dp)
    ) {
        filas.forEach { fila ->
            Row(modifier = Modifier.fillMaxWidth()) {
                fila.forEach { elemento ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .padding(6.dp)
                    ) {
                        RenderElemento(elemento, respuestas)
                    }
                }
            }
        }
    }
}