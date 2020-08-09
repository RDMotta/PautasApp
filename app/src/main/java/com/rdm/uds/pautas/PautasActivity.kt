package com.rdm.uds.pautas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rdm.uds.pautas.app.FirebaseUtils
import com.rdm.uds.pautas.app.FirebaseUtils.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.rdm.uds.pautas.controller.PautasController
import com.rdm.uds.pautas.model.IOperacaoPautas
import com.rdm.uds.pautas.model.OperacaoPautas
import com.rdm.uds.pautas.model.PautasModel
import kotlinx.android.synthetic.main.frame_pauta.*
import kotlin.toString as toString

class PautasActivity : AppCompatActivity() , IOperacaoPautas {

    lateinit var mSettingsPautasDB: FirebaseFirestoreSettings
    lateinit var mPautasDB: FirebaseFirestore
    lateinit var mTokenUser: String
    lateinit var mAutor: String
    var mId: String? =""
    var mPautas: PautasModel? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pauta)
        setSupportActionBar(findViewById(R.id.toolbar))
        setConfigurationDB()
        btnSalvarPauta.setOnClickListener { cadastrarPauta() }
        btnSalvarPauta.setEnabled(false)
        setupData(savedInstanceState)

        edtTitulo.setOnFocusChangeListener  { view, b -> focusEvent(b) }
        edtDescPauta.setOnFocusChangeListener  { view, b -> focusEvent(b) }
        edtDetalhe.setOnFocusChangeListener  { view, b -> focusEvent(b) }
    }

    private fun focusEvent(b: Boolean) {
        if (edtTitulo.text.isNotEmpty() && edtDescPauta.text.isNotEmpty() && edtDetalhe.text.isNotEmpty()){
            btnSalvarPauta.setEnabled(true)
        } else {
            btnSalvarPauta.setEnabled(false)
        }
    }

    private fun setupData(savedInstanceState: Bundle?){
        mId = ""
        if (savedInstanceState?.getParcelable<PautasModel>("param_pauta_detalhe") != null) {
            mPautas = savedInstanceState?.getParcelable<PautasModel>("param_pauta_detalhe")!!
            mId = savedInstanceState?.getString("param_pauta_detalhe_id")!!
        } else {
            mPautas = intent.getParcelableExtra("param_pauta_detalhe")
            mId = intent.getStringExtra("param_pauta_detalhe_id")
        }
        txtAutor.text =mAutor

        if (mPautas != null){
            edtTitulo.setText(mPautas?.titulo)
            edtDescPauta.setText(mPautas?.detalhe)
            edtDetalhe.setText(mPautas?.detalhe)
            txtAutor.text = mPautas?.autor
            edtTitulo.setEnabled(false)
            edtDescPauta.setEnabled(false)
            edtDetalhe.setEnabled(false)
            btnSalvarPauta.setEnabled(true)
            edtTitulo.setOnFocusChangeListener(null)
            edtDescPauta.setOnFocusChangeListener(null)
            edtDetalhe.setOnFocusChangeListener(null)
            if (mPautas?.ativo!!){
                btnSalvarPauta.setText(getString(R.string.txt_finalizar))
                btnSalvarPauta.setOnClickListener { finalizarPauta() }
            } else {
                btnSalvarPauta.setText(getString(R.string.txt_abrir))
                btnSalvarPauta.setOnClickListener { reabrirPauta() }
            }
        }
    }

    private fun setConfigurationDB() {
        mSettingsPautasDB = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        val reference: FirebaseUtils = Reference.instance
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            mTokenUser =  user.uid
            mAutor = user.displayName.toString()
        }

        mPautasDB = reference.firebaseFirestore
        mPautasDB.firestoreSettings = mSettingsPautasDB
    }

    fun cadastrarPauta(){
        val controller = PautasController()
        controller.ativo = true
        controller.titulo = edtTitulo.text.toString()
        controller.detalhe = edtDetalhe.text.toString()
        controller.descricao = edtDescPauta.text.toString()
        controller.autor = txtAutor.text.toString()
        controller.id = ""
        controller.token = mTokenUser
        controller.salvarPauta(mPautasDB, this, this)
    }

    fun reabrirPauta(){
        val controller = PautasController()
        controller.ativo = true
        controller.titulo = edtTitulo.text.toString()
        controller.detalhe = edtDetalhe.text.toString()
        controller.descricao = edtDescPauta.text.toString()
        controller.autor = txtAutor.text.toString()
        controller.id = mId.toString()
        controller.token = mTokenUser
        controller.salvarPauta(mPautasDB, this, this)
    }

    fun finalizarPauta(){
        val controller = PautasController()
        controller.ativo = false
        controller.titulo = edtTitulo.text.toString()
        controller.detalhe = edtDetalhe.text.toString()
        controller.descricao = edtDescPauta.text.toString()
        controller.autor = txtAutor.text.toString()
        controller.id = mId.toString()
        controller.token = mTokenUser
        controller.salvarPauta(mPautasDB, this, this)
    }
    override fun onBackPressed() {
        voltar()
    }

    fun voltar(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDadosInvalidos(mensagem: Int) {
        Toast.makeText(applicationContext, mensagem, Toast.LENGTH_LONG).show()
    }

    override fun onDadosValidar(operacao: OperacaoPautas) {
        if (operacao == OperacaoPautas.TITULO_INVALIDO){
            edtTitulo.requestFocus()
            edtTitulo.setError(getString(R.string.error_field_required))
            return
        }
        if (operacao == OperacaoPautas.DESCRICAO_INVALIDO) {
            edtDescPauta.requestFocus()
            edtDescPauta.setError(getString(R.string.error_field_required))
            return
        }
        if (operacao == OperacaoPautas.DETALHE_INVALIDO){
            edtDetalhe.requestFocus()
            edtDetalhe.setError(getString(R.string.error_field_required))
            return
        }
    }

    override fun onDadosGravados() {
        voltar()
    }

}