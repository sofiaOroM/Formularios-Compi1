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
    // Usamos el Map de estilos directamente
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
                    selected = (respuestas[pregunta.type] == textoOpcion), // Usamos type como clave si no hay ID
                    onClick = { respuestas[pregunta.type] = textoOpcion }
                )
                Text(textoOpcion, color = color)
            }
        }
    }
}
/*@Composable
fun RenderPregunta(
    pregunta: Pregunta,
    respuestas: MutableMap<String, Any>,
    estilos: Map<String, Any>? = null
) {
    val colorTexto = obtenerColor(estilos?.get("color"))
    val tamanoTexto = (estilos?.get("text size") as? Number)?.toFloat() ?: 16f


    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = pregunta.label ?: "",
            color = if (colorTexto == Color.Transparent) Color.Unspecified else colorTexto,
            fontSize = tamanoTexto.sp,
            style = when(estilos?.get("font family")?.toString()) {
                "MONO" -> MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                else -> MaterialTheme.typography.titleMedium
            }
        )*/
    /*Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = pregunta.label ?: "",
            fontSize = tamanoTexto.sp,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )*/

/*        when (pregunta.tipo) {
            "OPEN" -> {
                var text by remember { mutableStateOf("") }
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        respuestas[pregunta.label ?: ""] = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "SELECT" -> {
                var selected by remember { mutableStateOf("") }
                pregunta.opciones?.forEach { opcion ->
                    // Row alinea el RadioButton y el Texto horizontalmente
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selected == opcion,
                            onClick = {
                                selected = opcion
                                respuestas[pregunta.label ?: ""] = opcion
                            }
                        )
                        Text(opcion)
                    }
                }
            }

            "MULTIPLE" -> {
                val seleccionados = remember { mutableStateListOf<String>() }
                pregunta.opciones?.forEach { opcion ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = seleccionados.contains(opcion),
                            onCheckedChange = { isChecked ->
                                if (isChecked) seleccionados.add(opcion)
                                else seleccionados.remove(opcion)
                                respuestas[pregunta.label ?: ""] = seleccionados.toList()
                            }
                        )
                        Text(opcion)
                    }
                }
            }

            "DROP" -> {
                Column {
                    pregunta.opciones?.forEach {
                        Text(it, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}*//*package com.compi.formularios.render

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.compi.formularios.modelos.Pregunta

class RenderPregunta
@Composable
fun RenderPregunta(
    pregunta: Pregunta,
    respuestas: MutableMap<String, Any>
) {

    Text(pregunta.label ?: "")

    when (pregunta.tipo) {

        "OPEN" -> {
            var text by remember { mutableStateOf("") }

            TextField(
                value = text,
                onValueChange = {
                    text = it
                    respuestas[pregunta.label ?: ""] = it
                }
            )
        }

        "SELECT" -> {
            var selected by remember { mutableStateOf("") }

            pregunta.opciones?.forEach { opcion ->

                Row {
                    RadioButton(
                        selected = selected == opcion,
                        onClick = {
                            selected = opcion
                            respuestas[pregunta.label ?: ""] = opcion
                        }
                    )
                    Text(opcion)
                }
            }
        }

        "MULTIPLE" -> {
            val seleccionados = remember { mutableStateListOf<String>() }

            pregunta.opciones?.forEach { opcion ->

                Row {
                    Checkbox(
                        checked = seleccionados.contains(opcion),
                        onCheckedChange = {
                            if (it) seleccionados.add(opcion)
                            else seleccionados.remove(opcion)

                            respuestas[pregunta.label ?: ""] = seleccionados
                        }
                    )
                    Text(opcion)
                }
            }
        }

        "DROP" -> {
            pregunta.opciones?.forEach {
                Text(it)
            }
        }
    }
}*/