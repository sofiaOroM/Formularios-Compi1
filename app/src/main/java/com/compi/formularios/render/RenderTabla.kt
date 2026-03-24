package com.compi.formularios.render // Paquete actualizado

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compi.formularios.modelos.Elemento
import com.compi.formularios.modelos.Pregunta
import com.compi.formularios.modelos.Seccion
import com.compi.formularios.modelos.Tabla
import com.compi.formularios.modelos.Texto

@Composable
fun RenderTabla(
    tabla: Tabla,
    respuestas: MutableMap<String, Any>
) {
    // 1. EXTRAER Y NORMALIZAR FILAS
    val contenido = tabla.elements

    val estilosDeLaTabla = tabla.estilos
    val colorFondoTabla = obtenerColor(estilosDeLaTabla, "background color", default = Color.Transparent)

    @Suppress("UNCHECKED_CAST")
    val filas: List<List<Elemento>> = when {
        contenido.isEmpty() -> {
            println("DEBUG: Tabla vacía")
            emptyList()
        }
        contenido[0] is List<*> -> {
            contenido as List<List<Elemento>>
        }
        else -> {
            // Si el CUP mandó una lista simple, filas de 1 sola celda
            contenido.map { listOf(it as Elemento) }
        }
    }

    // 2. RENDERIZADO
    // Si la tabla no tiene ancho definido, uno por defecto para que no sea 0dp
    val anchoTabla = (tabla.width ?: 300.0).toDouble().dp

    Column(
        modifier = Modifier
            .padding(
                start = (tabla.pointX ?: 0.0).toDouble().dp,
                top = (tabla.pointY ?: 0.0).toDouble().dp
            )
            .width(anchoTabla)
            .background(colorFondoTabla)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
    ) {
        if (filas.isEmpty()) {
            Box(Modifier.padding(8.dp)) { androidx.compose.material3.Text("Tabla sin datos") }
        }

        filas.forEach { fila ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                fila.forEach { elemento ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(0.5.dp, Color.LightGray)
                            .padding(8.dp)
                            .defaultMinSize(minHeight = 40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        val elementoConEstilo = if (elemento.estilos == null || elemento.estilos!!.isEmpty()) {
                            when (elemento) {
                                is Texto -> elemento.copy(estilos = estilosDeLaTabla)
                                is Pregunta -> elemento.copy(estilos = estilosDeLaTabla)
                                is Seccion -> elemento.copy(estilos = estilosDeLaTabla)
                                is Tabla -> elemento.copy(estilos = estilosDeLaTabla)
                                else -> elemento
                            }
                        } else {
                            elemento // Si ya trae su propio estilo definido en CUP, se respeta
                        }

                        RenderElemento(elementoConEstilo, respuestas)
                    }
                }
            }
        }
    }
}