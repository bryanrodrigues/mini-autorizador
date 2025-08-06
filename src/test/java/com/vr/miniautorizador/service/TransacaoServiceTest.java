package com.vr.miniautorizador.service;

import com.vr.miniautorizador.exception.*;
import com.vr.miniautorizador.model.dto.TransacaoRequest;
import com.vr.miniautorizador.model.entity.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransacaoServiceTest {

    @Mock private CartaoRepository repository;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;

    @InjectMocks private TransacaoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void realizarTransacao_sucesso() {
        // Arrange
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(50));
        Cartao cartao = Cartao.builder()
                .numeroCartao("1234567890123456")
                .senha("1234")
                .saldo(BigDecimal.valueOf(100))
                .build();

        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(repository.findByNumeroCartaoForUpdate("1234567890123456")).thenReturn(Optional.of(cartao));
        when(repository.save(any(Cartao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        assertDoesNotThrow(() -> service.realizar(request));

        // Assert
        assertEquals(BigDecimal.valueOf(50), cartao.getSaldo());
        verify(redisTemplate.opsForValue()).set(contains("transacao:"), eq("ok"), any());
    }

    @Test
    void realizarTransacao_repetida_deveLancarExcecao() {
        TransacaoRequest request = new TransacaoRequest("123", "1234", BigDecimal.TEN);
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        assertThrows(TransacaoRepetidaException.class, () -> service.realizar(request));
    }

    @Test
    void realizarTransacao_cartaoInexistente_deveLancarExcecao() {
        TransacaoRequest request = new TransacaoRequest("999", "senha", BigDecimal.TEN);

        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(repository.findByNumeroCartaoForUpdate("999")).thenReturn(Optional.empty());

        assertThrows(CartaoInexistenteException.class, () -> service.realizar(request));
    }

    @Test
    void realizarTransacao_senhaInvalida_deveLancarExcecao() {
        TransacaoRequest request = new TransacaoRequest("123", "errada", BigDecimal.TEN);
        Cartao cartao = Cartao.builder()
                .numeroCartao("123")
                .senha("correta")
                .saldo(BigDecimal.valueOf(100))
                .build();

        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(repository.findByNumeroCartaoForUpdate("123")).thenReturn(Optional.of(cartao));

        assertThrows(SenhaInvalidaException.class, () -> service.realizar(request));
    }

    @Test
    void realizarTransacao_saldoInsuficiente_deveLancarExcecao() {
        TransacaoRequest request = new TransacaoRequest("123", "1234", BigDecimal.valueOf(200));
        Cartao cartao = Cartao.builder()
                .numeroCartao("123")
                .senha("1234")
                .saldo(BigDecimal.valueOf(100))
                .build();

        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(repository.findByNumeroCartaoForUpdate("123")).thenReturn(Optional.of(cartao));

        assertThrows(SaldoInsuficienteException.class, () -> service.realizar(request));
    }

    @Test
    void realizarTransacao_requestInvalido_deveLancarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.realizar(null));
        assertThrows(IllegalArgumentException.class, () -> service.realizar(new TransacaoRequest(null, null, null)));
    }
}