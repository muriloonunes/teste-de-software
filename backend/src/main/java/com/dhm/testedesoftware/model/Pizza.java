package com.dhm.testedesoftware.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @Column(name = "nome", nullable = false, length = 200)
    @Size(max = 200, message = "O nome da pizza não pode ter mais de 200 caracteres")
    private String name;

    @Column(name = "descricao", nullable = false)
    @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
    private String descricao;

    @Column(name = "categoria", nullable = false, length = 50)
    @Size(min = 1, max = 50, message = "A categoria deve ter entre 1 e 50 caracteres.")
    private String categoria;

    @Column(name = "preco_base", nullable = false, precision = 10, scale = 2)
    @Positive(message = "O preço da pizza deve ser maior que zero.")
    private BigDecimal valorBase;
}
