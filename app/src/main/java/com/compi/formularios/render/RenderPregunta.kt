package com.compi.formularios.render

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compi.formularios.modelos.Pregunta

@Composable
fun RenderPregunta(
    pregunta: Pregunta,
    respuestas: MutableMap<String, Any>
) {
    val estilos = pregunta.estilos
    val color = obtenerColor(estilos?.get("color"))
    val tamanoRaw = estilos?.get("text size") ?: 16.0
    val fontSize = (tamanoRaw as? Number)?.toFloat() ?: 16f

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = pregunta.label.replace("\"", ""),
            color = color,
            fontSize = fontSize.sp
        )

        pregunta.options.forEach { opcion ->
            val textoOpcion = opcion.replace("\"", "")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (respuestas[pregunta.type] == textoOpcion),
                    onClick = { respuestas[pregunta.type] = textoOpcion }
                )
                Text(textoOpcion, color = color)
            }
        }
    }
}