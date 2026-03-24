package com.compi.formularios.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compi.formularios.modelos.Pregunta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderPregunta(
    pregunta: Pregunta,
    respuestas: MutableMap<String, Any>
) {
    val estilos = pregunta.estilos

    // 🎨 1. Usamos el mapeador seguro para ignorar ""comillas dobles"" y mayúsculas
    val colorTexto = obtenerColor(estilos, "color")
    val colorFondoCard = obtenerColor(estilos, "background color", default = MaterialTheme.colorScheme.surface)
    val fuente = obtenerFuente(estilos)
    val tamanio = obtenerTamanioLetra(estilos)
    val tipo = pregunta.type.uppercase()
    val llave = pregunta.label.replace("\"", "").trim()
    var opcionesDinamicas by remember { mutableStateOf<List<String>>(emptyList()) }
    var estaCargando by remember { mutableStateOf(false) }

    // 2. POKEMON_API:n:m
    LaunchedEffect(pregunta.options) {
        val comando = pregunta.options.firstOrNull() ?: ""

        if (comando.startsWith("POKEMON_API:")) {
            estaCargando = true
            try {
                // Extraemos el rango del string "POKEMON_API:1:20"
                val partes = comando.split(":")
                val inicio = partes[1].toInt()
                val fin = partes[2].toInt()

                opcionesDinamicas = PokeApiHelper.obtenerRangoPokemones(inicio, fin)
            } catch (e: Exception) {
                opcionesDinamicas = listOf("Error al cargar Pokémones")
            }
            estaCargando = false
        } else {
            opcionesDinamicas = pregunta.options.map { it.replace("\"", "").trim() }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondoCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ParserEmojis.procesar(llave),
                color = colorTexto,
                fontFamily = fuente,
                fontSize = tamanio
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (tipo) {
                "OPEN" -> {
                    var textoIngresado by remember {
                        mutableStateOf(
                            respuestas[llave]?.toString() ?: ""
                        )
                    }
                    OutlinedTextField(
                        value = textoIngresado,
                        onValueChange = {
                            textoIngresado = it
                            respuestas[llave] = it
                        },
                        modifier = Modifier
                        .heightIn(min = 56.dp),
                        placeholder = { Text("Escribe aquí...", color = colorTexto.copy(alpha = 0.6f)) }
                    )
                }

                "DROP" -> {
                    var expanded by remember { mutableStateOf(false) }
                    val seleccionada = respuestas[llave]?.toString() ?: "Selecciona una opción"

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { if (!estaCargando) expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = if (estaCargando) "Buscando en la PokéAPI..." else seleccionada,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                if (estaCargando) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                }
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )

                        if (!estaCargando) {
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(colorFondoCard)
                            ) {
                                opcionesDinamicas.forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion, color = colorTexto, fontFamily = fuente)},
                                        onClick = {
                                            respuestas[llave] = opcion
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "MULTIPLE" -> {
                    pregunta.options.forEach { opcion ->
                        val textoOpcion = opcion.replace("\"", "").trim()
                        val seleccionados =
                            respuestas[llave] as? MutableList<String> ?: mutableListOf()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = seleccionados.contains(textoOpcion),
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        if (!seleccionados.contains(textoOpcion)) seleccionados.add(
                                            textoOpcion
                                        )
                                    } else {
                                        seleccionados.remove(textoOpcion)
                                    }
                                    respuestas[llave] = seleccionados.toMutableList()
                                }
                            )
                            Text(ParserEmojis.procesar(textoOpcion), color = colorTexto,
                                fontFamily = fuente,
                                fontSize = tamanio,
                                modifier = Modifier)
                        }
                    }
                }

                "SELECT" -> {
                    pregunta.options.forEach { opcion ->
                        val textoOpcion = opcion.replace("\"", "").trim()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (respuestas[llave] == textoOpcion),
                                onClick = { respuestas[llave] = textoOpcion }
                            )
                            Text(ParserEmojis.procesar(textoOpcion), color = colorTexto, fontFamily = fuente,
                                fontSize = tamanio)
                        }
                    }
                }
            }
        }
    }
}