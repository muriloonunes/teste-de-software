package com.dhm.testedesoftware.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 18/09/2026
 * @brief Class Pedido
 */
@Entity
@Table(name = "item_pedido")
@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pizza_id", nullable = false)
    private Pizza pizza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TamanhoPizza tamanho;

    @Column(name = "quantidade", nullable = false)
    @Min(value = 1, message = "A quantidade deve ser maior que zero.")
    private Integer quantidade = 1;

    @Column(name = "valor_item", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPedido;
}
