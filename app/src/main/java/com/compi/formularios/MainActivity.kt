package com.compi.formularios

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.compi.formularios.modelos.Elemento
import com.compi.formularios.parser.ParserBridge
import com.compi.formularios.parser.guardado.ParserBridgeGuardado
import com.compi.formularios.ui.PantallaEditor
import com.compi.formularios.ui.PantallaFormulario
import com.compi.formularios.ui.PantallaMisFormularios
import com.compi.formularios.ui.PantallaOpciones
import com.compi.formularios.ui.PantallaServidor // Importa la pantalla que creamos
import com.compi.formularios.ui.theme.FormulariosTheme
import com.compi.formularios.util.ClientRed
import com.compi.formularios.util.LectorGuardado
import com.compi.formularios.util.LectorGuardado.traducirAParserOriginal
import com.compi.formularios.util.SerializarForm
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormulariosTheme {
                var pantallaActual by remember { mutableStateOf("opciones") }
                var codigoEditor by remember { mutableStateOf("") }
                var elementosFormulario by remember { mutableStateOf<List<Elemento>>(emptyList()) }
                var errorCompilacion by remember { mutableStateOf("") }

                when (pantallaActual) {
                    "opciones" -> PantallaOpciones(
                        onCrearNuevo = {
                            codigoEditor = "" // Resetear para nuevo
                            pantallaActual = "editor"
                        },
                        onVerMisFormularios = {
                            pantallaActual = "mis_formularios"
                        },
                        onExplorarServidor = {
                            pantallaActual = "explorar"
                        },
                        onContestar = {
                            if (elementosFormulario.isNotEmpty()) pantallaActual = "formulario"
                        }
                    )

                    "editor" -> PantallaEditor(
                        codigoInicial = codigoEditor,
                        onGenerar = { codigo ->
                            try {
                                val resultado = ParserBridge.parsearFormulario(codigo)
                                if (resultado.isNotEmpty()) {
                                    elementosFormulario = resultado
                                    codigoEditor = codigo
                                    errorCompilacion = ""
                                    pantallaActual = "formulario"
                                }
                            } catch (e: Exception) {
                                errorCompilacion = e.message ?: "Error de sintaxis"
                            }
                        },
                        error = errorCompilacion,
                        onRegresar = { pantallaActual = "opciones" }
                    )

                    "formulario" -> PantallaFormulario(
                        elementos = elementosFormulario,
                        onRegresar = { pantallaActual = "editor" },
                        onMenu = { pantallaActual = "opciones" },
                        onFinalizar = { respuestas ->
                            guardarArchivoReporteTxt(respuestas)
                            pantallaActual = "opciones"
                        },
                        onGuardarPKM = { autor, nombreArchivoUsuario, descripcion ->
                            val contenido = SerializarForm.serialize(elementosFormulario, autor, descripcion)

                            // 1. Guardar copia local interna
                            guardarArchivoPKMLocal(nombreArchivoUsuario, contenido)

                            // 2. ".pkm" automáticamente para que no falle el formato
                            val nombreLimpio = "${nombreArchivoUsuario.trim().replace(" ", "_")}.pkm"

                            subirAlServidor(nombreLimpio, contenido)
                        }
                    )

                    "mis_formularios" -> PantallaMisFormularios(
                    onRegresar = { pantallaActual = "opciones" },
                    onCargarFormulario = { nombreArchivo, contenido ->
                        try {
                            val codigoParaCUP = traducirAParserOriginal(contenido)
                            android.util.Log.d("TRADUCTOR_CUP", "--- Código generado para CUP --- \n$codigoParaCUP\n---------------------------------")
                            val elementosCargados = ParserBridge.parsearFormulario(codigoParaCUP)
                            if (elementosCargados.isNotEmpty()) {
                                elementosFormulario = elementosCargados
                                pantallaActual = "formulario" // Salta directamente a contestar el formulario local
                                runOnUiThread {
                                    Toast.makeText(this, "Formulario $nombreArchivo cargado con éxito!", Toast.LENGTH_SHORT).show()
                                }                                }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this, "Error al interpretar el archivo local. $e", Toast.LENGTH_LONG).show()
                            }                            }
                    }
                        )


                    "explorar" -> PantallaServidor(
                        onRegresar = { pantallaActual = "opciones" },
                        onDescargarArchivo = { nombre, contenido ->
                            try {
                                val codigoParaCUP = LectorGuardado.traducirAParserOriginal(contenido)
                                android.util.Log.d("TRADUCTOR_CUP", "--- Código generado para CUP --- \n$codigoParaCUP\n---------------------------------")
                                val elementosDescargados = ParserBridge.parsearFormulario(codigoParaCUP)
                                if (elementosDescargados.isNotEmpty()) {
                                    elementosFormulario = elementosDescargados
                                    pantallaActual = "formulario" // Renderizar directamente el descargado

                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "Formulario $nombre descargado!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "No se pudo interpretar el archivo .pkm $e" , Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Funciones auxiliares de guardado físico y de red
    private fun guardarArchivoPKMLocal(nombreArchivoUsuario: String, contenido: String) {
        try {
            val nombreLimpio = "${nombreArchivoUsuario.trim().replace(" ", "_")}.pkm"

            val fileOut = openFileOutput(nombreLimpio, Context.MODE_PRIVATE)
            fileOut.write(contenido.toByteArray())
            fileOut.close()

            runOnUiThread {
                Toast.makeText(this, "¡Guardado localmente como $nombreLimpio!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun guardarArchivoReporteTxt(respuestas: Map<String, Any>) {
        try {
            val sdf = java.text.SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", java.util.Locale.getDefault())
            val fechaFormateada = sdf.format(java.util.Date())
            val contenido = StringBuilder().apply {
                append("SOLUCIÓN DE FORMULARIO\n")
                append("Autor: Estudiante de Compiladores\n")
                append("Fecha y Hora: $fechaFormateada\n")
                append("====================================\n\n")

                respuestas.forEach { (pregunta, respuesta) ->
                    append("Pregunta: $pregunta\n")
                    append("Respuesta: $respuesta\n")
                    append("------------------------------------\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun subirAlServidor(nombreArchivo: String, contenidoPkm: String) {
        val client = ClientRed.okHttpClient
        val body = contenidoPkm.toRequestBody("text/plain".toMediaType())

        val request = Request.Builder()
            .url("${ClientRed.BASE_URL}/subir")
            .addHeader("Nombre-Archivo", nombreArchivo)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Fallo al subir a PC. ¿Servidor apagado?", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "¡Subido a la computadora!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
/*package com.compi.formularios

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.compi.formularios.modelos.Elemento
import com.compi.formularios.parser.ParserBridge
import com.compi.formularios.ui.PantallaEditor
import com.compi.formularios.ui.PantallaFormulario
import com.compi.formularios.ui.PantallaOpciones
import com.compi.formularios.ui.theme.FormulariosTheme
import com.compi.formularios.util.SerializarForm
import com.compi.formularios.util.ClientRed
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormulariosTheme {
                var pantallaActual by remember { mutableStateOf("opciones") }
                var codigoEditor by remember { mutableStateOf("") }
                var elementosFormulario by remember { mutableStateOf<List<Elemento>>(emptyList()) }
                var errorCompilacion by remember { mutableStateOf("") }

                when (pantallaActual) {
                    "opciones" -> PantallaOpciones(
                        onCrearNuevo = {
                            codigoEditor = "" // Resetear para nuevo
                            pantallaActual = "editor"
                        },
                        onVerMisFormularios = { /* Abrir selector de archivos .pkm */ },
                        onExplorarServidor = { /* Lógica de descarga */ },
                        onContestar = {
                            // Aquí podrías cargar un formulario por defecto o el último
                            if (elementosFormulario.isNotEmpty()) pantallaActual = "formulario"
                        }
                    )

                    "editor" -> PantallaEditor(
                        codigoInicial = codigoEditor,
                        onGenerar = { codigo ->
                            try {
                                val resultado = ParserBridge.parsearFormulario(codigo)
                                if (resultado.isNotEmpty()) {
                                    elementosFormulario = resultado
                                    codigoEditor = codigo
                                    errorCompilacion = ""
                                    pantallaActual = "formulario"
                                }
                            } catch (e: Exception) {
                                errorCompilacion = e.message ?: "Error de sintaxis"
                            }
                        },
                        error = errorCompilacion,
                        onRegresar = { pantallaActual = "opciones" }
                    )

                    "formulario" -> PantallaFormulario(
                        elementos = elementosFormulario,
                        onRegresar = { pantallaActual = "editor" },
                        onFinalizar = { respuestas ->
                            println("Respuestas recibidas: $respuestas")
                            pantallaActual = "opciones"
                        },
                        onGuardarPKM = { autor, descripcion ->
                            val contenido = SerializarForm.serialize(elementosFormulario, autor, descripcion)

                            // Lógica de guardado físico
                            try {
                                val fileOut = openFileOutput("formulario.pkm", Context.MODE_PRIVATE)
                                fileOut.write(contenido.toByteArray())
                                fileOut.close()
                                Toast.makeText(this, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )
                }
            }
        }
    }

    fun guardarArchivoPKM(respuestas: Map<String, Any>) {
        try {
            val sdf =
                java.text.SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", java.util.Locale.getDefault())
            val fechaFormateada = sdf.format(java.util.Date())

            val nombreArchivo = "Reporte_$fechaFormateada.txt"

            val contenido = StringBuilder().apply {
                append("SOLUCIÓN DE FORMULARIO\n")
                append("Autor: Estudiante de Compiladores\n") // Aquí puedes poner tu nombre
                append("Fecha y Hora: $fechaFormateada\n")
                append("====================================\n\n")

                respuestas.forEach { (pregunta, respuesta) ->
                    append("Pregunta: $pregunta\n")
                    append("Respuesta: $respuesta\n")
                    append("------------------------------------\n")
                }
            }

            println("--- ARCHIVO GENERADO ---\n$contenido")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun subirAlServidor(nombreArchivo: String, contenidoPkm: String) {
        val client = ClientRed.okHttpClient

        val body = contenidoPkm.toRequestBody("text/plain".toMediaType())

        val request = Request.Builder()
            .url("${ClientRed.BASE_URL}/subir")
            .addHeader("Nombre-Archivo", nombreArchivo)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Éxito
                }
            }
        })
    }
}*/