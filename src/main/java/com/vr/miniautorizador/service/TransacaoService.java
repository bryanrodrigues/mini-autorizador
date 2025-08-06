package com.vr.miniautorizador.service;

import com.vr.miniautorizador.exception.CartaoInexistenteException;
import com.vr.miniautorizador.exception.SaldoInsuficienteException;
import com.vr.miniautorizador.exception.SenhaInvalidaException;
import com.vr.miniautorizador.exception.TransacaoRepetidaException;
import com.vr.miniautorizador.model.dto.TransacaoRequest;
import com.vr.miniautorizador.model.entity.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final CartaoRepository repository;
    private final StringRedisTemplate redisTemplate;

    private static final String TRANSACAO_KEY_PREFIX = "transacao:";
    private static final Duration TTL = Duration.ofSeconds(15);

    @Transactional
    public void realizar(TransacaoRequest request) {
        validarRequest(request);

        String chaveRedis = gerarChaveRedis(request);

        Boolean jaExecutada = redisTemplate.hasKey(chaveRedis);
        if (Boolean.TRUE.equals(jaExecutada)) {
            throw new TransacaoRepetidaException("Transação repetida detectada.");
        }

        Cartao cartao = repository.findByNumeroCartaoForUpdate(request.numeroCartao())
                .orElseThrow(() -> new CartaoInexistenteException("Cartão inexistente."));

        if (!Objects.equals(cartao.getSenha(), request.senha())) {
            throw new SenhaInvalidaException("Senha inválida.");
        }

        if (cartao.getSaldo() == null || cartao.getSaldo().compareTo(request.valor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }

        cartao.setSaldo(cartao.getSaldo().subtract(request.valor()));
        repository.save(cartao);

        redisTemplate.opsForValue().set(chaveRedis, "ok", TTL);
    }

    private String gerarChaveRedis(TransacaoRequest request) {
        return TRANSACAO_KEY_PREFIX + request.numeroCartao() + ":" + request.valor();
    }

    private void validarRequest(TransacaoRequest request) {
        if (request == null || request.valor() == null || request.numeroCartao() == null || request.senha() == null) {
            throw new IllegalArgumentException("Dados da transação inválidos.");
        }
    }
}
