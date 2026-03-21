package com.compi.formularios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.compi.formularios.modelos.Elemento
import com.compi.formularios.parser.ParserBridge
import com.compi.formularios.ui.PantallaEditor
import com.compi.formularios.ui.PantallaFormulario
import com.compi.formularios.ui.PantallaOpciones
import com.compi.formularios.ui.theme.FormulariosTheme

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
                        onGuardarPKM = { respuestas ->
                            guardarArchivoPKM(respuestas)
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
}