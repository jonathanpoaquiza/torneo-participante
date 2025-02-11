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

class TorneoListView : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listaTorneos = mutableListOf<Torneo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_torneo_list_view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.lv_torneos)
        val botonAnadirTorneo = findViewById<Button>(R.id.btn_anadir_torneo)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTorneos.map { it.nombre })
        listView.adapter = adapter

        registerForContextMenu(listView)

        cargarDatosDesdeBaseDeDatos()

        botonAnadirTorneo.setOnClickListener {
            irActividad(CrudTorneo::class.java) // Pasa el requestCode 1
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
        inflater.inflate(R.menu.menu, menu)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val posicion = info.position
        posicionItemSeleccionado = posicion
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarDatosDesdeBaseDeDatos() // Refresca la lista
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_actualizar -> {
                val torneoSeleccionado = listaTorneos[posicionItemSeleccionado]
                irActividad(CrudTorneo::class.java, torneoSeleccionado)
                return true
            }
            R.id.mi_eliminar -> {
                abrirDialogo()
                return true
            }
            R.id.mis_participantes -> {
                val torneoSeleccionado = listaTorneos[posicionItemSeleccionado]
                irActividad(ParticipanteListView::class.java, torneoSeleccionado)
                return true
            }
            R.id.mi_mapa -> {
                val torneoSeleccionado = listaTorneos[posicionItemSeleccionado]
                irActividad(MapaTorneo::class.java, torneoSeleccionado)
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
                val torneoSeleccionado = listaTorneos[posicionItemSeleccionado]
                val id = torneoSeleccionado.id

                // Llamar al método de eliminación
                val eliminado = BaseDeDatos.tablaTorneo?.eliminarTorneo(id)
                if (eliminado == true) {
                    mostrarSnackbar("Torneo eliminado correctamente.")
                    cargarDatosDesdeBaseDeDatos() // Refrescar la lista
                } else {
                    mostrarSnackbar("Error al eliminar el torneo.")
                }
            }
        )
        builder.setNegativeButton("Cancelar", null)
        val dialogo = builder.create()
        dialogo.show()
    }

    fun cargarDatosDesdeBaseDeDatos() {
        val torneos = BaseDeDatos.tablaTorneo?.obtenerTodosLosTorneos()
        listaTorneos.clear()
        if (torneos != null) {
            listaTorneos.addAll(torneos)
        }
        adapter.clear()
        adapter.addAll(listaTorneos.map { torneo -> // Cambia 'it' a 'torneo' para mayor claridad
            val activoFormateado = if (torneo.activo) "Activo" else "Finalizado" // Estado para *este* torneo
            "${torneo.id} - ${torneo.nombre} - ${activoFormateado} - ${torneo.fecha} - ${torneo.costoInscripcion}"
        })
        adapter.notifyDataSetChanged()
    }

    fun irActividad(clase: Class<*>, torneo: Torneo? = null) {
        val intentExplicito = Intent(this, clase)
        if (torneo != null) {
            intentExplicito.putExtra("modo", "editar")
            intentExplicito.putExtra("torneo", torneo)
        } else {
            intentExplicito.putExtra("modo", "crear")
        }
        startActivityForResult(intentExplicito, 1)
    }
}
