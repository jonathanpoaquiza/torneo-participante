package com.example.torneo_participante

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate

class Torneo(
    val id: Int,
    val nombre: String,
    val fecha: LocalDate,
    val latitud: Double,   // Cambié 'lugar' por 'latitud'
    val longitud: Double,  // Cambié 'lugar' por 'longitud'
    val costoInscripcion: Double,
    val activo: Boolean,
   // val participantes: MutableList<Participante> // Cambié el tipo a MutableList<Participante> en lugar de String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        LocalDate.parse(parcel.readString()),
        parcel.readDouble(), // Leemos latitud
        parcel.readDouble(), // Leemos longitud
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
       /* mutableListOf<Participante>().apply {
            parcel.readList(this, Participante::class.java.classLoader) // Lee la lista de participantes correctamente
        }*/
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(fecha.toString())
        parcel.writeDouble(latitud)  // Escribimos latitud
        parcel.writeDouble(longitud) // Escribimos longitud
        parcel.writeDouble(costoInscripcion)
        parcel.writeByte(if (activo) 1 else 0)
        //parcel.writeList(participantes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Torneo> {
        override fun createFromParcel(parcel: Parcel): Torneo {
            return Torneo(parcel)
        }

        override fun newArray(size: Int): Array<Torneo?> {
            return arrayOfNulls(size)
        }
    }
}
