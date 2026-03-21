package com.compi.formularios.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditorCodigo(
    codigo: String,
    onCodigoChange: (String) -> Unit
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .background(Color(0xFF2D2D2D))
                .verticalScroll(verticalScrollState)
                .padding(vertical = 8.dp)
        ) {
            val lineCount = codigo.lines().size
            for (i in 1..lineCount) {
                Text(
                    text = "$i",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }

        // EDITOR REAL
        BasicTextField(
            value = codigo,
            onValueChange = onCodigoChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
                .padding(8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 20.5.sp,
                fontFamily = FontFamily.Monospace
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.Cyan),
            visualTransformation = CodigoVisualTransformation()
        )
    }
}
/*@Composable
fun EditorCodigo(
    codigo: String,
    onCodigoChange: (String) -> Unit
) {

    val scroll = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFF1E1E1E))
    ) {

        // NUMEROS DE LINEA
        Column(
            modifier = Modifier
                .width(40.dp)
                .background(Color(0xFF2D2D2D))
                .verticalScroll(scroll)
        ) {

            val lineas = codigo.lines()

            lineas.forEachIndexed { index, _ ->

                Text(
                    text = "${index + 1}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // EDITOR
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .padding(8.dp)
        ) {

            BasicTextField(

                value = codigo,

                onValueChange = onCodigoChange,

                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                ),

                decorationBox = { inner ->

                    val coloreado = colorearCodigo(codigo)

                    Text(
                        coloreado,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp
                    )

                    inner()
                },

                modifier = Modifier.fillMaxSize()
            )
        }
    }
}*/