package com.compi.formularios.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PantallaOpciones(
    onCrearNuevo: () -> Unit,
    onVerMisFormularios: () -> Unit,
    onExplorarServidor: () -> Unit,
    onContestar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Administrador de Formularios",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // BOTÓN PRINCIPAL: CREAR
        Button(
            onClick = onCrearNuevo,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear Nuevo Formulario", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECCIÓN LOCAL
        OutlinedButton(
            onClick = onVerMisFormularios,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Mis Formularios", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECCIÓN SERVIDOR
        OutlinedButton(
            onClick = onExplorarServidor,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Explorar Formularios del Servidor", color = Color.Cyan)
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(color = Color.DarkGray)
        Spacer(modifier = Modifier.height(32.dp))

        // ACCIÓN DE RESPUESTA
        FilledTonalButton(
            onClick = onContestar,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Contestar Último Formulario")
        }

    }
}
