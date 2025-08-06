package com.vr.miniautorizador.exception;

public class TransacaoRepetidaException extends RuntimeException {

    public TransacaoRepetidaException(String mensagem) {
        super(mensagem);
    }

}
