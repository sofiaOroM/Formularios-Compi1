package com.compi.formularios.ui.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
class CodigoVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Tomamos el texto plano (text.text) y lo pasamos por tu función de coloreado
        return TransformedText(
            colorearCodigo(text.text),
            OffsetMapping.Identity
        )
    }
}