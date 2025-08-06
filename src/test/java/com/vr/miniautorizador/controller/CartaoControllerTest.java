package com.vr.miniautorizador.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vr.miniautorizador.model.dto.CartaoRequest;
import com.vr.miniautorizador.model.dto.CartaoResponse;
import com.vr.miniautorizador.service.CartaoService;
import com.vr.miniautorizador.exception.CartaoInexistenteException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartaoController.class)
class CartaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartaoService cartaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criarCartao_deveRetornar201ComDados() throws Exception {
        CartaoRequest request = new CartaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100));
        CartaoResponse response = new CartaoResponse("1234567890123456", BigDecimal.valueOf(100));

        when(cartaoService.criar(any(CartaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("1234567890123456"))
                .andExpect(jsonPath("$.saldo").value(100));
    }

    @Test
    void consultarSaldo_deveRetornar200ComSaldo() throws Exception {
        when(cartaoService.consultarSaldo("1234567890123456")).thenReturn(BigDecimal.valueOf(250.50));

        mockMvc.perform(get("/cartoes/1234567890123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("250.5"));
    }

    @Test
    void consultarSaldo_cartaoInexistente_deveRetornar404() throws Exception {
        when(cartaoService.consultarSaldo("9999999999999999"))
                .thenThrow(new CartaoInexistenteException("Cartão não encontrado"));

        mockMvc.perform(get("/cartoes/9999999999999999"))
                .andExpect(status().isNotFound());
    }
}