package com.compi.formularios.render

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        text = ParserEmojis.procesar(texto.content),
        fontSize = fontSize.sp,
        color = colorFinal,
        modifier = Modifier.padding(
            start = (texto.pointX ?: 0.0).dp,
            top = (texto.pointY ?: 0.0).dp
        ).padding(8.dp)
    )
}