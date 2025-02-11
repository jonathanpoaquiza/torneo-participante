package com.example.torneo_participante

class BaseDeDatos {
    companion object {
        // Instancia de los helpers para cada tabla
        var tablaTorneo: SqliteHelperTorneo? = null
        var tablaParticipante: SqliteHelperParticipante? = null
    }
}