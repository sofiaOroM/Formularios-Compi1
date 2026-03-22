package com.compi.formularios.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compi.formularios.util.ClientRed
import okhttp3.*
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaServidor(
    onRegresar: () -> Unit,
    onDescargarArchivo: (String, String) -> Unit
) {
    var listaArchivos by remember { mutableStateOf<List<String>>(emptyList()) }
    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    var textoBusqueda by remember { mutableStateOf("") }

    val listaFiltrada = listaArchivos.filter {
        it.contains(textoBusqueda, ignoreCase = true)
    }

    fun cargarArchivos() {
        cargando = true
        mensajeError = ""
        val request = Request.Builder()
            .url("${ClientRed.BASE_URL}/listar")
            .get()
            .build()

        ClientRed.okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cargando = false
                mensajeError = "No se pudo conectar a la PC: ${e.message}"
            }

            override fun onResponse(call: Call, response: Response) {
                cargando = false
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    listaArchivos = body.split("\n").filter { it.isNotBlank() }
                } else {
                    mensajeError = "Error del servidor: ${response.code}"
                }
            }
        })
    }

    fun descargarUnArchivo(nombre: String) {
        val request = Request.Builder()
            .url("${ClientRed.BASE_URL}/descargar?nombre=$nombre")
            .get()
            .build()

        ClientRed.okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { /* Manejo de error */ }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val contenido = response.body?.string() ?: ""
                    onDescargarArchivo(nombre, contenido)
                }
            }
        })
    }

    LaunchedEffect(Unit) {
        cargarArchivos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Servidor Central", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { cargarArchivos() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF121212))
                .padding(16.dp)
        ) {
            // BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = { Text("Buscar formulario por nombre...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // LISTA DE ARCHIVOS
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Cyan)
                } else if (mensajeError.isNotBlank()) {
                    Text(
                        text = mensajeError,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (listaFiltrada.isEmpty()) {
                    Text(
                        text = if (listaArchivos.isEmpty()) "No hay archivos en el servidor." else "No se encontraron coincidencias.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(listaFiltrada) { archivo ->
                            ItemArchivoServidor(
                                nombreArchivo = archivo,
                                onDescargar = { descargarUnArchivo(archivo) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemArchivoServidor(nombreArchivo: String, onDescargar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = nombreArchivo,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Formato: .pkm",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onDescargar,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Descargar")
            }
        }
    }
}