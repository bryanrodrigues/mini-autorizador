package com.vr.miniautorizador.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Requisição para realizar uma transação")
public record TransacaoRequest(

        @Schema(description = "Número do cartão", example = "6549873025634501")
        @NotBlank
        String numeroCartao,

        @Schema(description = "Senha do cartão", example = "1234")
        @NotBlank
        String senha,

        @Schema(description = "Valor da transação", example = "150.00", minimum = "0.01")
        @NotNull
        @DecimalMin("0.01")
        BigDecimal valor

) {}
