import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Pizza {
  id: number;
  name: string;
  descricao: string;
  categoria: string;
  valorBase: number;
}

interface Adicional {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
}

interface User {
  id: number;
  nome: string;
  email: string;
  ativo: boolean;
}

interface CartItem {
  pizza: Pizza;
  tamanho: 'PEQUENA' | 'MEDIA' | 'GRANDE';
  quantidade: number;
  adicionais: Adicional[];
}

interface Order {
  id: number;
  valor: number;
  pedidos: { pizza: { name: string }; tamanho: string; quantidade: number }[];
}

@Component({
  selector: 'app-home-screen',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home-screen.html',
  styleUrl: './home-screen.css',
})
export class HomeScreen implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api';

  pizzas: Pizza[] = [];
  adicionais: Adicional[] = [];
  pedidos: Order[] = [];
  carrinho: CartItem[] = this.loadCart();
  usuario: User | null = null;
  categoria = 'Todas';
  busca = '';
  loginAberto = false;
  carrinhoAberto = false;
  mensagem = '';
  login = { email: '', senha: '' };
  cadastro = { nome: '', email: '', senha: '' };
  entrega = { nome: '', telefone: '', endereco: '' };

  ngOnInit(): void {
    this.carregarCatalogo();
  }

  carregarCatalogo(): void {
    this.http.get<Pizza[]>(`${this.apiUrl}/pizzas`).subscribe({
      next: pizzas => (this.pizzas = pizzas),
      error: () => (this.mensagem = 'Não foi possível carregar o cardápio.'),
    });
    this.http.get<Adicional[]>(`${this.apiUrl}/adicionais`).subscribe({
      next: adicionais => (this.adicionais = adicionais),
      error: () => (this.mensagem = 'Não foi possível carregar os adicionais.'),
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

  precoItem(item: CartItem): number {
    const multiplicadores = { PEQUENA: 1, MEDIA: 1.5, GRANDE: 1.8 };
    const extras = item.adicionais.reduce((total, adicional) => total + Number(adicional.preco), 0);
    return (Number(item.pizza.valorBase) * multiplicadores[item.tamanho] + extras) * item.quantidade;
  }

  adicionar(pizza: Pizza): void {
    this.carrinho.push({ pizza, tamanho: 'MEDIA', quantidade: 1, adicionais: [] });
    this.persistirCarrinho();
  }

  alterarQuantidade(index: number, delta: number): void {
    this.carrinho[index].quantidade += delta;
    if (this.carrinho[index].quantidade <= 0) this.carrinho.splice(index, 1);
    this.persistirCarrinho();
  }

  remover(index: number): void {
    this.carrinho.splice(index, 1);
    this.persistirCarrinho();
  }

  abrirLogin(): void {
    this.loginAberto = true;
    this.mensagem = '';
  }

  autenticar(): void {
    this.http.post<User>(`${this.apiUrl}/auth/login`, this.login).subscribe({
      next: usuario => {
        this.usuario = usuario;
        this.entrega.nome = usuario.nome;
        this.loginAberto = false;
        this.carregarHistorico();
      },
      error: error => (this.mensagem = error.error?.message || 'E-mail ou senha inválidos.'),
    });
  }

  cadastrar(): void {
    this.http.post<User>(`${this.apiUrl}/usuarios`, { ...this.cadastro, ativo: true }).subscribe({
      next: usuario => {
        this.usuario = usuario;
        this.entrega.nome = usuario.nome;
        this.loginAberto = false;
      },
      error: error => (this.mensagem = error.error?.message || 'Não foi possível criar a conta.'),
    });
  }

  finalizar(): void {
    if (!this.usuario) {
      this.abrirLogin();
      this.mensagem = 'Entre ou crie sua conta para finalizar o pedido.';
      return;
    }
    if (!this.carrinho.length || !this.entrega.telefone || !this.entrega.endereco) {
      this.mensagem = 'Informe telefone, endereço e adicione pelo menos uma pizza.';
      return;
    }
    this.http.post(`${this.apiUrl}/clientes`, this.entrega).subscribe({
      next: (cliente: any) => this.http.post(`${this.apiUrl}/vendas`, {
        clienteId: cliente.id,
        usuarioId: this.usuario?.id,
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
          this.mensagem = 'Pedido realizado com sucesso!';
        },
        error: error => (this.mensagem = error.error?.message || 'Não foi possível finalizar o pedido.'),
      }),
      error: error => (this.mensagem = error.error?.message || 'Não foi possível salvar os dados de entrega.'),
    });
  }

  carregarHistorico(): void {
    if (!this.usuario) return;
    this.http.get<Order[]>(`${this.apiUrl}/vendas/historico/${this.usuario.id}`)
      .subscribe(orders => (this.pedidos = orders));
  }

  logout(): void {
    this.usuario = null;
    this.pedidos = [];
  }

  private persistirCarrinho(): void {
    localStorage.setItem('bella-pizza-carrinho', JSON.stringify(this.carrinho));
  }

  private loadCart(): CartItem[] {
    try {
      return JSON.parse(localStorage.getItem('bella-pizza-carrinho') || '[]') as CartItem[];
    } catch {
      return [];
    }
  }
}
