package com.example.torneo_participante

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate

class Participante(
    val id: Int,
    var nombre: String,
    var fechaNacimiento: LocalDate,
    var ranking: Double,
    var activo: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        LocalDate.parse(parcel.readString()),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(fechaNacimiento.toString())
        parcel.writeDouble(ranking)
        parcel.writeByte(if (activo) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Participante> {
        override fun createFromParcel(parcel: Parcel): Participante {
            return Participante(parcel)
        }

        override fun newArray(size: Int): Array<Participante?> {
            return arrayOfNulls(size)
        }
    }
}
