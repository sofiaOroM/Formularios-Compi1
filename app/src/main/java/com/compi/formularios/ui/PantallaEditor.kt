package com.compi.formularios.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compi.formularios.ui.editor.EditorCodigo

@Composable
fun PantallaEditor(
    onGenerar: (String) -> Unit,
    error: String,
    codigoInicial: String,
    onRegresar: () -> Unit
) {
    var codigo by remember(codigoInicial) { mutableStateOf(codigoInicial) }

    val scrollEstadoPantalla = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .verticalScroll(scrollEstadoPantalla)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Editor de Formularios",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            // Botón para volver a opciones
            TextButton(onClick = onRegresar) {
                Text("Cerrar", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        EditorCodigo(
            codigo = codigo,
            onCodigoChange = { codigo = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onGenerar(codigo) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
        ) {
            Text("Generar y Ejecutar", color = Color.Black)
        }

        // SECCIÓN DE ERRORES
        Spacer(modifier = Modifier.height(10.dp))

        if (error.trim().isNotEmpty()) {
            TablaErrores(errorRaw = error)
        }
    }
}

@Composable
fun TablaErrores(errorRaw: String) {
    val listaDeErrores = errorRaw.split("\n").filter { it.isNotBlank() }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF210000)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF44336)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Reporte de Errores (${listaDeErrores.size} detectados)",
                color = Color(0xFFFFB4AB),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .background(Color(0xFFB00020))
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text("Descripción del Error", color = Color.White, style = MaterialTheme.typography.labelMedium)
            }

            listaDeErrores.forEachIndexed { index, mensaje ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (index % 2 == 0) Color(0xFF2D0000) else Color(0xFF210000))
                        .padding(12.dp)
                ) {
                    Text(
                        text = mensaje,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                if (index < listaDeErrores.size - 1) {
                    HorizontalDivider(color = Color(0xFF440000), thickness = 0.5.dp)
                }
            }
        }
    }
}