package com.vr.miniautorizador.controller;

import com.vr.miniautorizador.exception.CartaoInexistenteException;
import com.vr.miniautorizador.exception.SaldoInsuficienteException;
import com.vr.miniautorizador.exception.SenhaInvalidaException;
import com.vr.miniautorizador.model.dto.TransacaoRequest;
import com.vr.miniautorizador.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Operações de transações de compra com cartão")
public class TransacaoController {

    private final TransacaoService service;

    @PostMapping
    @Operation(
            summary = "Realizar uma transação",
            description = "Efetua uma transação de débito com cartão informado",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransacaoRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transação realizada com sucesso"),
                    @ApiResponse(responseCode = "422", description = """
                Erro de negócio:
                - CARTAO_INEXISTENTE
                - SENHA_INVALIDA
                - SALDO_INSUFICIENTE
            """, content = @Content(schema = @Schema(example = "CARTAO_INEXISTENTE")))
            }
    )
    public ResponseEntity<String> transacionar(@RequestBody @Valid TransacaoRequest request) {
        try {
            service.realizar(request);
            return ResponseEntity.status(201).body("OK");
        } catch (CartaoInexistenteException e) {
            return ResponseEntity.unprocessableEntity().body("CARTAO_INEXISTENTE");
        } catch (SenhaInvalidaException e) {
            return ResponseEntity.unprocessableEntity().body("SENHA_INVALIDA");
        } catch (SaldoInsuficienteException e) {
            return ResponseEntity.unprocessableEntity().body("SALDO_INSUFICIENTE");
        }
    }
}