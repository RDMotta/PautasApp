package com.rdm.uds.pautas.controller

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rdm.uds.pautas.PautasActivity
import com.rdm.uds.pautas.R
import com.rdm.uds.pautas.adapter.PautasAdapter
import com.rdm.uds.pautas.app.FirebaseUtils
import com.rdm.uds.pautas.model.IOperacaoPautas
import com.rdm.uds.pautas.model.OperacaoPautas
import com.rdm.uds.pautas.model.PautasModel
import kotlinx.android.synthetic.main.frame_pauta.*

class PautasController {

    constructor()

    init {

    }

    var id: String = ""
    var ativo: Boolean = false
    var titulo: String = ""
    var detalhe: String = ""
    var descricao: String = ""
    var autor: String = ""
    var token: String = ""

    private fun isValidFields(): OperacaoPautas {

        return when {
            titulo.isEmpty() -> {
                OperacaoPautas.TITULO_INVALIDO
            }
            detalhe.isEmpty() -> {
                OperacaoPautas.DETALHE_INVALIDO
            }
            descricao.isEmpty() -> {
                OperacaoPautas.DESCRICAO_INVALIDO
            }
            else -> {
                OperacaoPautas.OK
            }
        }
    }

    fun consultarPautas(
        token: String, pautasReference: FirebaseFirestore,
        context: Context, recyclerView: RecyclerView
    ) {
        val table_pautas = context.getString(R.string.table_pautas)
        recyclerView.adapter = null

        val pautasReferencia = pautasReference.collection(table_pautas)
            .whereEqualTo(context.getString(R.string.table_pautas_ativo), true)
            .whereEqualTo(context.getString(R.string.table_pautas_dono), token)
            .limit(50)

        val options = FirestoreRecyclerOptions.Builder<PautasModel>()
            .setQuery(pautasReferencia, PautasModel::class.java)
            .build()

        var pautasAdapter = PautasAdapter(options)
        pautasAdapter.startListening()
        recyclerView.adapter = pautasAdapter
    }


    fun consultarPautasFinalizadas(
        token: String, pautasReference: FirebaseFirestore,
        context: Context, recyclerView: RecyclerView
    ) {
        val table_pautas = context.getString(R.string.table_pautas)
        recyclerView.adapter = null

        val pautasReferencia = pautasReference.collection(table_pautas)
            .whereEqualTo(context.getString(R.string.table_pautas_ativo), false)
            .whereEqualTo(context.getString(R.string.table_pautas_dono), token)
            .limit(50)

        val options = FirestoreRecyclerOptions.Builder<PautasModel>()
            .setQuery(pautasReferencia, PautasModel::class.java)
            .build()

        var pautasAdapter = PautasAdapter(options)
        pautasAdapter.startListening()
        recyclerView.adapter = pautasAdapter
    }

    private fun editarPauta(
        collection: String,
        pautasReference: FirebaseFirestore
    ) {
        val model = PautasModel(
            this.ativo, this.titulo,
            this.detalhe, this.descricao, this.autor, this.token
        )
        pautasReference.collection(collection).document(this.id)?.set(model, SetOptions.merge())
    }

    fun salvarPauta(
        pautasReference: FirebaseFirestore,
        context: Context, operacaoPautas: IOperacaoPautas
    ) {
        val validacao = isValidFields()
        if (validacao != OperacaoPautas.OK) {
            operacaoPautas.onDadosValidar(validacao)
            return
        }

        val user = FirebaseUtils.firebaseAuth.currentUser
        val table_pautas = context.getString(R.string.table_pautas)

        if (id.isNotEmpty()) {
            editarPauta(table_pautas, pautasReference)
            operacaoPautas.onDadosGravados();
            return
        }
        val model = PautasModel(
            this.ativo, this.titulo,
            this.detalhe, this.descricao, this.autor, this.token
        )
        pautasReference.collection(table_pautas)
            .add(model)
            .addOnSuccessListener {
                operacaoPautas.onDadosGravados();
            }
            .addOnFailureListener {
                operacaoPautas.onDadosInvalidos(R.string.txt_erro_add_pauta)
            }
    }

}