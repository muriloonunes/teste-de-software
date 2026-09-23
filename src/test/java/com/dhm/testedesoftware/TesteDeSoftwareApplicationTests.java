package com.dhm.testedesoftware;

import com.dhm.testedesoftware.model.Adicional;
import com.dhm.testedesoftware.model.ItemAdicional;
import com.dhm.testedesoftware.model.ItemPedido;
import com.dhm.testedesoftware.model.Pizza;
import com.dhm.testedesoftware.model.TamanhoPizza;
import com.dhm.testedesoftware.model.Venda;
import com.dhm.testedesoftware.repository.PizzaRepository;
import com.dhm.testedesoftware.repository.VendaRepository;
import com.dhm.testedesoftware.service.PizzaService;
import com.dhm.testedesoftware.service.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TesteDeSoftwareApplicationTests {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private VendaService vendaService;

    @BeforeEach
    void setUp() {
        vendaService = new VendaService(vendaRepository);
    }


    @Test
    void deveSomarPizzasEAdicionais() {
        Pizza pizza = pizzaComValor("40.00");
        Adicional adicional = adicionalComValor("6.00");

        ItemAdicional extra = itemAdicional(adicional, 1);
        extra.setQuantidade(2);
        Venda venda = novaVenda(List.of(itemPedido(pizza, TamanhoPizza.GRANDE, 2)), List.of(extra));

        BigDecimal total = vendaService.calcularTotal(venda);

        // (40.00 * 1.8 * 2) + (6.00 * 2) = 144.00 + 12.00
        assertEquals(new BigDecimal("156.00"), total);
    }

    @Test
    void deveCalcularPizzaPequenaComQuantidade() {
        Pizza pizza = new Pizza();
        pizza.setValorBase(new BigDecimal("20.00"));

       Venda venda= novaVenda(List.of(itemPedido(pizza,TamanhoPizza.PEQUENA,3)), List.of());

        BigDecimal resultado = vendaService.calcularTotal(venda);

        assertEquals(new BigDecimal("60.00"), resultado);
    }

    @Test
    void deveSalvarVendaComValorCalculado() {
        Pizza pizza = new Pizza();
        pizza.setValorBase(new BigDecimal("25.00"));

        ItemPedido item = itemPedido(pizza,TamanhoPizza.PEQUENA,3);

        Venda venda= novaVenda(List.of(item), List.of());
        venda.setPedidos(List.of(item));

        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venda resultado = new VendaService(vendaRepository).salvar(venda);

        assertEquals(new BigDecimal("75.00"), resultado.getValor());
        verify(vendaRepository).save(venda);
    }

    @Test
    void deveSalvarPizzaUsandoRepositoryMockado() {
        Pizza pizza = new Pizza();
        pizza.setName("Calabresa");
        when(pizzaRepository.save(pizza)).thenReturn(pizza);

        Pizza resultado = new PizzaService(pizzaRepository).salvar(pizza);

        assertEquals("Calabresa", resultado.getName());
        verify(pizzaRepository).save(pizza);
    }

    @Test
    void deveRetornarZeroParaVendaSemItens() {
        Venda venda = novaVenda(List.of(), List.of());

        BigDecimal total = vendaService.calcularTotal(venda);

        assertEquals(new BigDecimal("0.00"), total);
    }

    private static Pizza pizzaComValor(String valor) {
        Pizza pizza = new Pizza();
        pizza.setName("Calabresa");
        pizza.setDescricao("Calabresa tradicional");
        pizza.setValorBase(new BigDecimal(valor));
        return pizza;
    }

    private static ItemPedido itemPedido(Pizza pizza, TamanhoPizza tamanho, int quantidade) {
        ItemPedido item = new ItemPedido();
        item.setPizza(pizza);
        item.setTamanho(tamanho);
        item.setQuantidade(quantidade);
        return item;
    }

    private static Adicional adicionalComValor(String valor) {
        Adicional adicional = new Adicional();
        adicional.setNome("Borda de catupiry");
        adicional.setDescricao("Borda recheada");
        adicional.setPreco(new BigDecimal(valor));
        return adicional;
    }

    private static ItemAdicional itemAdicional(Adicional adicional, int quantidade) {
        ItemAdicional item = new ItemAdicional();
        item.setAdicional(adicional);
        item.setQuantidade(quantidade);
        return item;
    }

    private static Venda novaVenda(List<ItemPedido> pedidos, List<ItemAdicional> adicionais) {
        Venda venda = new Venda();
        venda.setPedidos(new ArrayList<>(pedidos));
        venda.setAdicionais(new ArrayList<>(adicionais));
        return venda;
    }
}
