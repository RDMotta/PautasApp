package com.rdm.uds.pautas.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*


class PautasModel : Parcelable {

    var ativo: Boolean? = false
    var titulo: String? = null
    var detalhe: String? = null
    var descricao: String? = null
    var autor: String?  = null
    var token: String?  = null

    constructor(parcel: Parcel) : this() {
        ativo = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        titulo = parcel.readString()
        detalhe = parcel.readString()
        descricao = parcel.readString()
        autor = parcel.readString()
        token = parcel.readString()
    }

    constructor()

    constructor(ativo: Boolean?, titulo: String?, detalhe: String?,descricao: String?, autor: String?, token: String?) {
        this.ativo = ativo
        this.titulo = titulo
        this.detalhe = detalhe
        this.descricao= descricao
        this.autor= autor
        this.token= token
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ativo)
        parcel.writeString(titulo)
        parcel.writeString(detalhe)
        parcel.writeString(descricao)
        parcel.writeString(autor)
        parcel.writeString(token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PautasModel> {
        override fun createFromParcel(parcel: Parcel): PautasModel {
            return PautasModel(parcel)
        }

        override fun newArray(size: Int): Array<PautasModel?> {
            return arrayOfNulls(size)
        }
    }
}