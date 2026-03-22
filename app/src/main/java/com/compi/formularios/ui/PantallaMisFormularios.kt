package com.compi.formularios.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMisFormularios(
    onRegresar: () -> Unit,
    onCargarFormulario: (String, String) -> Unit
) {
    val context = LocalContext.current
    var listaArchivos by remember { mutableStateOf<List<File>>(emptyList()) }
    var textoBusqueda by remember { mutableStateOf("") }

    // Función para leer los archivos locales de la app
    fun cargarArchivosLocales() {
        val carpetaInterna = context.filesDir
        val archivos = carpetaInterna.listFiles()?.filter { it.isFile && it.name.endsWith(".pkm") } ?: emptyList()
        listaArchivos = archivos
    }

    val listaFiltrada = listaArchivos.filter {
        it.name.contains(textoBusqueda, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        cargarArchivosLocales()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Formularios Locales", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
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
            // Barra de búsqueda local
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = { Text("Buscar en el teléfono...", color = Color.Gray) },
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

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (listaFiltrada.isEmpty()) {
                    Text(
                        text = if (listaArchivos.isEmpty()) "No has guardado formularios en el teléfono." else "No se encontraron coincidencias.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(listaFiltrada) { archivo ->
                            ItemArchivoLocal(
                                archivo = archivo,
                                onCargar = {
                                    try {
                                        val contenido = archivo.readText()
                                        // 📝 Mandamos a traducir y cargar
                                        onCargarFormulario(archivo.name, contenido)
                                    } catch (e: Exception) {
                                        android.util.Log.e("CUP_ERROR", "Error al procesar el archivo parseado: ${e.message}")
                                    }
                                },
                                onEliminar = {
                                    archivo.delete()
                                    cargarArchivosLocales()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemArchivoLocal(archivo: File, onCargar: () -> Unit, onEliminar: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = archivo.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Almacenamiento Interno",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }

                Button(
                    onClick = {
                        try {
                            onCargar() // Llama a la función que lee el archivo y abre CUP
                        } catch (e: Exception) {
                            e.printStackTrace()
                            android.util.Log.e("CARGAR_ARCHIVO", "Error recuperando el archivo: ${e.message}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Abrir")
                }
            }
        }
    }
}