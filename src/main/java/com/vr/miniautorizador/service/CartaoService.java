package com.vr.miniautorizador.service;


import com.vr.miniautorizador.constants.Message;
import com.vr.miniautorizador.exception.CartaoInexistenteException;
import com.vr.miniautorizador.model.dto.CartaoRequest;
import com.vr.miniautorizador.model.dto.CartaoResponse;
import com.vr.miniautorizador.model.entity.Cartao;
import com.vr.miniautorizador.model.mapper.CartaoMapper;
import com.vr.miniautorizador.repository.CartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartaoService {

    private final CartaoRepository repository;

    public CartaoResponse criar(CartaoRequest request) {
        if (repository.existsById(request.numeroCartao())) {
            BigDecimal saldoAtual = consultarSaldo(request.numeroCartao());
            return new CartaoResponse(request.numeroCartao(), saldoAtual);
        }

        BigDecimal saldoInicial = verificarOutroSaldo(request);
        Cartao cartao = CartaoMapper.toEntity(request);
        cartao.setSaldo(saldoInicial);

        repository.save(cartao);

        return CartaoMapper.toResponse(cartao);
    }

    public BigDecimal consultarSaldo(String numeroCartao) {
        return repository.findById(numeroCartao)
                .map(Cartao::getSaldo)
                .orElseThrow(() -> new CartaoInexistenteException(Message.CARTAO_INEXISTENTE));
    }

    public Cartao getCartao(String numeroCartao) {
        return repository.findById(numeroCartao)
                .orElseThrow(() -> new CartaoInexistenteException(Message.CARTAO_INEXISTENTE));
    }

    private BigDecimal verificarOutroSaldo(CartaoRequest request) {
        return Optional.ofNullable(request.saldo())
                .orElse(BigDecimal.valueOf(500.00));
    }
}
