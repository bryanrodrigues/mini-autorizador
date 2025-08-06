package com.vr.miniautorizador.controller;

import com.vr.miniautorizador.exception.CartaoInexistenteException;
import com.vr.miniautorizador.model.dto.CartaoRequest;
import com.vr.miniautorizador.model.dto.CartaoResponse;
import com.vr.miniautorizador.service.CartaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
@Tag(name = "Cartões", description = "Operações relacionadas a cartões")
public class CartaoController {

    private final CartaoService service;

    @PostMapping
    @Operation(
            summary = "Criar um novo cartão",
            description = "Cria um novo cartão com número e senha informados",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CartaoRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso"),
                    @ApiResponse(responseCode = "422", description = "Cartão já existente", content = @Content)
            }
    )
    public ResponseEntity<CartaoResponse> criar(@RequestBody @Valid CartaoRequest request) {
        CartaoResponse response = service.criar(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{numeroCartao}")
    @Operation(
            summary = "Consultar saldo do cartão",
            description = "Retorna o saldo disponível do cartão",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content)
            }
    )
    public ResponseEntity<BigDecimal> consultar(@PathVariable String numeroCartao) {
        try {
            BigDecimal saldo = service.consultarSaldo(numeroCartao);
            return ResponseEntity.ok(saldo);
        } catch (CartaoInexistenteException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

