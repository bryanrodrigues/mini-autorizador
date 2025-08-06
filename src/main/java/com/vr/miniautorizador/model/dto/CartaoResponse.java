package com.vr.miniautorizador.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resposta com os dados do cartão criado")
public record CartaoResponse(

        @Schema(description = "Número do cartão", example = "6549873025634501")
        String numeroCartao,

        @Schema(description = "Saldo atual do cartão", example = "500.00")
        BigDecimal saldo

) {}
