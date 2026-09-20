package com.dhm.testedesoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 18/09/2026
 * @brief Class Pagamento
 */
@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "taxa", nullable = false)
    private Double taxa;

    @Column(name = "tipo", nullable = false)
    private String tipoPagamento;

    @Column(name = "parcelas", nullable = false)
    private int parcelas;
}
