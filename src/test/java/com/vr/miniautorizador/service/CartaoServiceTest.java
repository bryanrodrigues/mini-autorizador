package com.vr.miniautorizador.service;

import com.vr.miniautorizador.exception.CartaoInexistenteException;
import com.vr.miniautorizador.model.dto.CartaoRequest;
import com.vr.miniautorizador.model.dto.CartaoResponse;
import com.vr.miniautorizador.model.entity.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartaoServiceTest {

    @Mock
    private CartaoRepository repository;

    @InjectMocks
    private CartaoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarCartao_novo_deveSalvarComSaldoPadrao() {
        // Arrange
        CartaoRequest request = new CartaoRequest("1234567890123456", "1234", null);

        when(repository.existsById("1234567890123456")).thenReturn(false);
        when(repository.save(any(Cartao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartaoResponse response = service.criar(request);

        // Assert
        assertEquals("1234567890123456", response.numeroCartao());
        assertEquals(0, response.saldo().compareTo(new BigDecimal("500.00")));

    }

    @Test
    void criarCartao_jaExiste_deveRetornarSaldoExistente() {
        // Arrange
        CartaoRequest request = new CartaoRequest("6549873025634501", "1234", null);

        when(repository.existsById("6549873025634501")).thenReturn(true);
        when(repository.findById("6549873025634501")).thenReturn(
                Optional.of(Cartao.builder()
                        .numeroCartao("6549873025634501")
                        .senha("1234")
                        .saldo(BigDecimal.valueOf(1000))
                        .build())
        );

        // Act
        CartaoResponse response = service.criar(request);

        // Assert
        assertEquals("6549873025634501", response.numeroCartao());
        assertEquals(BigDecimal.valueOf(1000), response.saldo());
    }

    @Test
    void consultarSaldo_cartaoExistente_deveRetornarSaldo() {
        // Arrange
        String numeroCartao = "1111222233334444";
        BigDecimal saldo = BigDecimal.valueOf(300.25);

        when(repository.findById(numeroCartao)).thenReturn(
                Optional.of(Cartao.builder()
                        .numeroCartao(numeroCartao)
                        .saldo(saldo)
                        .build())
        );

        // Act
        BigDecimal result = service.consultarSaldo(numeroCartao);

        // Assert
        assertEquals(saldo, result);
    }

    @Test
    void consultarSaldo_cartaoNaoExiste_deveLancarExcecao() {
        // Arrange
        String numeroCartao = "0000111122223333";
        when(repository.findById(numeroCartao)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartaoInexistenteException.class, () -> service.consultarSaldo(numeroCartao));
    }

    @Test
    void getCartao_cartaoExistente_deveRetornarEntidade() {
        // Arrange
        String numeroCartao = "9999888877776666";
        Cartao cartao = Cartao.builder()
                .numeroCartao(numeroCartao)
                .senha("1234")
                .saldo(BigDecimal.valueOf(400))
                .build();

        when(repository.findById(numeroCartao)).thenReturn(Optional.of(cartao));

        // Act
        Cartao result = service.getCartao(numeroCartao);

        // Assert
        assertEquals(cartao, result);
    }

    @Test
    void getCartao_cartaoInexistente_deveLancarExcecao() {
        when(repository.findById("naoexiste")).thenReturn(Optional.empty());
        assertThrows(CartaoInexistenteException.class, () -> service.getCartao("naoexiste"));
    }
}