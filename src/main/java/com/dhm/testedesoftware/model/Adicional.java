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
 * @brief Class Adicional
 */
@Entity
@Table(name = "adicional")
@Getter
@Setter
@NoArgsConstructor
public class Adicional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    @Size(min = 1, max = 100, message = "O nome do adicional deve ter entre 1 e 100 caracteres.")
    private String nome;

    @Column(name = "descricao")
    @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres.")
    private String descricao;

    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    @Positive(message = "O preço do adicional deve ser maior que zero.")
    private BigDecimal preco;
}
