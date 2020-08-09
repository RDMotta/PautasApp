package com.rdm.uds.pautas.model

interface IValidarLogin {

    fun onDadosValidos(operacao: OperacaoLogin)
    fun onDadosInvalidos(operacao: OperacaoLogin)
    fun onResetPassword(idMessage: Int)
}