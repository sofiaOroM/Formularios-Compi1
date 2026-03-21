package com.compi.formularios.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.compi.formularios.modelos.Elemento
import com.compi.formularios.render.RenderFormulario

@Composable
fun PantallaFormulario(
    elementos: List<Elemento>,
    onRegresar: () -> Unit,
    onFinalizar: (Map<String, Any>) -> Unit,
    onGuardarPKM: (Map<String, Any>) -> Unit // Nueva función para guardar
) {
    val respuestas = remember { mutableStateMapOf<String, Any>() }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Un gris muy claro de fondo mejora la vista
    ) {
        // --- BARRA SUPERIOR (TOOLBAR) ---
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
                TextButton(onClick = onRegresar) {
                    Text("VOLVER", color = Color.White)
                }
                Text("Formulario", color = Color.White, style = MaterialTheme.typography.titleLarge)

                Button(
                    onClick = { onGuardarPKM(respuestas.toMap()) },
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
                // Botón secundario
                OutlinedButton(
                    onClick = onRegresar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar Código")
                }

                // Botón principal
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
/*@Composable
fun PantallaFormulario(
    elementos: List<Elemento>,
    onRegresar: () -> Unit,
    onFinalizar: (Map<String, Any>) -> Unit
) {

    // Estado para respuestas
    val respuestas = remember { mutableStateMapOf<String, Any>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Formulario generado",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(10.dp))

        // SCROLL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1000.dp)
        ) {

            //RENDER DINÁMICO
            RenderFormulario(
                elementos = elementos,
                respuestas = respuestas
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(onClick = onRegresar) {
                Text("Regresar")
            }

            Button(
                onClick = {
                    onFinalizar(respuestas.toMap())
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Finalizar y Calificar")
            }
        }
    }
}*/