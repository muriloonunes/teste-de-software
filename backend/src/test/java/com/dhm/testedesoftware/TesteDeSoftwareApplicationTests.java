package com.dhm.testedesoftware;

import com.dhm.testedesoftware.model.Adicional;
import com.dhm.testedesoftware.model.ItemAdicional;
import com.dhm.testedesoftware.model.ItemPedido;
import com.dhm.testedesoftware.model.Pizza;
import com.dhm.testedesoftware.model.TamanhoPizza;
import com.dhm.testedesoftware.model.Venda;
import com.dhm.testedesoftware.model.Cliente;
import com.dhm.testedesoftware.model.Pagamento;
import com.dhm.testedesoftware.model.Usuario;
import com.dhm.testedesoftware.model.dto.ItemAdicionalRequestDTO;
import com.dhm.testedesoftware.model.dto.ItemPedidoRequestDTO;
import com.dhm.testedesoftware.model.dto.VendaRequestDTO;
import com.dhm.testedesoftware.repository.VendaRepository;
import com.dhm.testedesoftware.repository.AdicionalRepository;
import com.dhm.testedesoftware.repository.ClienteRepository;
import com.dhm.testedesoftware.repository.PagamentoRepository;
import com.dhm.testedesoftware.repository.PizzaRepository;
import com.dhm.testedesoftware.repository.UsuarioRepository;
import com.dhm.testedesoftware.service.VendaService;
import com.dhm.testedesoftware.service.UsuarioService;
import com.dhm.testedesoftware.model.dto.UsuarioRequestDTO;
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
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private AdicionalRepository adicionalRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;

    private VendaService vendaService;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        vendaService = new VendaService(
                vendaRepository, pizzaRepository, adicionalRepository,
                clienteRepository, usuarioRepository, pagamentoRepository
        );
        usuarioService = new UsuarioService(usuarioRepository);
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

    @Test
    void deveCriarVendaCompletaPelaCamadaDeServico() {
        Pizza pizza = pizzaComValor("40.00");
        Adicional adicional = adicionalComValor("6.00");
        Cliente cliente = new Cliente();
        cliente.setId(10L);
        Usuario usuario = usuario("operador@bella.com", true);
        usuario.setId(20L);
        Pagamento pagamento = pagamento(30L);

        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
        when(adicionalRepository.findById(2L)).thenReturn(Optional.of(adicional));
        when(clienteRepository.findById(10L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuario));
        when(pagamentoRepository.findById(30L)).thenReturn(Optional.of(pagamento));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VendaService service = vendaService;
        var response = service.criar(new VendaRequestDTO(
                10L,
                20L,
                30L,
                List.of(new ItemPedidoRequestDTO(1L, TamanhoPizza.GRANDE, 2)),
                List.of(new ItemAdicionalRequestDTO(2L, 1))
        ));

        assertEquals(new BigDecimal("150.00"), response.valor());
        assertEquals(1, response.pedidos().size());
        assertEquals(1, response.adicionais().size());
        verify(vendaRepository).save(any(Venda.class));
    }

    @Test
    void deveValidarHistoricoPorUsuario() {
        Usuario usuario = usuario("historico@bella.com", true);
        usuario.setId(7L);
        Venda pedido = novaVenda(List.of(), List.of());
        pedido.setUsuario(usuario);
        pedido.setValor(new BigDecimal("50.00"));
        when(vendaRepository.findByUsuarioIdOrderByIdDesc(7L)).thenReturn(List.of(pedido));

        List<?> historico = vendaService.historicoDTO(7L);

        assertEquals(1, historico.size());
        verify(vendaRepository).findByUsuarioIdOrderByIdDesc(7L);
    }

    @Test
    void deveAutenticarUsuarioAtivo() {
        Usuario usuario = usuario("ativo@bella.com", true);
        usuario.setSenha(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("segredo123"));
        when(usuarioRepository.findByEmailIgnoreCase("ativo@bella.com")).thenReturn(Optional.of(usuario));

        assertEquals(usuario, usuarioService.autenticar("ativo@bella.com", "segredo123"));
    }

    @Test
    void deveRejeitarSenhaIncorreta() {
        Usuario usuario = usuario("senha@bella.com", true);
        usuario.setSenha(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("segredo123"));
        when(usuarioRepository.findByEmailIgnoreCase("senha@bella.com")).thenReturn(Optional.of(usuario));

        org.junit.jupiter.api.Assertions.assertThrows(
                com.dhm.testedesoftware.exception.CredenciaisInvalidasException.class,
                () -> usuarioService.autenticar("senha@bella.com", "senha-incorreta")
        );
    }

    @Test
    void deveRejeitarUsuarioInativo() {
        Usuario usuario = usuario("inativo@bella.com", false);
        usuario.setSenha(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("segredo123"));
        when(usuarioRepository.findByEmailIgnoreCase("inativo@bella.com")).thenReturn(Optional.of(usuario));

        org.junit.jupiter.api.Assertions.assertThrows(
                com.dhm.testedesoftware.exception.CredenciaisInvalidasException.class,
                () -> usuarioService.autenticar("inativo@bella.com", "segredo123")
        );
    }

    @Test
    void deveImpedirEmailDuplicado() {
        when(usuarioRepository.existsByEmailIgnoreCase("duplicado@bella.com")).thenReturn(true);

        org.junit.jupiter.api.Assertions.assertThrows(
                com.dhm.testedesoftware.exception.EmailJaCadastradoException.class,
                () -> usuarioService.criar(new UsuarioRequestDTO(
                        "Operador", "duplicado@bella.com", "segredo123", true
                ))
        );
        verify(usuarioRepository, org.mockito.Mockito.never()).save(any(Usuario.class));
    }

    @Test
    void deveArredondarSegundaCasaDecimalDoPrecoDaPizza() {
        Pizza pizza = pizzaComValor("49.90");
        // Pequena (1x): 49.90 -> 49.90
        // Média (1.5x): 49.90 * 1.5 = 74.85 (segunda casa 5 -> mantém 74.85)
        // Grande (1.8x): 49.90 * 1.8 = 89.82 (segunda casa 2 < 5 -> arredonda p/ baixo 89.80)
        assertEquals(new BigDecimal("74.85"), vendaService.arredondarPrecoPizza(new BigDecimal("74.85")));
        assertEquals(new BigDecimal("89.80"), vendaService.arredondarPrecoPizza(new BigDecimal("89.82")));
        assertEquals(new BigDecimal("89.90"), vendaService.arredondarPrecoPizza(new BigDecimal("89.87")));

        Venda vendaGrande = novaVenda(List.of(itemPedido(pizza, TamanhoPizza.GRANDE, 1)), List.of());
        assertEquals(new BigDecimal("89.80"), vendaService.calcularTotal(vendaGrande));
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
        adicional.setCategoria("Extra");
        adicional.setPreco(new BigDecimal(valor));
        return adicional;
    }

    private static Usuario usuario(String email, boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setNome("Operador");
        usuario.setEmail(email);
        usuario.setAtivo(ativo);
        return usuario;
    }

    private static Pagamento pagamento(Long id) {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(id);
        pagamento.setNome("Pix");
        pagamento.setTipoPagamento("PIX");
        pagamento.setTaxa(0.0);
        pagamento.setParcelas(1);
        return pagamento;
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
