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
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CrudParticipante : AppCompatActivity() {

    // Función para mostrar el Snackbar
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
        setContentView(R.layout.activity_crud_participante)

        // Configuración para los márgenes de la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Botón para crear un nuevo participante
        val botonCrearBDD = findViewById<Button>(R.id.btn_crear_participante)
        botonCrearBDD.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.input_participante_nombre)
            val fechaNacimiento = findViewById<EditText>(R.id.input_participante_fecha_nacimiento)
            val ranking = findViewById<EditText>(R.id.input_participante_ranking)
            val activo = findViewById<CheckBox>(R.id.input_participante_activo)

            // Validar que el ranking es un número
            val rankingValue = ranking.text.toString().toDoubleOrNull()
            if (rankingValue == null) {
                mostrarSnackbar("Ranking inválido")
                return@setOnClickListener
            }

            // Crear un nuevo participante
            val respuesta = SqliteHelperParticipante(this).crearParticipante(
                nombre.text.toString(),
                LocalDate.parse(fechaNacimiento.text.toString()),
                rankingValue,
                activo.isChecked // Incluye el valor de "activo"
            )

            if (respuesta) {
                mostrarSnackbar("Participante creado")
                irActividad(ParticipanteListView::class.java)

            } else {
                mostrarSnackbar("Fallo al crear participante")
            }
        }

        // Botón para actualizar un participante
        val botonActualizarBDD = findViewById<Button>(R.id.btn_actualizar_participante)
        botonActualizarBDD.setOnClickListener {
            val id = findViewById<EditText>(R.id.input_participante_id)
            val nombre = findViewById<EditText>(R.id.input_participante_nombre)
            val fechaNacimiento = findViewById<EditText>(R.id.input_participante_fecha_nacimiento)
            val ranking = findViewById<EditText>(R.id.input_participante_ranking)
            val activo = findViewById<CheckBox>(R.id.input_participante_activo)

            // Validar que el ID es un número
            val participanteId = id.text.toString().toIntOrNull()
            if (participanteId == null) {
                mostrarSnackbar("ID inválido")
                return@setOnClickListener
            }

            // Convertir la fecha de nacimiento de string a LocalDate
            val fechaString = fechaNacimiento.text.toString()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fecha: LocalDate = try {
                LocalDate.parse(fechaString, formatter)
            } catch (e: DateTimeParseException) {
                mostrarSnackbar("Fecha inválida. Use el formato yyyy-MM-dd.")
                return@setOnClickListener
            }

            // Validar que el ranking es un número
            val rankingValue = ranking.text.toString().toDoubleOrNull()
            if (rankingValue == null) {
                mostrarSnackbar("Ranking inválido")
                return@setOnClickListener
            }

            // Actualizar un participante en la base de datos
            val respuesta = BaseDeDatos.tablaParticipante?.actualizarParticipante(
                nombre.text.toString(),
                fecha,
                rankingValue,
                activo.isChecked, // Incluir el estado "activo"
                participanteId
            )

            if (respuesta == true) {
                mostrarSnackbar("Participante actualizado")
                irActividad(ParticipanteListView::class.java)
            } else {
                mostrarSnackbar("Fallo al actualizar participante")
            }
        }
    }
}
