package com.example.torneo_participante

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class ParticipanteListView : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listaParticipantes = mutableListOf<Participante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_participante_list_view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.lv_participantes)
        val botonAnadirParticipante = findViewById<Button>(R.id.btn_anadir_participante)

        // Adaptador para mostrar los participantes en la lista
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaParticipantes.map { it.nombre })
        listView.adapter = adapter

        // Registrar el menú contextual
        registerForContextMenu(listView)

        cargarDatosDesdeBaseDeDatos()

        botonAnadirParticipante.setOnClickListener {
            irActividad(CrudParticipante::class.java) // Abre la actividad para agregar un participante
        }
    }

    var posicionItemSeleccionado = -1 // VARIABLE GLOBAL
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu2, menu)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarDatosDesdeBaseDeDatos() // Refresca la lista después de agregar un participante
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_actualizar2 -> {
                val participanteSeleccionado = listaParticipantes[posicionItemSeleccionado]
                irActividad(CrudParticipante::class.java, participanteSeleccionado)
                return true
            }
            R.id.mi_eliminar2 -> {
                abrirDialogo()
                return true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Cerrar") { snack.dismiss() }
        snack.show()
    }

    fun abrirDialogo() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Desea Eliminar?")
        builder.setPositiveButton(
            "Aceptar",
            DialogInterface.OnClickListener { dialog, which ->
                val participanteSeleccionado = listaParticipantes[posicionItemSeleccionado]
                val id = participanteSeleccionado.id

                // Llamar al método de eliminación
                val eliminado = BaseDeDatos.tablaParticipante?.eliminarParticipante(id)
                if (eliminado == true) {
                    mostrarSnackbar("Participante eliminado correctamente.")
                    cargarDatosDesdeBaseDeDatos() // Refrescar la lista
                } else {
                    mostrarSnackbar("Error al eliminar el participante.")
                }
            }
        )
        builder.setNegativeButton("Cancelar", null)
        val dialogo = builder.create()
        dialogo.show()
    }

    fun cargarDatosDesdeBaseDeDatos() {
        val participantes = BaseDeDatos.tablaParticipante?.obtenerTodosLosParticipantes()
        listaParticipantes.clear()
        if (participantes != null) {
            listaParticipantes.addAll(participantes)
        }
        adapter.clear()
        adapter.addAll(listaParticipantes.map { participante-> // Cambia 'it' a 'torneo' para mayor claridad
            val activoFormateado = if (participante.activo) "Activo" else "Retirado" // Estado para *este* torneo
            "${participante.id} - ${participante.nombre} - ${activoFormateado} - ${participante.fechaNacimiento} - ${participante.ranking}"
        })
        adapter.notifyDataSetChanged()
    }

    fun irActividad(clase: Class<*>, participante: Participante? = null) {
        val intentExplicito = Intent(this, clase)
        if (participante != null) {
            intentExplicito.putExtra("modo", "editar")
            intentExplicito.putExtra("participante", participante)
        } else {
            intentExplicito.putExtra("modo", "crear")
        }
        startActivityForResult(intentExplicito, 1)
    }
}
