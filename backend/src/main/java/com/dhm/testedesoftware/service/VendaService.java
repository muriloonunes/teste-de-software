package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.model.dto.ItemAdicionalRequestDTO;
import com.dhm.testedesoftware.model.dto.ItemPedidoRequestDTO;
import com.dhm.testedesoftware.model.dto.VendaRequestDTO;
import com.dhm.testedesoftware.model.dto.VendaResponseDTO;
import com.dhm.testedesoftware.exception.EntidadeNaoEncontrada;
import com.dhm.testedesoftware.model.Adicional;
import com.dhm.testedesoftware.model.Cliente;
import com.dhm.testedesoftware.model.ItemAdicional;
import com.dhm.testedesoftware.model.ItemPedido;
import com.dhm.testedesoftware.model.Pagamento;
import com.dhm.testedesoftware.model.Pizza;
import com.dhm.testedesoftware.model.Usuario;
import com.dhm.testedesoftware.model.Venda;
import com.dhm.testedesoftware.repository.AdicionalRepository;
import com.dhm.testedesoftware.repository.ClienteRepository;
import com.dhm.testedesoftware.repository.PagamentoRepository;
import com.dhm.testedesoftware.repository.PizzaRepository;
import com.dhm.testedesoftware.repository.UsuarioRepository;
import com.dhm.testedesoftware.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final PizzaRepository pizzaRepository;
    private final AdicionalRepository adicionalRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagamentoRepository pagamentoRepository;

    public VendaService(VendaRepository vendaRepository) {
        this(vendaRepository, null, null, null, null, null);
    }

    @Autowired
    public VendaService(
            VendaRepository vendaRepository,
            PizzaRepository pizzaRepository,
            AdicionalRepository adicionalRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            PagamentoRepository pagamentoRepository
    ) {
        this.vendaRepository = vendaRepository;
        this.pizzaRepository = pizzaRepository;
        this.adicionalRepository = adicionalRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    public Venda salvar(Venda venda) {
        venda.setValor(calcularTotal(venda));
        return vendaRepository.save(venda);
    }

    @Transactional
    public VendaResponseDTO criar(VendaRequestDTO dto) {
        Venda venda = new Venda();

        if (dto.clienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(
                    () -> new EntidadeNaoEncontrada("Cliente não encontrado com o id: " + dto.clienteId())
            );
            venda.setCliente(cliente);
        }

        if (dto.usuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.usuarioId()).orElseThrow(
                    () -> new EntidadeNaoEncontrada("Usuário não encontrado com o id: " + dto.usuarioId())
            );
            venda.setUsuario(usuario);
        }

        if (dto.pagamentoId() != null) {
            Pagamento pagamento = pagamentoRepository.findById(dto.pagamentoId()).orElseThrow(
                    () -> new EntidadeNaoEncontrada("Pagamento não encontrado com o id: " + dto.pagamentoId())
            );
            venda.setPagamento(pagamento);
        }

        List<ItemPedido> itensPedido = new ArrayList<>();
        if (dto.pedidos() != null) {
            for (ItemPedidoRequestDTO itemDto : dto.pedidos()) {
                Pizza pizza = pizzaRepository.findById(itemDto.pizzaId()).orElseThrow(
                        () -> new EntidadeNaoEncontrada("Pizza não encontrada com o id: " + itemDto.pizzaId())
                );
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setPizza(pizza);
                itemPedido.setTamanho(itemDto.tamanho());
                itemPedido.setQuantidade(itemDto.quantidade());
                itensPedido.add(itemPedido);
            }
        }
        venda.setPedidos(itensPedido);

        List<ItemAdicional> itensAdicional = new ArrayList<>();
        if (dto.adicionais() != null) {
            for (ItemAdicionalRequestDTO itemDto : dto.adicionais()) {
                Adicional adicional = adicionalRepository.findById(itemDto.adicionalId()).orElseThrow(
                        () -> new EntidadeNaoEncontrada("Adicional não encontrado com id: " + itemDto.adicionalId())
                );
                ItemAdicional itemAdicional = new ItemAdicional();
                itemAdicional.setAdicional(adicional);
                itemAdicional.setQuantidade(itemDto.quantidade());
                itensAdicional.add(itemAdicional);
            }
        }
        venda.setAdicionais(itensAdicional);

        return VendaResponseDTO.fromEntity(salvar(venda));
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarDTO() {
        return vendaRepository.findAll().stream()
                .map(VendaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> historicoDTO(Long usuarioId) {
        return vendaRepository.findByUsuarioIdOrderByIdDesc(usuarioId).stream()
                .map(VendaResponseDTO::fromEntity)
                .toList();
    }

    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Venda não encontrada com o id: " + id)
        );
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarDTOPorId(Long id) {
        return VendaResponseDTO.fromEntity(buscarPorId(id));
    }

    public void excluir(Long id) {
        if (!vendaRepository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Venda não encontrada com o id: " + id);
        }
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
