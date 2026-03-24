package com.compi.formularios.ui

import android.icu.text.ListFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compi.formularios.modelos.Elemento
import com.compi.formularios.render.RenderFormulario
import com.compi.formularios.util.SerializarForm.obtenerFechaActual

@Composable
fun PantallaFormulario(
    elementos: List<Elemento>,
    onMenu: () -> Unit,
    onRegresar: () -> Unit,
    onFinalizar: (Map<String, Any>) -> Unit,
    onGuardarPKM: (autor: String, archivo:String, descripcion: String) -> Unit
) {
    val respuestas = remember { mutableStateMapOf<String, Any>() }
    val scrollState = rememberScrollState()
    var mostrarDialogoGuardar by remember { mutableStateOf(false) }
    var nombreAutor by remember { mutableStateOf("") }
    var nombreArchivo by remember { mutableStateOf("") }

    if (mostrarDialogoGuardar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoGuardar = false },
            title = { Text("Guardar Formulario .pkm") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingresa los datos para los metadatos y el servidor:")

                    OutlinedTextField(
                        value = nombreAutor,
                        onValueChange = { nombreAutor = it },
                        label = { Text("Nombre del Autor") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nombreArchivo,
                        onValueChange = { nombreArchivo = it },
                        label = { Text("Nombre del Archivo (ej: examen_mate)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (nombreAutor.isNotBlank() && nombreArchivo.isNotBlank()) {
                        // Pasamos autor, nombre de archivo y descripción a la función
                        onGuardarPKM(nombreAutor, nombreArchivo, "Formulario creado por $nombreAutor")
                        mostrarDialogoGuardar = false
                    }
                }) { Text("Guardar y Subir") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoGuardar = false }) { Text("Cancelar") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // --- BARRA SUPERIOR ---
        Surface(
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenu) {
                    Icon(Icons.Default.Home,contentDescription = "Principal", tint = Color.White
                    )
                }
                Text("Formulario", color = Color.White, style = MaterialTheme.typography.titleLarge)

                Button(
                    onClick = { mostrarDialogoGuardar = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Guardar .pkm")
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            RenderFormulario(
                elementos = elementos,
                respuestas = respuestas
            )
        }

        // --- BARRA INFERIOR DE ACCIONES ---
        Surface(
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Botón editar
                OutlinedButton(
                    onClick = onRegresar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar Código")
                }

                // Botón finalizar
                Button(
                    onClick = { onFinalizar(respuestas.toMap()) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Finalizar y Calificar")
                }
            }
        }
    }
}