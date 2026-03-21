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
        // CUP usa el nombre 'elements'
        seccion.elements.forEach { elemento ->
            RenderElemento(elemento, respuestas)
        }
    }
}
/*@Composable
fun RenderSeccion(
    seccion: Seccion,
    respuestas: MutableMap<String, Any>
) {

    val x = (seccion.x as? Number)?.toFloat() ?: 0f
    val y = (seccion.y as? Number)?.toFloat() ?: 0f
    val width = (seccion.width as? Number)?.toFloat() ?: 300f
    val height = (seccion.height as? Number)?.toFloat() ?: 200f
    Box(
        modifier = Modifier
            .offset(x.dp, y.dp)
            .width(width.dp)
            .height(height.dp)
            .background(obtenerColor(seccion.estilos?.get("background color")))
            .padding(8.dp)
    ) {

        if (seccion.orientation == "HORIZONTAL") {
            Row {
                seccion.elementos.forEach {
                    RenderElemento(it, respuestas, seccion.estilos)
                }
            }
        } else {
            Column {
                seccion.elementos.forEach {
                    RenderElemento(it, respuestas, seccion.estilos)
                }
            }
        }
    }
}*/