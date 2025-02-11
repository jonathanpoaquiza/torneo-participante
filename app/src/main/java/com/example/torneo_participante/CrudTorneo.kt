package com.example.torneo_participante

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate

class CrudTorneo : AppCompatActivity() {

    fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.show()
    }
    fun irActividad(
        clase: Class<*>
    ){
        val intentExplicito = Intent(
            this,
            clase
        )
        startActivity(intentExplicito)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crud_torneo)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botonCrearBDD = findViewById<Button>(R.id.btn_crear_torneo)
        botonCrearBDD.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.input_torneo_nombre)
            val fecha = findViewById<EditText>(R.id.input_torneo_fecha)
            val latitud = findViewById<EditText>(R.id.input_torneo_latitud) // Campo para latitud
            val longitud = findViewById<EditText>(R.id.input_torneo_longitud) // Campo para longitud
            val costoInscripcion = findViewById<EditText>(R.id.input_torneo_costo) // Campo para el costo de inscripción
            val activo = findViewById<CheckBox>(R.id.input_torneo_activo)
            val participantes = "" // Aquí deberías manejar los participantes, por ahora está vacío.

            // Asegúrate de que la latitud y longitud sean números válidos
            val lat = latitud.text.toString().toDoubleOrNull()
            val lon = longitud.text.toString().toDoubleOrNull()

            if (lat != null && lon != null) {
                val respuesta = SqliteHelperTorneo(this).crearTorneo(
                    nombre.text.toString(),
                    LocalDate.parse(fecha.text.toString()),
                    lat,  // Usar latitud
                    lon,  // Usar longitud
                    costoInscripcion.text.toString().toDouble(),
                    activo.isChecked,
                    participantes
                )

                if (respuesta) {
                    mostrarSnackbar("Torneo creado")
                    irActividad(TorneoListView::class.java)
                }else {mostrarSnackbar("Fallo")}
            } else {
                mostrarSnackbar("Por favor ingresa coordenadas válidas de latitud y longitud")
            }
        }

        val botonActualizarBDD = findViewById<Button>(R.id.btn_actualizar_torneo)
        botonActualizarBDD.setOnClickListener {
            val id = findViewById<EditText>(R.id.input_torneo_id)
            val nombre = findViewById<EditText>(R.id.input_torneo_nombre)
            val fecha = findViewById<EditText>(R.id.input_torneo_fecha)
            val latitud = findViewById<EditText>(R.id.input_torneo_latitud) // Campo para latitud
            val longitud = findViewById<EditText>(R.id.input_torneo_longitud) // Campo para longitud
            val costoInscripcion = findViewById<EditText>(R.id.input_torneo_costo)
            val activo = findViewById<CheckBox>(R.id.input_torneo_activo)
            val participantes = "" // Aquí deberías manejar los participantes.

            // Asegúrate de que la latitud y longitud sean números válidos
            val lat = latitud.text.toString().toDoubleOrNull()
            val lon = longitud.text.toString().toDoubleOrNull()

            if (lat != null && lon != null) {
                val respuesta = SqliteHelperTorneo(this).actualizarTorneo(
                    nombre.text.toString(),
                    LocalDate.parse(fecha.text.toString()),
                    lat,  // Usar latitud
                    lon,  // Usar longitud
                    costoInscripcion.text.toString().toDouble(),
                    activo.isChecked,
                    id.text.toString().toInt()
                )

                if (respuesta) {
                    mostrarSnackbar("Torneo actualizado")
                    irActividad(TorneoListView::class.java)
                }else {mostrarSnackbar("Fallo")}
            } else {
                mostrarSnackbar("Por favor ingresa coordenadas válidas de latitud y longitud")
            }

        }
    }
}
