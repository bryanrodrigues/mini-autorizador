package com.vr.miniautorizador.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cartao {

    @Id
    private String numeroCartao;

    private String senha;

    private BigDecimal saldo;

    @Version
    private Long versao;

}