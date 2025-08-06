package com.vr.miniautorizador.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Schema(description = "Requisição para criação de um novo cartão")
public record CartaoRequest(

        @Schema(description = "Número do cartão", example = "6549873025634501")
        @NotBlank
        String numeroCartao,

        @Schema(description = "Senha do cartão", example = "1234")
        @NotBlank
        String senha,

        @Schema(description = "Saldo inicial do cartão", example = "500.00")
        BigDecimal saldo

) {}
