package com.example.torneo_participante

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

class SqliteHelperParticipante(
    contexto: Context?
): SQLiteOpenHelper(
    contexto,
    "participantes", // nombre del archivo sqlite
    null,
    1
) {

    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaParticipante =
            """
                CREATE TABLE PARTICIPANTE(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(100),
                    fechaNacimiento TEXT,
                    ranking REAL,
                    activo INTEGER
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaParticipante)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun crearParticipante(
        nombre: String,
        fechaNacimiento: LocalDate,
        ranking: Double,
        activo: Boolean
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("nombre", nombre)
        valoresGuardar.put("fechaNacimiento", fechaNacimiento.toString()) // Convertimos LocalDate a String
        valoresGuardar.put("ranking", ranking)
        valoresGuardar.put("activo", if (activo) 1 else 0) // Convertimos el Boolean a Integer

        val resultadoGuardar = baseDatosEscritura
            .insert(
                "PARTICIPANTE", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return resultadoGuardar != -1L
    }

    fun eliminarParticipante(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura
            .delete(
                "PARTICIPANTE", // tabla
                "id=?", // consulta
                parametrosConsultaDelete // parámetros
            )
        baseDatosEscritura.close()
        return resultadoEliminar > 0
    }

    fun actualizarParticipante(
        nombre: String,
        fechaNacimiento: LocalDate,
        ranking: Double,
        activo: Boolean,
        id: Int
    ): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresAActualizar = ContentValues()
        valoresAActualizar.put("nombre", nombre)
        valoresAActualizar.put("fechaNacimiento", fechaNacimiento.toString()) // Convertimos LocalDate a String
        valoresAActualizar.put("ranking", ranking)
        valoresAActualizar.put("activo", if (activo) 1 else 0) // Convertimos el Boolean a Integer

        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura
            .update(
                "PARTICIPANTE", // tabla
                valoresAActualizar, // valores
                "id=?", // id=?
                parametrosConsultaActualizar // [id]
            )
        baseDatosEscritura.close()
        return resultadoActualizar > 0
    }

    fun consultarParticipantePorId(id: Int): Participante? {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
            SELECT * FROM PARTICIPANTE WHERE id = ?
        """.trimIndent()
        val parametrosConsultaLectura = arrayOf(id.toString())
        val resultadoConsultaLectura = baseDatosLectura
            .rawQuery(scriptConsultaLectura, parametrosConsultaLectura)

        return if (resultadoConsultaLectura.moveToFirst()) {
            val participante = Participante(
                resultadoConsultaLectura.getInt(0), // id
                resultadoConsultaLectura.getString(1), // nombre
                LocalDate.parse(resultadoConsultaLectura.getString(2)), // fechaNacimiento
                resultadoConsultaLectura.getDouble(3), // ranking
                resultadoConsultaLectura.getInt(4) == 1 // activo
            )
            resultadoConsultaLectura.close()
            participante
        } else {
            resultadoConsultaLectura.close()
            null
        }
    }
    fun obtenerTodosLosParticipantes(): List<Participante> {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
        SELECT * FROM PARTICIPANTE
    """.trimIndent()
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(scriptConsultaLectura, null)

        val listaParticipantes = mutableListOf<Participante>()
        while (resultadoConsultaLectura.moveToNext()) {
            val participante = Participante(
                resultadoConsultaLectura.getInt(0), // id
                resultadoConsultaLectura.getString(1), // nombre
                LocalDate.parse(resultadoConsultaLectura.getString(2)), // fechaNacimiento
                resultadoConsultaLectura.getDouble(3), // ranking
                resultadoConsultaLectura.getInt(4) == 1 // activo
            )
            listaParticipantes.add(participante)
        }
        resultadoConsultaLectura.close()
        return listaParticipantes
    }

}
