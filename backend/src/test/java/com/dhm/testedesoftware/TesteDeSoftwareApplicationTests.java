package com.dhm.testedesoftware;

import com.dhm.testedesoftware.model.Adicional;
import com.dhm.testedesoftware.model.ItemAdicional;
import com.dhm.testedesoftware.model.ItemPedido;
import com.dhm.testedesoftware.model.Pizza;
import com.dhm.testedesoftware.model.TamanhoPizza;
import com.dhm.testedesoftware.model.Venda;
import com.dhm.testedesoftware.repository.VendaRepository;
import com.dhm.testedesoftware.service.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TesteDeSoftwareApplicationTests {

    @Mock
    private VendaRepository vendaRepository;

    private VendaService vendaService;

    @BeforeEach
    void setUp() {
        vendaService = new VendaService(vendaRepository);
    }

    @Test
    void deveSomarPizzasEAdicionais() {
        Pizza pizza = pizzaComValor("40.00");
        Adicional adicional = adicionalComValor("6.00");
        ItemAdicional extra = itemAdicional(adicional, 2);

        Venda venda = novaVenda(List.of(itemPedido(pizza, TamanhoPizza.GRANDE, 2)), List.of(extra));

        BigDecimal total = vendaService.calcularTotal(venda);

        // (40.00 * 1.8 * 2) + (6.00 * 2) = 144.00 + 12.00
        assertEquals(new BigDecimal("156.00"), total);
    }

    @Test
    void deveCalcularValorTotalDeVendaComPizzasDeTamanhosDiferentes() {
        Pizza pizzaPequena = pizzaComValor("30.00");
        Pizza pizzaGrande = pizzaComValor("30.00");

        Venda venda = novaVenda(
                List.of(
                        itemPedido(pizzaPequena, TamanhoPizza.PEQUENA, 1),
                        itemPedido(pizzaGrande, TamanhoPizza.GRANDE, 1)
                ),
                List.of()
        );

        BigDecimal total = vendaService.calcularTotal(venda);

        // 30.00 (pequena) + (30.00 * 1.8) (grande) = 30.00 + 54.00
        assertEquals(new BigDecimal("84.00"), total);
    }

    @Test
    void deveCalcularPizzaPequenaComQuantidade() {
        Pizza pizza = pizzaComValor("20.00");

        Venda venda = novaVenda(List.of(itemPedido(pizza, TamanhoPizza.PEQUENA, 3)), List.of());

        BigDecimal resultado = vendaService.calcularTotal(venda);

        assertEquals(new BigDecimal("60.00"), resultado);
    }

    @Test
    void deveRetornarZeroParaVendaSemItens() {
        Venda venda = novaVenda(List.of(), List.of());

        BigDecimal total = vendaService.calcularTotal(venda);

        assertEquals(new BigDecimal("0.00"), total);
    }

    @Test
    void deveSalvarVendaUsandoRepository() {
        Pizza pizza = pizzaComValor("35.00");
        Venda venda = novaVenda(List.of(itemPedido(pizza, TamanhoPizza.GRANDE, 1)), List.of());

        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);

        Venda salva = vendaService.salvar(venda);

        verify(vendaRepository).save(venda);
        assertEquals(new BigDecimal("63.00"), salva.getValor()); // 35.00 * 1.8
    }

    @Test
    void deveBuscarVendaPorIdUsandoRepository() {
        Pizza pizza = pizzaComValor("25.00");
        Venda venda = novaVenda(List.of(itemPedido(pizza, TamanhoPizza.PEQUENA, 1)), List.of());

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        Venda encontrada = vendaService.buscarPorId(1L);

        verify(vendaRepository).findById(1L);
        assertEquals(venda, encontrada);
    }

    private static Pizza pizzaComValor(String valor) {
        Pizza pizza = new Pizza();
        pizza.setName("Calabresa");
        pizza.setDescricao("Calabresa tradicional");
        pizza.setCategoria("Tradicionais");
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