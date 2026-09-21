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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

    @Test
    void deveCalcularTotalDaVendaComPizzaEAdicional() {
        Pizza pizza = new Pizza();
        pizza.setValorBase(new BigDecimal("30.00"));

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setPizza(pizza);
        itemPedido.setTamanho(TamanhoPizza.MEDIA);
        itemPedido.setQuantidade(2);

        Adicional adicional = new Adicional();
        adicional.setPreco(new BigDecimal("5.00"));

        ItemAdicional itemAdicional = new ItemAdicional();
        itemAdicional.setAdicional(adicional);
        itemAdicional.setQuantidade(1);

        Venda venda = new Venda();
        venda.setPedidos(List.of(itemPedido));
        venda.setAdicionais(List.of(itemAdicional));

        BigDecimal resultado = new VendaService(vendaRepository).calcularTotal(venda);

        assertEquals(new BigDecimal("95.00"), resultado);
        assertEquals(new BigDecimal("90.00"), itemPedido.getValorPedido());
    }

    @Test
    void deveCalcularPizzaPequenaComQuantidade() {
        Pizza pizza = new Pizza();
        pizza.setValorBase(new BigDecimal("20.00"));

        ItemPedido item = new ItemPedido();
        item.setPizza(pizza);
        item.setTamanho(TamanhoPizza.PEQUENA);
        item.setQuantidade(3);

        Venda venda = new Venda();
        venda.setPedidos(List.of(item));

        BigDecimal resultado = new VendaService(vendaRepository).calcularTotal(venda);

        assertEquals(new BigDecimal("60.00"), resultado);
    }

    @Test
    void deveSalvarVendaComValorCalculado() {
        Pizza pizza = new Pizza();
        pizza.setValorBase(new BigDecimal("25.00"));

        ItemPedido item = new ItemPedido();
        item.setPizza(pizza);
        item.setTamanho(TamanhoPizza.PEQUENA);
        item.setQuantidade(1);

        Venda venda = new Venda();
        venda.setPedidos(List.of(item));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venda resultado = new VendaService(vendaRepository).salvar(venda);

        assertEquals(new BigDecimal("25.00"), resultado.getValor());
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
}
