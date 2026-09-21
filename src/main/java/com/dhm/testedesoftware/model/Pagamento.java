package com.dhm.testedesoftware.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 100, message = "O nome do pagamento deve ter entre 1 e 100 caracteres.")
    private String nome;

    @Column(name = "taxa", nullable = false)
    private Double taxa;

    @Column(name = "tipo", nullable = false)
    @Size(min = 1, max = 100, message = "O tipo de pagamento deve ter entre 1 e 100 caracteres.")
    private String tipoPagamento;

    @Column(name = "parcelas", nullable = false)
    @Max(value = 12, message = "O número máximo de parcelas é 12.")
    private int parcelas;
}
