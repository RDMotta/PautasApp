package com.rdm.uds.pautas.model

interface IOperacaoPautas {
    fun onDadosInvalidos(mensagem: Int)
    fun onDadosValidar(operacao: OperacaoPautas)
    fun onDadosGravados()
}