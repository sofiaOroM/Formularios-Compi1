package com.compi.formularios.render

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compi.formularios.modelos.Texto

@Composable
fun RenderTexto(texto: Texto) {
    val estilos = texto.estilos

    val colorFinal = obtenerColor(estilos?.get("color"))
    val tamanoRaw = estilos?.get("text size") ?: 16.0
    val fontSize = (tamanoRaw as? Number)?.toFloat() ?: 16f

    Text(
        text = texto.content.replace("\"", ""),
        fontSize = fontSize.sp,
        color = colorFinal, // Usar la función obtenerColor que hicimos antes
        modifier = Modifier.padding(
            start = (texto.pointX ?: 0.0).dp,
            top = (texto.pointY ?: 0.0).dp
        ).padding(8.dp)
    )
}
/*@Composable
fun RenderTexto(
    texto: Texto,
    estilos: Map<String, Any>? = null
) {

    val color = obtenerColor(estilos?.get("color"))
    val fontSize = (estilos?.get("text size") as? Number)?.toInt() ?: 16

    Text(
        text = parsearEmojis(texto.contenido.replace("\"", "")),
        color = color,
        fontSize = fontSize.dp.value.sp,
        style = MaterialTheme.typography.bodyLarge
    )
}*/