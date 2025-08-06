package com.vr.miniautorizador.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vr.miniautorizador.model.dto.TransacaoRequest;
import com.vr.miniautorizador.service.TransacaoService;
import com.vr.miniautorizador.exception.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransacaoService transacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String URL = "/transacoes";

    @Test
    void transacaoComSucesso_deveRetornar201() throws Exception {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(50));

        doNothing().when(transacaoService).realizar(request);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));
    }

    @Test
    void cartaoInexistente_deveRetornar422ComMensagem() throws Exception {
        TransacaoRequest request = new TransacaoRequest("0000000000000000", "1234", BigDecimal.valueOf(50));

        doThrow(new CartaoInexistenteException("Cartão não encontrado"))
                .when(transacaoService).realizar(any(TransacaoRequest.class));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("CARTAO_INEXISTENTE"));
    }

    @Test
    void senhaInvalida_deveRetornar422ComMensagem() throws Exception {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "9999", BigDecimal.valueOf(50));

        doThrow(new SenhaInvalidaException("Senha inválida"))
                .when(transacaoService).realizar(any(TransacaoRequest.class));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SENHA_INVALIDA"));
    }


    @Test
    void saldoInsuficiente_deveRetornar422ComMensagem() throws Exception {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(9999));

        doThrow(new SaldoInsuficienteException("Saldo insuficiente"))
                .when(transacaoService).realizar(any(TransacaoRequest.class));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SALDO_INSUFICIENTE"));
    }

}