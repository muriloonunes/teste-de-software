import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthStateService } from '../../core/services/auth-state.service';
import { CatalogoService } from '../../core/services/catalogo.service';
import { PedidoService } from '../../core/services/pedido.service';
import { Adicional } from '../../core/models/adicional.model';
import { DadosEntrega, ItemCarrinho, Pedido, TamanhoPizza } from '../../core/models/pedido.model';
import { Pizza } from '../../core/models/pizza.model';

const CHAVE_CARRINHO = 'dhm-pizzaria-pedido-atual';

@Component({
  selector: 'app-internal-screen',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './internal-screen.html',
  styleUrl: './internal-screen.css',
})
export class InternalScreen implements OnInit {
  private readonly authState = inject(AuthStateService);
  private readonly catalogoService = inject(CatalogoService);
  private readonly pedidoService = inject(PedidoService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly usuario = this.authState.usuario;

  pizzas: Pizza[] = [];
  adicionais: Adicional[] = [];
  pedidos: Pedido[] = [];
  carrinho: ItemCarrinho[] = this.loadCart();
  abaAtiva: 'novo-pedido' | 'historico' = 'novo-pedido';
  categoria = 'Todas';
  busca = '';
  carrinhoAberto = false;
  mensagem = '';
  tipoMensagem: 'sucesso' | 'erro' | 'info' = 'sucesso';
  private timerMensagem: ReturnType<typeof setTimeout> | null = null;
  entrega: DadosEntrega = { nome: '', telefone: '', endereco: '' };
  private readonly tamanhosSelecionados = new Map<number, TamanhoPizza>();

  ngOnInit(): void {
    this.entrega.nome = this.usuario()?.nome ?? '';
    this.carregarCatalogo();
    this.carregarHistorico();
  }

  exibirFeedback(texto: string, tipo: 'sucesso' | 'erro' | 'info' = 'sucesso'): void {
    if (this.timerMensagem) {
      clearTimeout(this.timerMensagem);
    }
    this.mensagem = texto;
    this.tipoMensagem = tipo;
    this.cdr.markForCheck();
    this.timerMensagem = setTimeout(() => {
      this.mensagem = '';
      this.timerMensagem = null;
      this.cdr.markForCheck();
    }, 4000);
  }

  fecharFeedback(): void {
    this.mensagem = '';
    if (this.timerMensagem) {
      clearTimeout(this.timerMensagem);
      this.timerMensagem = null;
    }
    this.cdr.markForCheck();
  }

  carregarCatalogo(): void {
    this.catalogoService.listarPizzas().subscribe({
      next: pizzas => {
        this.pizzas = pizzas;
        this.cdr.markForCheck();
      },
      error: () => this.exibirFeedback('Não foi possível carregar o cardápio.', 'erro'),
    });
    this.catalogoService.listarAdicionais().subscribe({
      next: adicionais => {
        this.adicionais = adicionais;
        this.cdr.markForCheck();
      },
      error: () => this.exibirFeedback('Não foi possível carregar os adicionais.', 'erro'),
    });
  }

  get categorias(): string[] {
    return ['Todas', ...new Set(this.pizzas.map(pizza => pizza.categoria))];
  }

  get pizzasFiltradas(): Pizza[] {
    const termo = this.busca.toLowerCase().trim();
    return this.pizzas.filter(pizza =>
      (this.categoria === 'Todas' || pizza.categoria === this.categoria) &&
      (!termo || `${pizza.name} ${pizza.descricao}`.toLowerCase().includes(termo)),
    );
  }

  get quantidadeCarrinho(): number {
    return this.carrinho.reduce((total, item) => total + item.quantidade, 0);
  }

  get subtotal(): number {
    return this.carrinho.reduce((total, item) => total + this.precoItem(item), 0);
  }

  get faturamentoHistorico(): number {
    return this.pedidos.reduce((total, pedido) => total + Number(pedido.valor), 0);
  }

  precoItem(item: ItemCarrinho): number {
    const multiplicadores: Record<TamanhoPizza, number> = { PEQUENA: 1, MEDIA: 1.5, GRANDE: 1.8 };
    const extras = item.adicionais.reduce((total, adicional) => total + Number(adicional.preco), 0);
    return (Number(item.pizza.valorBase) * multiplicadores[item.tamanho] + extras) * item.quantidade;
  }

  tamanhoSelecionado(pizza: Pizza): TamanhoPizza {
    return this.tamanhosSelecionados.get(pizza.id) ?? 'MEDIA';
  }

  selecionarTamanho(pizza: Pizza, tamanho: TamanhoPizza): void {
    this.tamanhosSelecionados.set(pizza.id, tamanho);
  }

  adicionar(pizza: Pizza): void {
    const tamanho = this.tamanhoSelecionado(pizza);
    const existente = this.carrinho.find(item => item.pizza.id === pizza.id && item.tamanho === tamanho);
    if (existente) {
      existente.quantidade += 1;
    } else {
      this.carrinho.push({ pizza, tamanho, quantidade: 1, adicionais: [] });
    }
    this.persistirCarrinho();
    this.exibirFeedback(`🍕 ${pizza.name} (${tamanho.toLowerCase()}) adicionada!`, 'sucesso');
  }

  alterarQuantidade(index: number, delta: number): void {
    this.carrinho[index].quantidade += delta;
    if (this.carrinho[index].quantidade <= 0) {
      this.carrinho.splice(index, 1);
      this.exibirFeedback('Item removido do pedido.', 'info');
    }
    this.persistirCarrinho();
  }

  remover(index: number): void {
    const item = this.carrinho[index];
    this.carrinho.splice(index, 1);
    this.persistirCarrinho();
    this.exibirFeedback(`Item "${item?.pizza?.name ?? 'Pizza'}" removido.`, 'info');
  }

  finalizar(): void {
    const usuario = this.usuario();
    if (!usuario) return;
    if (!this.carrinho.length) {
      this.exibirFeedback('Adicione pelo menos uma pizza ao pedido antes de finalizar.', 'erro');
      return;
    }
    if (!this.entrega.telefone.trim() || !this.entrega.endereco.trim()) {
      this.exibirFeedback('Por favor, informe telefone e endereço de entrega do cliente.', 'erro');
      return;
    }
    this.pedidoService.criarCliente(this.entrega).subscribe({
      next: cliente => this.pedidoService.criarVenda({
        clienteId: cliente.id,
        usuarioId: usuario.id,
        pedidos: this.carrinho.map(item => ({
          pizzaId: item.pizza.id,
          tamanho: item.tamanho,
          quantidade: item.quantidade,
        })),
        adicionais: this.carrinho.flatMap(item =>
          item.adicionais.map(adicional => ({ adicionalId: adicional.id, quantidade: item.quantidade })),
        ),
      }).subscribe({
        next: () => {
          this.carrinho = [];
          this.persistirCarrinho();
          this.carregarHistorico();
          this.abaAtiva = 'historico';
          this.carrinhoAberto = false;
          this.exibirFeedback('Pedido registrado com sucesso!', 'sucesso');
          this.cdr.markForCheck();
        },
        error: error => this.exibirFeedback(error.error?.mensagem || error.error?.message || 'Não foi possível registrar o pedido.', 'erro'),
      }),
      error: error => this.exibirFeedback(error.error?.mensagem || error.error?.message || 'Não foi possível salvar os dados do cliente.', 'erro'),
    });
  }

  carregarHistorico(): void {
    const usuario = this.usuario();
    if (!usuario) return;
    this.pedidoService.listarHistorico(usuario.id).subscribe(pedidos => {
      this.pedidos = pedidos;
      this.cdr.markForCheck();
    });
  }

  logout(): void {
    this.authState.encerrarSessao();
    this.router.navigate(['/login']);
  }

  private persistirCarrinho(): void {
    localStorage.setItem(CHAVE_CARRINHO, JSON.stringify(this.carrinho));
  }

  private loadCart(): ItemCarrinho[] {
    try {
      return JSON.parse(localStorage.getItem(CHAVE_CARRINHO) || '[]') as ItemCarrinho[];
    } catch {
      return [];
    }
  }
}
