package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.model.ItemAdicional;
import com.dhm.testedesoftware.model.ItemPedido;
import com.dhm.testedesoftware.model.Venda;
import com.dhm.testedesoftware.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;

    public VendaService(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }

    public Venda salvar(Venda venda) {
        venda.setValor(calcularTotal(venda));
        return vendaRepository.save(venda);
    }

    public List<Venda> listar() {
        return vendaRepository.findAll();
    }

    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.findById(id);
    }

    public void excluir(Long id) {
        vendaRepository.deleteById(id);
    }

    public BigDecimal calcularTotal(Venda venda) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : venda.getPedidos()) {
            BigDecimal valorPizza = item.getPizza().getValorBase()
                    .multiply(BigDecimal.valueOf(item.getTamanho().getMultiplicadorTamanho()))
                    .multiply(BigDecimal.valueOf(item.getQuantidade()));
            valorPizza = valorPizza.setScale(2, RoundingMode.HALF_UP);
            item.setValorPedido(valorPizza);
            total = total.add(valorPizza);
        }

        for (ItemAdicional item : venda.getAdicionais()) {
            BigDecimal valorAdicional = item.getAdicional().getPreco()
                    .multiply(BigDecimal.valueOf(item.getQuantidade()));
            valorAdicional = valorAdicional.setScale(2, RoundingMode.HALF_UP);
            item.setPrecoUnitario(item.getAdicional().getPreco());
            total = total.add(valorAdicional);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
