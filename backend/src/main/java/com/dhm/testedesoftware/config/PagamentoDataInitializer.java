package com.dhm.testedesoftware.config;

import com.dhm.testedesoftware.model.Pagamento;
import com.dhm.testedesoftware.repository.PagamentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PagamentoDataInitializer {

    @Bean
    public CommandLineRunner inicializarPagamentos(PagamentoRepository pagamentoRepository) {
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
}
