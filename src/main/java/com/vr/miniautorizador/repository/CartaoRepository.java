package com.vr.miniautorizador.repository;

import com.vr.miniautorizador.model.entity.Cartao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartaoRepository extends JpaRepository<Cartao, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cartao c WHERE c.numeroCartao = :numeroCartao")
    Optional<Cartao> buscarComLock(@Param("numeroCartao") String numeroCartao);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cartao c WHERE c.numeroCartao = :numeroCartao")
    Optional<Cartao> findByNumeroCartaoForUpdate(@Param("numeroCartao") String numeroCartao);


}