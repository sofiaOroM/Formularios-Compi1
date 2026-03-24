package com.compi.formularios.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compi.formularios.modelos.Pregunta
import com.compi.formularios.modelos.Seccion
import com.compi.formularios.modelos.Tabla
import com.compi.formularios.modelos.Texto

@Composable
fun RenderSeccion(seccion: Seccion, respuestas: MutableMap<String, Any>) {

    val estilosSeccion = seccion.estilos
    val colorFondoSeccion = obtenerColor(estilosSeccion, "background color", default = Color.Transparent)

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
            .background(colorFondoSeccion)
            .padding(12.dp)
    ) {
        seccion.elements.forEach { elemento ->

            val elementoConEstilo = if (elemento.estilos == null || elemento.estilos!!.isEmpty()) {
                when (elemento) {
                    is Texto -> elemento.copy(estilos = estilosSeccion)
                    is Pregunta -> elemento.copy(estilos = estilosSeccion)
                    is Tabla -> elemento.copy(estilos = estilosSeccion)
                    is Seccion -> elemento.copy(estilos = estilosSeccion)
                    else -> elemento
                }
            } else {
                elemento
            }

            RenderElemento(elementoConEstilo, respuestas)
        }
    }
}
/*@Composable
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
}*/