package com.example.amorhuellitas

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VeterinariaApp()
                }
            }
        }
    }
}

enum class Pantalla { INICIO, AGENDAR, SERVICIOS }

@Composable
fun VeterinariaApp() {
    val context = LocalContext.current
    var pantallaActual by remember { mutableStateOf(Pantalla.INICIO) }

    when (pantallaActual) {
        Pantalla.INICIO -> PantallaInicio(
            onAgendar = { pantallaActual = Pantalla.AGENDAR },
            onServicios = { pantallaActual = Pantalla.SERVICIOS },
            onSalir = { (context as? Activity)?.finish() }
        )
        Pantalla.AGENDAR -> PantallaAgendar(
            onCancelar = { pantallaActual = Pantalla.INICIO }
        )
        Pantalla.SERVICIOS -> PantallaServicios(
            onVolverInicio = { pantallaActual = Pantalla.INICIO }
        )
    }
}

@Composable
fun PantallaInicio(
    onAgendar: () -> Unit,
    onServicios: () -> Unit,
    onSalir: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo_huellitas),
            contentDescription = "Fondo de veterinaria",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF74743D).copy(alpha = 0.85f)))
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_huellitas),
                contentDescription = "Logo de la veterinaria",
                modifier = Modifier.size(160.dp).clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("Amor & Huellitas", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Cuidamos a quienes más quieres", fontSize = 17.sp, color = Color(0xFFEDDCBF))
            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = onAgendar,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCD985D))
            ) { Text("Agendar Cita", fontSize = 17.sp, color = Color(0xFF381B09), fontWeight = FontWeight.Bold) }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onServicios,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDDCBF))
            ) { Text("Nuestros servicios", fontSize = 17.sp, color = Color(0xFF503116), fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onSalir,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) { Text("Salir", fontSize = 17.sp) }
        }
    }
}

