package com.example.amorhuellitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------
// PALETA DE COLORES "AMOR & HUELLITAS" (sacada del logo)
// ---------------------------------------------------------------
val VerdeOliva = Color(0xFF74743D)     // fondo principal del logo
val Marron = Color(0xFF503116)         // texto, bordes y detalles oscuros
val Dorado = Color(0xFFCD985D)         // pelaje del perrito
val BeigeCrema = Color(0xFFEDDCBF)     // huellita y detalles claros
val Blanco = Color(0xFFFFFFFF)         // bordes y fondo alternativo
val MarronOscuro = Color(0xFF381B09)   // contornos y zonas oscuras

// ---------------------------------------------------------------
// PANTALLAS DE LA APP
// Tu compañero agrega aquí las que le faltan (por ejemplo RESUMEN).
// ---------------------------------------------------------------
enum class Pantalla { INICIO, AGENDAR, SERVICIOS }

// ---------------------------------------------------------------
// ACTIVITY PRINCIPAL
// ---------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppHuellitas()
            }
        }
    }
}

// ---------------------------------------------------------------
// NAVEGACIÓN controlada por estado
// `pantallaActual` decide qué pantalla se muestra.
// rememberSaveable la conserva al rotar el teléfono.
// ---------------------------------------------------------------
@Composable
fun AppHuellitas() {
    var pantallaActual by rememberSaveable { mutableStateOf(Pantalla.INICIO) }
    val activity = LocalActivity.current

    when (pantallaActual) {
        Pantalla.INICIO -> PantallaInicio(
            onAgendarCita = { pantallaActual = Pantalla.AGENDAR },
            onServicios = { pantallaActual = Pantalla.SERVICIOS },
            onSalir = { activity?.finish() }   // cierra la aplicación
        )

        // TODO (compañero): reemplazar por la pantalla del formulario
        Pantalla.AGENDAR -> PantallaTemporal(
            titulo = "Agendar cita",
            onVolver = { pantallaActual = Pantalla.INICIO }
        )

        // TODO (compañero): reemplazar por la pantalla de servicios
        Pantalla.SERVICIOS -> PantallaTemporal(
            titulo = "Nuestros servicios",
            onVolver = { pantallaActual = Pantalla.INICIO }
        )
    }
}

// ---------------------------------------------------------------
// PANTALLA DE INICIO  (esta es TU parte)
// Muestra: fondo de huellitas + logo + 3 botones.
// No navega por sí sola: recibe "eventos" (lambdas) desde afuera.
// ---------------------------------------------------------------
@Composable
fun PantallaInicio(
    onAgendarCita: () -> Unit,
    onServicios: () -> Unit,
    onSalir: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeOliva)
    ) {
        // Fondo decorativo de huellitas
        FondoHuellitas()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo circular con borde blanco
            Image(
                painter = painterResource(id = R.drawable.logo_huellitas),
                contentDescription = "Logo de Amor & Huellitas",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .border(4.dp, Blanco, CircleShape)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Cuidamos a quienes más quieres",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BeigeCrema,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Los tres botones tienen el mismo ancho y alto
            val tamBoton = Modifier
                .fillMaxWidth()
                .height(56.dp)
            val formaBoton = RoundedCornerShape(16.dp)

            // 1. Agendar cita (botón principal, color dorado)
            Button(
                onClick = onAgendarCita,
                modifier = tamBoton,
                shape = formaBoton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Dorado,
                    contentColor = MarronOscuro
                )
            ) {
                Text("Agendar cita", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Nuestros servicios (botón secundario, beige)
            Button(
                onClick = onServicios,
                modifier = tamBoton,
                shape = formaBoton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BeigeCrema,
                    contentColor = Marron
                )
            ) {
                Text("Nuestros servicios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Salir (botón con solo borde)
            OutlinedButton(
                onClick = onSalir,
                modifier = tamBoton,
                shape = formaBoton,
                border = BorderStroke(2.dp, BeigeCrema),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BeigeCrema)
            ) {
                Text("Salir", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ---------------------------------------------------------------
// FONDO: patrón de huellitas dibujado con Canvas
// (no necesita ninguna imagen, se dibuja con código)
// ---------------------------------------------------------------
@Composable
fun FondoHuellitas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val paso = 150.dp.toPx()   // separación entre huellitas
        val tam = 34.dp.toPx()     // tamaño de cada huellita
        var fila = 0
        var y = 40.dp.toPx()
        while (y < size.height + paso) {
            // Las filas impares se desplazan para que el patrón se vea natural
            var x = (if (fila % 2 == 0) 0f else paso / 2) + 40.dp.toPx()
            while (x < size.width + paso) {
                // Giramos cada huellita un poco, a un lado y a otro
                rotate(degrees = if (fila % 2 == 0) -20f else 20f, pivot = Offset(x, y)) {
                    dibujarHuella(Offset(x, y), tam)
                }
                x += paso
            }
            y += paso * 0.8f
            fila++
        }
    }
}

/** Dibuja una huellita: una almohadilla grande y cuatro deditos. */
private fun DrawScope.dibujarHuella(centro: Offset, tam: Float) {
    val color = BeigeCrema.copy(alpha = 0.14f)

    // Almohadilla principal
    drawOval(
        color = color,
        topLeft = Offset(centro.x - tam * 0.5f, centro.y - tam * 0.05f),
        size = Size(tam, tam * 0.8f)
    )

    // Cuatro deditos
    val dedos = listOf(
        Offset(-0.62f, -0.25f),
        Offset(-0.22f, -0.62f),
        Offset(0.22f, -0.62f),
        Offset(0.62f, -0.25f)
    )
    dedos.forEach { d ->
        drawOval(
            color = color,
            topLeft = Offset(
                centro.x + d.x * tam - tam * 0.17f,
                centro.y + d.y * tam - tam * 0.22f
            ),
            size = Size(tam * 0.34f, tam * 0.44f)
        )
    }
}

// ---------------------------------------------------------------
// PANTALLA TEMPORAL: solo sirve para probar que los botones del
// inicio navegan. Se borra cuando estén las pantallas reales.
// ---------------------------------------------------------------
@Composable
fun PantallaTemporal(titulo: String, onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeCrema)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = titulo,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Marron
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "(pantalla pendiente)", color = Marron)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onVolver,
            colors = ButtonDefaults.buttonColors(containerColor = VerdeOliva)
        ) {
            Text("Volver al inicio")
        }
    }
}

// ---------------------------------------------------------------
// PREVIEW (vista previa en Android Studio)
// ---------------------------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaInicioPreview() {
    MaterialTheme {
        PantallaInicio(onAgendarCita = {}, onServicios = {}, onSalir = {})
    }
}