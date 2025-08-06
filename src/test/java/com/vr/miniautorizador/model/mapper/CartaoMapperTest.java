package com.vr.miniautorizador.model.mapper;

import static org.junit.jupiter.api.Assertions.*;
import com.vr.miniautorizador.model.dto.CartaoRequest;
import com.vr.miniautorizador.model.dto.CartaoResponse;
import com.vr.miniautorizador.model.entity.Cartao;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;


class CartaoMapperTest {

    @Test
    void toEntity_deveMapearCorretamenteDeRequestParaCartao() {
        // Arrange
        CartaoRequest request = new CartaoRequest("1234567890123456", "1234", null);

        // Act
        Cartao cartao = CartaoMapper.toEntity(request);

        // Assert
        assertEquals("1234567890123456", cartao.getNumeroCartao());
        assertEquals("1234", cartao.getSenha());
        assertEquals(new BigDecimal("500.00"), cartao.getSaldo()); // saldo fixo no mapper
    }

    @Test
    void toResponse_deveMapearCorretamenteDeCartaoParaResponse() {
        // Arrange
        Cartao cartao = Cartao.builder()
                .numeroCartao("1234567890123456")
                .senha("1234")
                .saldo(new BigDecimal("300.50"))
                .build();

        // Act
        CartaoResponse response = CartaoMapper.toResponse(cartao);

        // Assert
        assertEquals("1234567890123456", response.numeroCartao());
        assertEquals(new BigDecimal("300.50"), response.saldo());
    }
}