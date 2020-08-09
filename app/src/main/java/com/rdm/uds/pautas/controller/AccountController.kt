package com.rdm.uds.pautas.controller

import android.app.Activity
import android.text.TextUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.rdm.uds.pautas.R
import com.rdm.uds.pautas.app.FirebaseUtils
import com.rdm.uds.pautas.model.IValidarLogin
import com.rdm.uds.pautas.model.OperacaoLogin
import com.rdm.uds.pautas.model.UsuarioModel
import java.util.regex.Pattern

class AccountController {
    var name: String = ""
    var email: String = ""
    var senha: String = ""
    var confirmaSenha: String = ""

    init {

    }

    private fun isEmailValid(): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    private fun isPasswordValid(): Boolean {
        return Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}\$"
        ).matcher(senha).matches()
    }

    private fun isUserAuth(): Boolean {
        var mAuth: FirebaseAuth = FirebaseUtils.firebaseAuth
        return mAuth.currentUser != null
    }

    private fun isValidFields(): OperacaoLogin {

        return when {
            TextUtils.isEmpty(name) -> {
                OperacaoLogin.NOME_INVALIDO
            }
            TextUtils.isEmpty(email) -> {
                OperacaoLogin.EMAIL_INVALIDO
            }
            TextUtils.isEmpty(senha) -> {
                OperacaoLogin.SENHA_INVALIDA
            }
            !isEmailValid() -> {
                OperacaoLogin.EMAIL_FORMATO_INVALIDO
            }
            !isPasswordValid() -> {
                OperacaoLogin.SENHA_FORMATO_INVALIDO
            }
            confirmaSenha.isNotEmpty() && !confirmaSenha.contentEquals(senha) -> {
                OperacaoLogin.REPETICAO_SENHA_DIFERENTE
            }
            else -> {
                OperacaoLogin.OK
            }
        }
    }

    fun isValidEmail(): OperacaoLogin {
        return if (TextUtils.isEmpty(email)) {
            OperacaoLogin.EMAIL_INVALIDO
        } else if (!isEmailValid()) {
            OperacaoLogin.EMAIL_FORMATO_INVALIDO
        } else {
            OperacaoLogin.OK
        }
    }

    fun isValidPassword(): OperacaoLogin {
        return if (TextUtils.isEmpty(senha)) {
            OperacaoLogin.SENHA_INVALIDA
        } else if (!isPasswordValid()) {
            OperacaoLogin.SENHA_FORMATO_INVALIDO
        } else {
            OperacaoLogin.OK
        }
    }

    fun doLogin(validarLogin: IValidarLogin) {

        if (isUserAuth()) {
            validarLogin.onDadosInvalidos(OperacaoLogin.OK)
            return
        }

        val validacao = isValidEmail()
        if (validacao != OperacaoLogin.OK) {
            validarLogin.onDadosInvalidos(validacao)
            return
        }

        val validacaoSenha = isValidPassword()
        if (validacaoSenha != OperacaoLogin.OK) {
            validarLogin.onDadosInvalidos(validacaoSenha)
            return
        }

        var mAuth: FirebaseAuth = FirebaseUtils.firebaseAuth
        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    validarLogin.onDadosValidos(OperacaoLogin.OK)
                }
            }
        ).addOnFailureListener(
            OnFailureListener {
                validarLogin.onDadosInvalidos(OperacaoLogin.USUARIO_INVALIDO)
            }
        )

    }

    fun resetPassword(validarLogin: IValidarLogin) {

        val operacao = isValidEmail()
        if (operacao == OperacaoLogin.EMAIL_FORMATO_INVALIDO) {
            validarLogin.onResetPassword(R.string.error_invalid_email)
            return
        }
        if (operacao == OperacaoLogin.EMAIL_INVALIDO) {
            validarLogin.onResetPassword(R.string.error_field_email_required)
            return
        }

        val mAuth = FirebaseUtils.firebaseAuth

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    validarLogin.onResetPassword(R.string.txt_reset_password)
                } else {
                    validarLogin.onResetPassword(R.string.txt_reset_error_email)
                }
            }.addOnFailureListener { _ ->
                validarLogin.onResetPassword(R.string.txt_reset_error)
            }
    }

    fun doAddNewUser(activity: Activity, validarLogin: IValidarLogin) {
        val validacao = isValidFields()
        if (validacao != OperacaoLogin.OK) {
            validarLogin.onDadosInvalidos(validacao)
            return
        }

        var mAuth: FirebaseAuth = FirebaseUtils.firebaseAuth
        mAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(activity, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    val user = task.result.user
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                            validarLogin.onDadosValidos(OperacaoLogin.OK)
                        }

                    }
                }
            }).addOnFailureListener(
                OnFailureListener {
                    validarLogin.onDadosInvalidos(OperacaoLogin.ERRO_CADASTRAR_USUARIO)
                }
            )
    }

}