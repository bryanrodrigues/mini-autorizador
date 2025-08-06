package com.vr.miniautorizador.model.mapper;

import com.vr.miniautorizador.model.dto.CartaoRequest;
import com.vr.miniautorizador.model.dto.CartaoResponse;
import com.vr.miniautorizador.model.entity.Cartao;

import java.math.BigDecimal;

public class CartaoMapper {

    public static Cartao toEntity(CartaoRequest request) {
        return Cartao.builder()
                .numeroCartao(request.numeroCartao())
                .senha(request.senha())
                .saldo(new BigDecimal("500.00"))
                .build();
    }

    public static CartaoResponse toResponse(Cartao cartao) {
        return new CartaoResponse(cartao.getNumeroCartao(), cartao.getSaldo());
    }
}