@Composable
fun PantallaAgendar(onCancelar: () -> Unit) {
    val context = LocalContext.current
    val db = remember { CitaDatabase(context.applicationContext) }

    var idSeleccionado by remember { mutableStateOf(0) }
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var tamano by remember { mutableStateOf("") }
    var citas by remember { mutableStateOf(emptyList<Cita>()) }
    var mostrarError by remember { mutableStateOf(false) }
    var mostrarConfirmacionEliminar by remember { mutableStateOf(false) }
    var consultaRealizada by remember { mutableStateOf(false) }

    fun cargarCitas() {
        citas = db.consultarTodas()
        consultaRealizada = true
    }

    fun limpiarFormulario() {
        idSeleccionado = 0
        nombre = ""
        telefono = ""
        fecha = ""
        hora = ""
        raza = ""
        tamano = ""
    }

    fun cargarCita(cita: Cita) {
        idSeleccionado = cita.id
        nombre = cita.nombre
        telefono = cita.telefono
        fecha = cita.fecha
        hora = cita.hora
        raza = cita.raza
        tamano = cita.tamano
    }

    val calendar = Calendar.getInstance()

    fun seleccionarFecha() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                fecha = String.format("%02d/%02d/%04d", day, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun seleccionarHora() {
        TimePickerDialog(
            context,
            { _, hour, minute -> hora = String.format("%02d:%02d", hour, minute) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    LaunchedEffect(Unit) { cargarCitas() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)
    ) {
        Text("Agendar Cita", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF503116))
        Text(
            if (idSeleccionado == 0) "Complete el formulario para registrar una cita."
            else "Editando la cita número $idSeleccionado",
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it },
            modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del propietario") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = telefono, onValueChange = { telefono = it },
            modifier = Modifier.fillMaxWidth(), label = { Text("Número de teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = fecha, onValueChange = {}, modifier = Modifier.fillMaxWidth(),
            label = { Text("Fecha de la cita") }, readOnly = true,
            trailingIcon = { TextButton(onClick = { seleccionarFecha() }) { Text("Elegir") } }
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = hora, onValueChange = {}, modifier = Modifier.fillMaxWidth(),
            label = { Text("Hora de la cita") }, readOnly = true,
            trailingIcon = { TextButton(onClick = { seleccionarHora() }) { Text("Elegir") } }
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = raza, onValueChange = { raza = it },
            modifier = Modifier.fillMaxWidth(), label = { Text("Raza de la mascota") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = tamano, onValueChange = { tamano = it },
            modifier = Modifier.fillMaxWidth(), label = { Text("Tamaño de la mascota") },
            placeholder = { Text("Pequeña, mediana o grande") }, singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (nombre.isBlank() || telefono.isBlank() || fecha.isBlank() ||
                        hora.isBlank() || raza.isBlank() || tamano.isBlank()
                    ) {
                        mostrarError = true
                    } else {
                        val resultado = db.insertar(
                            Cita(
                                nombre = nombre.trim(), telefono = telefono.trim(),
                                fecha = fecha, hora = hora, raza = raza.trim(), tamano = tamano.trim()
                            )
                        )
                        if (resultado != -1L) {
                            Toast.makeText(context, "Cita guardada correctamente", Toast.LENGTH_SHORT).show()
                            limpiarFormulario()
                            cargarCitas()
                        } else {
                            Toast.makeText(context, "No fue posible guardar la cita", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF717D45))
            ) { Text("Guardar") }

            Button(
                onClick = {
                    if (idSeleccionado == 0) {
                        Toast.makeText(context, "Seleccione una cita de la lista para editar", Toast.LENGTH_SHORT).show()
                    } else if (nombre.isBlank() || telefono.isBlank() || fecha.isBlank() ||
                        hora.isBlank() || raza.isBlank() || tamano.isBlank()
                    ) {
                        mostrarError = true
                    } else {
                        val filas = db.actualizar(
                            Cita(idSeleccionado, nombre.trim(), telefono.trim(), fecha, hora, raza.trim(), tamano.trim())
                        )
                        if (filas > 0) {
                            Toast.makeText(context, "Cita actualizada", Toast.LENGTH_SHORT).show()
                            limpiarFormulario()
                            cargarCitas()
                        } else {
                            Toast.makeText(context, "No se pudo actualizar la cita", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF503116))
            ) { Text("Editar") }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    if (idSeleccionado == 0) {
                        Toast.makeText(context, "Seleccione una cita de la lista para eliminar", Toast.LENGTH_SHORT).show()
                    } else {
                        mostrarConfirmacionEliminar = true
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Eliminar") }

            OutlinedButton(
                onClick = { cargarCitas() },
                modifier = Modifier.weight(1f)
            ) { Text("Consultar") }
        }

        OutlinedButton(
            onClick = { limpiarFormulario() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Limpiar formulario") }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Citas registradas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF503116))
        Text(
            if (consultaRealizada) "Registros encontrados: ${citas.size}"
            else "Pulse Consultar para ver los registros.",
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (citas.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "No hay citas registradas. Complete el formulario y pulse Guardar.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        } else {
            citas.forEach { cita ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                        .clickable { cargarCita(cita) },
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Text("Cita #${cita.id} · ${cita.nombre}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF503116))
                        Text("Teléfono: ${cita.telefono}")
                        Text("Fecha: ${cita.fecha}    Hora: ${cita.hora}")
                        Text("Mascota: ${cita.raza} · Tamaño: ${cita.tamano}")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Toque este registro para cargarlo en el formulario",
                            color = Color(0xFF717D45), fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Volver al inicio")
        }
    }

    if (mostrarError) {
        AlertDialog(
            onDismissRequest = { mostrarError = false },
            title = { Text("Datos incompletos") },
            text = { Text("Debe completar todos los campos antes de guardar o editar la cita.") },
            confirmButton = {
                TextButton(onClick = { mostrarError = false }) { Text("Aceptar") }
            }
        )
    }

    if (mostrarConfirmacionEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionEliminar = false },
            title = { Text("Eliminar cita") },
            text = { Text("¿Está seguro de que desea eliminar la cita número $idSeleccionado?") },
            confirmButton = {
                TextButton(onClick = {
                    val filas = db.eliminar(idSeleccionado)
                    mostrarConfirmacionEliminar = false
                    if (filas > 0) {
                        Toast.makeText(context, "Cita eliminada", Toast.LENGTH_SHORT).show()
                        limpiarFormulario()
                        cargarCitas()
                    } else {
                        Toast.makeText(context, "No se pudo eliminar la cita", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Sí, eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun PantallaServicios(onVolverInicio: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo_huellitas),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFEEDEC2).copy(alpha = 0.95f)))
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_huellitas),
                contentDescription = "Logo de la veterinaria",
                modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally).clip(RoundedCornerShape(15.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Nuestros servicios", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF503116),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Servicio("Consulta veterinaria", "Evaluación general y seguimiento de la salud de su mascota.")
            Servicio("Vacunación", "Aplicación y control del calendario de vacunas.")
            Servicio("Desparasitación", "Prevención y tratamiento de parásitos.")
            Servicio("Baño y peluquería", "Higiene, baño y cuidado estético de su mascota.")
            Servicio("Emergencias veterinarias", "Atención para situaciones que requieren asistencia inmediata.")
            Servicio("Cirugía", "Procedimientos quirúrgicos realizados por profesionales.")
            Servicio("Control de mascotas", "Seguimiento periódico para mantener una buena salud.")
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onVolverInicio,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF717D45))
            ) { Text("Volver al inicio", fontSize = 17.sp, color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun Servicio(titulo: String, descripcion: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF503116))
            Spacer(modifier = Modifier.height(5.dp))
            Text(descripcion, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Pantalla Inicio")
@Composable
fun PreviewPantallaInicio() {
    MaterialTheme {
        PantallaInicio(onAgendar = {}, onServicios = {}, onSalir = {})
    }
}