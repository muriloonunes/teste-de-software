package com.dhm.testedesoftware.config;

import com.dhm.testedesoftware.model.Adicional;
import com.dhm.testedesoftware.model.Pagamento;
import com.dhm.testedesoftware.model.Pizza;
import com.dhm.testedesoftware.repository.AdicionalRepository;
import com.dhm.testedesoftware.repository.PagamentoRepository;
import com.dhm.testedesoftware.repository.PizzaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner inicializarDados(
            PagamentoRepository pagamentoRepository,
            PizzaRepository pizzaRepository,
            AdicionalRepository adicionalRepository
    ) {
        return _ -> {
            if (pagamentoRepository.count() == 0) {
                pagamentoRepository.saveAll(List.of(
                        novoPagamento("Pix", "PIX", 0.0, 1),
                        novoPagamento("Dinheiro", "DINHEIRO", 0.0, 1),
                        novoPagamento("Cartão de Débito", "DEBITO", 1.2, 1),
                        novoPagamento("Cartão de Crédito à vista (1x)", "CREDITO", 2.5, 1),
                        novoPagamento("Cartão de Crédito (2x)", "CREDITO", 3.5, 2),
                        novoPagamento("Cartão de Crédito (3x)", "CREDITO", 5.0, 3)
                ));
            }

            if (pizzaRepository.count() == 0) {
                pizzaRepository.saveAll(List.of(
                        novaPizza(
                                "Calabresa Artesanal",
                                "Molho de tomate pelati, mussarela, calabresa curada fatiada, cebola roxa e orégano",
                                "Tradicionais",
                                "39.90"
                        ),
                        novaPizza(
                                "Margherita Verace",
                                "Molho de tomate italiano, mussarela de búfala, manjericão fresco e azeite extravirgem",
                                "Tradicionais",
                                "42.90"
                        ),
                        novaPizza(
                                "Frango com Catupiry",
                                "Molho de tomate artesanal, peito de frango desfiado temperado, Catupiry original e milho",
                                "Tradicionais",
                                "44.90"
                        ),
                        novaPizza(
                                "Quatro Queijos",
                                "Molho de tomate, mussarela, provolone defumado, parmesão maturado e gorgonzola",
                                "Especiais",
                                "48.90"
                        ),
                        novaPizza(
                                "Pepperoni Supreme",
                                "Molho de tomate picante suave, dupla camada de mussarela e pepperoni importado crocante",
                                "Especiais",
                                "49.90"
                        ),
                        novaPizza(
                                "Chocolate com Morango",
                                "Ganache de chocolate meio amargo, morangos frescos fatiados e raspas de chocolate belga",
                                "Doces",
                                "45.90"
                        )
                ));
            }

            if (adicionalRepository.count() == 0) {
                adicionalRepository.saveAll(List.of(
                        novoAdicional("Coca-Cola 2L", "Refrigerante garrafa 2 litros bem gelada", "14.00"),
                        novoAdicional("Coca-Cola Lata 350ml", "Refrigerante lata 350ml individual", "6.50"),
                        novoAdicional("Guaraná Antarctica 2L", "Refrigerante de guaraná garrafa 2 litros", "12.50"),
                        novoAdicional("Suco de Laranja Natural 500ml", "Suco da fruta feito na hora", "10.00"),
                        novoAdicional("Água Mineral com Gás 500ml", "Garrafa individual 500ml", "5.00"),
                        novoAdicional("Borda Recheada de Catupiry", "Borda recheada com Catupiry original", "12.00"),
                        novoAdicional("Extra de Bacon Crocante", "Porção extra de bacon (100g)", "8.00")
                ));
            }
        };
    }

    private Pagamento novoPagamento(String nome, String tipoPagamento, Double taxa, int parcelas) {
        Pagamento pagamento = new Pagamento();
        pagamento.setNome(nome);
        pagamento.setTipoPagamento(tipoPagamento);
        pagamento.setTaxa(taxa);
        pagamento.setParcelas(parcelas);
        return pagamento;
    }

    private Pizza novaPizza(String nome, String descricao, String categoria, String valorBase) {
        Pizza pizza = new Pizza();
        pizza.setName(nome);
        pizza.setDescricao(descricao);
        pizza.setCategoria(categoria);
        pizza.setValorBase(new BigDecimal(valorBase));
        return pizza;
    }

    private Adicional novoAdicional(String nome, String descricao, String preco) {
        Adicional adicional = new Adicional();
        adicional.setNome(nome);
        adicional.setDescricao(descricao);
        adicional.setPreco(new BigDecimal(preco));
        return adicional;
    }
}
