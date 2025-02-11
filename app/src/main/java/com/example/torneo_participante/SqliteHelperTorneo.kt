package com.example.torneo_participante

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

class SqliteHelperTorneo(
    contexto: Context?
): SQLiteOpenHelper(
    contexto,
    "torneos", // nombre del archivo sqlite
    null,
    1
) {

    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaTorneo =
            """
                CREATE TABLE TORNEO(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(100),
                    fecha TEXT,
                    latitud REAL,
                    longitud REAL,
                    costoInscripcion REAL,
                    activo INTEGER,
                    participantes TEXT
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaTorneo)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun crearTorneo(
        nombre: String,
        fecha: LocalDate,
        latitud: Double,
        longitud: Double,
        costoInscripcion: Double,
        activo: Boolean,
        participantes: String // Asegúrate de convertir los participantes a formato adecuado (como JSON o lista de IDs)
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("nombre", nombre)
        valoresGuardar.put("fecha", fecha.toString()) // Convertimos LocalDate a String
        valoresGuardar.put("latitud", latitud)
        valoresGuardar.put("longitud", longitud)
        valoresGuardar.put("costoInscripcion", costoInscripcion)
        valoresGuardar.put("activo", if (activo) 1 else 0) // Convertimos el Boolean a Integer
        valoresGuardar.put("participantes", participantes) // Aquí deberías serializar la lista de participantes si es necesario

        val resultadoGuardar = baseDatosEscritura
            .insert(
                "TORNEO", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun eliminarTorneo(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura
            .delete(
                "TORNEO", // tabla
                "id=?", // consulta
                parametrosConsultaDelete // parámetros
            )
        baseDatosEscritura.close()
        return resultadoEliminar > 0
    }

    fun actualizarTorneo(
        nombre: String,
        fecha: LocalDate,
        latitud: Double,
        longitud: Double,
        costoInscripcion: Double,
        activo: Boolean,
        id: Int
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("nombre", nombre)
        valoresAActualizar.put("fecha", fecha.toString()) // Convertimos LocalDate a String
        valoresAActualizar.put("latitud", latitud)
        valoresAActualizar.put("longitud", longitud)
        valoresAActualizar.put("costoInscripcion", costoInscripcion)
        valoresAActualizar.put("activo", if (activo) 1 else 0) // Convertimos el Boolean a Integer

        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura
            .update(
                "TORNEO", // tabla
                valoresAActualizar, // valores
                "id=?", // id=?
                parametrosConsultaActualizar // [id]
            )
        baseDatosEscritura.close()
        return resultadoActualizar > 0
    }

    fun consultarTorneoPorId(id: Int): Torneo? {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
            SELECT * FROM TORNEO WHERE id = ?
        """.trimIndent()
        val parametrosConsultaLectura = arrayOf(id.toString())
        val resultadoConsultaLectura = baseDatosLectura
            .rawQuery(scriptConsultaLectura, parametrosConsultaLectura)

        return if (resultadoConsultaLectura.moveToFirst()) {
            val torneo = Torneo(
                resultadoConsultaLectura.getInt(0), // id
                resultadoConsultaLectura.getString(1), // nombre
                LocalDate.parse(resultadoConsultaLectura.getString(2)), // fecha
                resultadoConsultaLectura.getDouble(3), // latitud
                resultadoConsultaLectura.getDouble(4), // longitud
                resultadoConsultaLectura.getDouble(5), // costoInscripcion
                resultadoConsultaLectura.getInt(6) == 1, // activo
            )
            resultadoConsultaLectura.close()
            torneo
        } else {
            resultadoConsultaLectura.close()
            null
        }
    }

    fun obtenerTodosLosTorneos(): List<Torneo> {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
        SELECT * FROM TORNEO
    """.trimIndent()
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)

        val listaTorneos = mutableListOf<Torneo>()
        while (resultadoConsultaLectura.moveToNext()) {
            val torneo = Torneo(
                resultadoConsultaLectura.getInt(0), // id
                resultadoConsultaLectura.getString(1), // nombre
                LocalDate.parse(resultadoConsultaLectura.getString(2)), // fecha
                resultadoConsultaLectura.getDouble(3), // latitud
                resultadoConsultaLectura.getDouble(4), // longitud
                resultadoConsultaLectura.getDouble(5), // costoInscripcion
                resultadoConsultaLectura.getInt(6) == 1, // activo
            )
            listaTorneos.add(torneo)
        }
        resultadoConsultaLectura.close()
        return listaTorneos
    }

}
