package com.dhm.testedesoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 18/09/2026
 * @brief Class Pizza
 */
@Entity
@Table(name = "pizza")
@Getter
@Setter
@NoArgsConstructor
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 80)
    private String name;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "preco_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBase;
}
