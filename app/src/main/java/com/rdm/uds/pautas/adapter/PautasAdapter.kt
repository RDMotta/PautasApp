package com.rdm.uds.pautas.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.rdm.uds.pautas.PautasActivity
import com.rdm.uds.pautas.R
import com.rdm.uds.pautas.app.FirebaseUtils
import com.rdm.uds.pautas.model.PautasModel
import kotlinx.android.synthetic.main.frame_item_pauta.view.*


class PautasAdapter: FirestoreRecyclerAdapter<PautasModel, PautasAdapter.PautasAdapterHolder> {

    constructor (options: FirestoreRecyclerOptions<PautasModel>) : super(options) {
        mToken = FirebaseUtils.firebaseAuth.currentUser!!.uid
    }

    lateinit var mContext: Context
    lateinit var mToken: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PautasAdapterHolder {
        mContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pauta, null, false)
        return PautasAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: PautasAdapterHolder, position: Int, model: PautasModel) {
        holder.bind(model)
    }

    inner class PautasAdapterHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        fun bind(model: PautasModel) {
            with(itemView) {

                val id: String = getSnapshots().getSnapshot(position).getId()

                txtTitulo.text = (model.titulo)
                txtDescricao.text = (model.detalhe)

                setOnClickListener {
                    val intent = Intent(mContext, PautasActivity::class.java)
                    intent.putExtra(mContext.getString(R.string.param_pauta_detalhe_id), id)
                    intent.putExtra(mContext.getString(R.string.param_pauta_detalhe), model)
                    mContext.startActivity(intent)
                }
            }
        }
    }
}