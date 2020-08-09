package com.rdm.uds.pautas

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.LoaderManager
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rdm.uds.pautas.app.FirebaseUtils
import com.rdm.uds.pautas.controller.AccountController
import com.rdm.uds.pautas.model.IValidarLogin
import com.rdm.uds.pautas.model.OperacaoLogin
import kotlinx.android.synthetic.main.frame_cadastro.*
import java.util.ArrayList

class AddUserActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, IValidarLogin {

    override fun onDadosValidos(operacao: OperacaoLogin) {
        showProgress(false)
        if (operacao == OperacaoLogin.OK) {
            callMain()
        }
    }

    override fun onDadosInvalidos(operacao: OperacaoLogin) {
        showProgress(false)
        when (operacao){
             OperacaoLogin.USUARIO_NAO_AUTENTICADO ->{
                return
            }
            OperacaoLogin.NOME_INVALIDO ->{
                edtNome.requestFocus()
                edtNome.setError(getString(R.string.error_field_required))
                return
            }
            OperacaoLogin.EMAIL_INVALIDO -> {
                edtEmail.requestFocus()
                edtEmail.error = (getString(R.string.error_field_required))
                return
            }
            OperacaoLogin.SENHA_INVALIDA ->{
                edtSenha.requestFocus()
                edtSenha.error = (getString(R.string.error_field_required))
                return
            }
            OperacaoLogin.EMAIL_FORMATO_INVALIDO ->{
                edtEmail.text.clear()
                edtEmail.requestFocus()
                edtEmail.error = (getString(R.string.error_invalid_email))
                return
            }
            OperacaoLogin.SENHA_FORMATO_INVALIDO ->{
                edtSenha.text.clear()
                edtSenha.requestFocus()
                edtSenha.error = (getString(R.string.error_invalid_password))

                Toast.makeText(applicationContext, getString(R.string.error_validation_password), Toast.LENGTH_LONG).show()
                return
            }
            OperacaoLogin.REPETICAO_SENHA_DIFERENTE ->{
                edtConfirmarSenha.text.clear()
                edtConfirmarSenha.requestFocus()
                edtConfirmarSenha.error = (getString(R.string.error_invalid_password))
                return
            }
            OperacaoLogin.USUARIO_INVALIDO ->{
                edtEmail.requestFocus()
                Toast.makeText(applicationContext, getString(R.string.error_invalid_login), Toast.LENGTH_LONG).show()
                return
            }
            OperacaoLogin.ERRO_CADASTRAR_USUARIO ->{
                Toast.makeText(applicationContext, getString(R.string.error_create_account), Toast.LENGTH_LONG).show()
                return
            }
        }
    }

    override fun onResetPassword(idMessage: Int) {
        showProgress(false)
        Toast.makeText(applicationContext, idMessage, Toast.LENGTH_SHORT).show()
    }

    lateinit var mUsuariosDB: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        mUsuariosDB = FirebaseUtils.Reference.firebaseFirestore
        mAuth = FirebaseUtils.firebaseAuth
        setupLayout()
        setupControls()
    }

    fun setupLayout() {
        showProgress(false)
        btnVoltar.visibility = View.VISIBLE
        btnConfirmar.visibility = View.VISIBLE

        btnVoltar.setOnClickListener { voltarHandler() }
        btnConfirmar.setOnClickListener { novoCadastroHandler() }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(edtNome, InputMethodManager.RESULT_HIDDEN)
        imm.showSoftInput(edtEmail, InputMethodManager.RESULT_HIDDEN)
        imm.showSoftInput(edtSenha, InputMethodManager.RESULT_HIDDEN)
        imm.showSoftInput(edtConfirmarSenha, InputMethodManager.RESULT_HIDDEN)
    }

    fun setupControls() {
        carregarListaeAutoComplete()
    }

    fun callMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun voltarHandler() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun novoCadastroHandler() {
        val accountController = AccountController()
        accountController.name = edtNome.text.toString()
        accountController.email = edtEmail.text.toString()
        accountController.senha = edtSenha.text.toString()
        accountController.confirmaSenha = edtConfirmarSenha.text.toString()

        accountController.doAddNewUser( this, this)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            progress.visibility = if (show) View.VISIBLE else View.GONE
            progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            progress.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun solicitarAcessoAosContatos(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(edtEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok
                    ) { requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS) }
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                carregarListaeAutoComplete()
            }
        }
    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        val adapter = ArrayAdapter(this@AddUserActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        edtEmail.setAdapter(adapter)
    }

    private fun carregarListaeAutoComplete() {
        if (!solicitarAcessoAosContatos()) {
            return
        }
        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }
        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {}

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }

    companion object {
        private val REQUEST_READ_CONTACTS = 0
    }
}
