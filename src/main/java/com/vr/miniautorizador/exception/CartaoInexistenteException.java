package com.vr.miniautorizador.exception;

public class CartaoInexistenteException extends RuntimeException {

    public CartaoInexistenteException(String mensagem) {
        super(mensagem);
    }

}
