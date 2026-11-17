import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, HostListener, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthStateService } from '../../core/services/auth-state.service';
import { CatalogoService } from '../../core/services/catalogo.service';
import { PedidoService } from '../../core/services/pedido.service';
import { Adicional } from '../../core/models/adicional.model';
import { DadosEntrega, ItemAdicionalCarrinho, ItemCarrinho, Pagamento, Pedido, TamanhoPizza } from '../../core/models/pedido.model';
import { Pizza } from '../../core/models/pizza.model';
import { CardapioManagerComponent } from './cardapio-manager/cardapio-manager';

const CHAVE_CARRINHO = 'dhm-pizzaria-pedido-atual';
const CHAVE_ACOMPANHAMENTOS = 'dhm-pizzaria-acompanhamentos-atual';

@Component({
  selector: 'app-internal-screen',
  standalone: true,
  imports: [CommonModule, FormsModule, CardapioManagerComponent],
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

  readonly opcoesTamanho: { valor: TamanhoPizza; sigla: string; rotulo: string; fatias: string }[] = [
    { valor: 'PEQUENA', sigla: 'P', rotulo: 'Pequena', fatias: '4 fatias' },
    { valor: 'MEDIA', sigla: 'M', rotulo: 'Média', fatias: '6 fatias' },
    { valor: 'GRANDE', sigla: 'G', rotulo: 'Grande', fatias: '8 fatias' },
  ];

  pizzas: Pizza[] = [];
  adicionais: Adicional[] = [];
  pagamentos: Pagamento[] = [];
  pagamentoSelecionadoId: number | undefined;
  pedidos: Pedido[] = [];
  carrinho: ItemCarrinho[] = this.loadCart();
  acompanhamentosCarrinho: ItemAdicionalCarrinho[] = this.loadAcompanhamentos();
  abaAtiva: 'novo-pedido' | 'historico' | 'cardapio' = 'novo-pedido';
  categoria = 'Todas';
  busca = '';
  carrinhoAberto = false;
  mensagem = '';
  tipoMensagem: 'sucesso' | 'erro' | 'info' = 'sucesso';
  private timerMensagem: ReturnType<typeof setTimeout> | null = null;
  entrega: DadosEntrega = { nome: '', telefone: '', endereco: '' };
  private readonly tamanhosSelecionados = new Map<number, TamanhoPizza>();

  @HostListener('document:keydown.escape')
  fecharComEscape(): void {
    if (this.carrinhoAberto) {
      this.carrinhoAberto = false;
      this.cdr.markForCheck();
    }
  }

  ngOnInit(): void {
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
      error: () => this.exibirFeedback('Não foi possível carregar os acompanhamentos e adicionais.', 'erro'),
    });
    this.pedidoService.listarPagamentos().subscribe({
      next: pagamentos => {
        this.pagamentos = pagamentos;
        if (!this.pagamentoSelecionadoId && pagamentos.length > 0) {
          this.pagamentoSelecionadoId = pagamentos[0].id;
        }
        this.cdr.markForCheck();
      },
      error: () => this.exibirFeedback('Não foi possível carregar as formas de pagamento.', 'erro'),
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

  get adicionaisFiltrados(): Adicional[] {
    const termo = this.busca.toLowerCase().trim();
    if (!termo) return this.adicionais;
    return this.adicionais.filter(adicional =>
      `${adicional.nome} ${adicional.descricao ?? ''}`.toLowerCase().includes(termo),
    );
  }

  get quantidadePizzasCarrinho(): number {
    return this.carrinho.reduce((total, item) => total + item.quantidade, 0);
  }

  get quantidadeAcompanhamentosCarrinho(): number {
    return this.acompanhamentosCarrinho.reduce((total, item) => total + item.quantidade, 0);
  }

  get quantidadeCarrinho(): number {
    return this.quantidadePizzasCarrinho + this.quantidadeAcompanhamentosCarrinho;
  }

  get subtotalPizzas(): number {
    return this.carrinho.reduce((total, item) => total + this.precoItem(item), 0);
  }

  get subtotalAcompanhamentos(): number {
    return this.acompanhamentosCarrinho.reduce(
      (total, item) => total + Number(item.adicional.preco) * item.quantidade,
      0,
    );
  }

  get subtotal(): number {
    return this.subtotalPizzas + this.subtotalAcompanhamentos;
  }

  get pagamentoSelecionado(): Pagamento | undefined {
    return this.pagamentos.find(p => p.id === Number(this.pagamentoSelecionadoId));
  }

  get percentualTaxaPagamento(): number {
    return Number(this.pagamentoSelecionado?.taxa ?? 0);
  }

  get valorTaxaPagamento(): number {
    return this.subtotal * (this.percentualTaxaPagamento / 100);
  }

  get totalFinal(): number {
    return this.subtotal + this.valorTaxaPagamento;
  }

  get valorParcela(): number {
    const parcelas = Number(this.pagamentoSelecionado?.parcelas ?? 1);
    return parcelas > 1 ? this.totalFinal / parcelas : this.totalFinal;
  }

  get faturamentoHistorico(): number {
    return this.pedidos.reduce((total, pedido) => total + Number(pedido.valor), 0);
  }

  get ticketMedioHistorico(): number {
    if (!this.pedidos.length) return 0;
    return this.faturamentoHistorico / this.pedidos.length;
  }

  limparFiltros(): void {
    this.busca = '';
    this.categoria = 'Todas';
  }

  limparCarrinho(): void {
    if (!this.carrinho.length && !this.acompanhamentosCarrinho.length) return;
    this.carrinho = [];
    this.acompanhamentosCarrinho = [];
    this.persistirCarrinho();
    this.exibirFeedback('Comanda limpa.', 'info');
  }

  precoPizzaPorTamanho(pizza: Pizza, tamanhoEspecifico?: TamanhoPizza): number {
    const multiplicadores: Record<TamanhoPizza, number> = { PEQUENA: 1, MEDIA: 1.5, GRANDE: 1.8 };
    const tamanho = tamanhoEspecifico ?? this.tamanhoSelecionado(pizza);
    return Number(pizza.valorBase) * multiplicadores[tamanho];
  }

  precoItem(item: ItemCarrinho): number {
    const multiplicadores: Record<TamanhoPizza, number> = { PEQUENA: 1, MEDIA: 1.5, GRANDE: 1.8 };
    const extras = (item.adicionais ?? []).reduce((total, adicional) => total + Number(adicional.preco), 0);
    return (Number(item.pizza.valorBase) * multiplicadores[item.tamanho] + extras) * item.quantidade;
  }

  precoAcompanhamento(item: ItemAdicionalCarrinho): number {
    return Number(item.adicional.preco) * item.quantidade;
  }

  tamanhoSelecionado(pizza: Pizza): TamanhoPizza {
    return this.tamanhosSelecionados.get(pizza.id) ?? 'MEDIA';
  }

  selecionarTamanho(pizza: Pizza, tamanho: TamanhoPizza): void {
    this.tamanhosSelecionados.set(pizza.id, tamanho);
  }

  temAdicional(item: ItemCarrinho, adicional: Adicional): boolean {
    return (item.adicionais ?? []).some(a => a.id === adicional.id);
  }

  alternarAdicional(item: ItemCarrinho, adicional: Adicional): void {
    if (!item.adicionais) {
      item.adicionais = [];
    }
    const idx = item.adicionais.findIndex(a => a.id === adicional.id);
    if (idx >= 0) {
      item.adicionais.splice(idx, 1);
    } else {
      item.adicionais.push(adicional);
    }
    this.persistirCarrinho();
    this.cdr.markForCheck();
  }

  quantidadePizzaNoPedido(pizzaId: number): number {
    return this.carrinho
      .filter(item => item.pizza.id === pizzaId)
      .reduce((total, item) => total + item.quantidade, 0);
  }

  quantidadeAcompanhamentoNoPedido(adicionalId: number): number {
    return this.acompanhamentosCarrinho.find(item => item.adicional.id === adicionalId)?.quantidade ?? 0;
  }

  adicionarAcompanhamento(adicional: Adicional): void {
    const existente = this.acompanhamentosCarrinho.find(item => item.adicional.id === adicional.id);
    if (existente) {
      existente.quantidade += 1;
    } else {
      this.acompanhamentosCarrinho.push({ adicional, quantidade: 1 });
    }
    this.persistirCarrinho();
    this.exibirFeedback(`🥤 ${adicional.nome} adicionado ao pedido!`, 'sucesso');
  }

  alterarQuantidadeAcompanhamento(index: number, delta: number): void {
    const item = this.acompanhamentosCarrinho[index];
    if (!item) return;
    item.quantidade += delta;
    if (item.quantidade <= 0) {
      this.acompanhamentosCarrinho.splice(index, 1);
      this.exibirFeedback(`"${item.adicional.nome}" removido do pedido.`, 'info');
    }
    this.persistirCarrinho();
    this.cdr.markForCheck();
  }

  alterarQuantidadeAcompanhamentoPorId(adicional: Adicional, delta: number): void {
    const idx = this.acompanhamentosCarrinho.findIndex(item => item.adicional.id === adicional.id);
    if (idx >= 0) {
      this.alterarQuantidadeAcompanhamento(idx, delta);
    } else if (delta > 0) {
      this.adicionarAcompanhamento(adicional);
    }
  }

  removerAcompanhamento(index: number): void {
    const item = this.acompanhamentosCarrinho[index];
    this.acompanhamentosCarrinho.splice(index, 1);
    this.persistirCarrinho();
    this.exibirFeedback(`"${item?.adicional?.nome ?? 'Adicional'}" removido.`, 'info');
    this.cdr.markForCheck();
  }

  adicionar(pizza: Pizza): void {
    const tamanho = this.tamanhoSelecionado(pizza);
    const existente = this.carrinho.find(
      item => item.pizza.id === pizza.id && item.tamanho === tamanho && (!item.adicionais || item.adicionais.length === 0),
    );
    if (existente) {
      existente.quantidade += 1;
    } else {
      this.carrinho.push({ pizza, tamanho, quantidade: 1, adicionais: [] });
    }
    this.persistirCarrinho();
    this.exibirFeedback(`🍕 ${pizza.name} (${tamanho.toLowerCase()}) adicionada!`, 'sucesso');
  }

  alterarTamanhoItem(index: number, novoTamanho: TamanhoPizza): void {
    const item = this.carrinho[index];
    if (!item || item.tamanho === novoTamanho) return;

    const idsAdicionaisItem = (item.adicionais ?? []).map(a => a.id).sort().join(',');
    const indiceExistente = this.carrinho.findIndex((outro, idx) => {
      if (idx === index) return false;
      const idsOutro = (outro.adicionais ?? []).map(a => a.id).sort().join(',');
      return outro.pizza.id === item.pizza.id && outro.tamanho === novoTamanho && idsOutro === idsAdicionaisItem;
    });

    if (indiceExistente >= 0) {
      this.carrinho[indiceExistente].quantidade += item.quantidade;
      this.carrinho.splice(index, 1);
    } else {
      item.tamanho = novoTamanho;
    }

    this.persistirCarrinho();
    this.cdr.markForCheck();
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
    if (!this.carrinho.length && !this.acompanhamentosCarrinho.length) {
      this.exibirFeedback('Adicione pelo menos um item ao pedido antes de finalizar.', 'erro');
      return;
    }
    if (!this.entrega.nome.trim() || !this.entrega.telefone.trim() || !this.entrega.endereco.trim()) {
      this.exibirFeedback('Por favor, informe nome, telefone e endereço de entrega do cliente.', 'erro');
      return;
    }

    // Agrega quantidades de acompanhamentos avulsos + modificadores de pizzas para enviar à API VendaService
    const mapaAdicionais = new Map<number, number>();
    for (const acomp of this.acompanhamentosCarrinho) {
      const atual = mapaAdicionais.get(acomp.adicional.id) ?? 0;
      mapaAdicionais.set(acomp.adicional.id, atual + acomp.quantidade);
    }
    for (const item of this.carrinho) {
      for (const adicional of item.adicionais ?? []) {
        const atual = mapaAdicionais.get(adicional.id) ?? 0;
        mapaAdicionais.set(adicional.id, atual + item.quantidade);
      }
    }
    const listaAdicionais = Array.from(mapaAdicionais.entries()).map(([adicionalId, quantidade]) => ({
      adicionalId,
      quantidade,
    }));

    this.pedidoService.criarCliente({
      nome: this.entrega.nome.trim(),
      telefone: this.entrega.telefone.trim(),
      endereco: this.entrega.endereco.trim(),
    }).subscribe({
      next: cliente => this.pedidoService.criarVenda({
        clienteId: cliente.id,
        usuarioId: usuario.id,
        pagamentoId: this.pagamentoSelecionadoId,
        pedidos: this.carrinho.map(item => ({
          pizzaId: item.pizza.id,
          tamanho: item.tamanho,
          quantidade: item.quantidade,
        })),
        adicionais: listaAdicionais,
      }).subscribe({
        next: () => {
          this.carrinho = [];
          this.acompanhamentosCarrinho = [];
          this.persistirCarrinho();
          this.entrega = { nome: '', telefone: '', endereco: '' };
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
    localStorage.setItem(CHAVE_ACOMPANHAMENTOS, JSON.stringify(this.acompanhamentosCarrinho));
  }

  private loadCart(): ItemCarrinho[] {
    try {
      return JSON.parse(localStorage.getItem(CHAVE_CARRINHO) || '[]') as ItemCarrinho[];
    } catch {
      return [];
    }
  }

  private loadAcompanhamentos(): ItemAdicionalCarrinho[] {
    try {
      return JSON.parse(localStorage.getItem(CHAVE_ACOMPANHAMENTOS) || '[]') as ItemAdicionalCarrinho[];
    } catch {
      return [];
    }
  }
}
