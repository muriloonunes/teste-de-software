import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';

import { AdicionalFormulario, CatalogoService, PizzaFormulario } from '../../../core/services/catalogo.service';
import { Adicional } from '../../../core/models/adicional.model';
import { Pizza, arredondarPrecoPizza } from '../../../core/models/pizza.model';

const PIZZA_VAZIA: PizzaFormulario = { name: '', descricao: '', categoria: '', valorBase: 0 };
const ADICIONAL_VAZIO: AdicionalFormulario = { nome: '', descricao: '', preco: 0 };
const CATEGORIAS_PADRAO = ['Tradicionais', 'Especiais', 'Doces', 'Vegetarianas'];

@Component({
  selector: 'app-cardapio-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cardapio-manager.html',
  styleUrl: './cardapio-manager.css',
})
export class CardapioManagerComponent implements OnInit {
  private readonly catalogoService = inject(CatalogoService);
  private readonly cdr = inject(ChangeDetectorRef);

  @Output() readonly catalogoAtualizado = new EventEmitter<void>();

  secaoAtiva: 'pizzas' | 'adicionais' = 'pizzas';

  pizzas: Pizza[] = [];
  adicionais: Adicional[] = [];

  pizzaEmEdicao: Pizza | null = null;
  adicionalEmEdicao: Adicional | null = null;
  salvandoPizza = false;
  salvandoAdicional = false;

  confirmandoExclusaoPizzaId: number | null = null;
  confirmandoExclusaoAdicionalId: number | null = null;
  private timerConfirmacaoPizza: ReturnType<typeof setTimeout> | null = null;
  private timerConfirmacaoAdicional: ReturnType<typeof setTimeout> | null = null;

  formPizza: PizzaFormulario = { ...PIZZA_VAZIA };
  formAdicional: AdicionalFormulario = { ...ADICIONAL_VAZIO };
  errosPizza: Record<string, string> = {};
  errosAdicional: Record<string, string> = {};

  mensagem = '';
  tipoMensagem: 'sucesso' | 'erro' = 'sucesso';
  private timerMensagem: ReturnType<typeof setTimeout> | null = null;

  get categoriasSugeridas(): string[] {
    const existentes = this.pizzas
      .map(p => p.categoria?.trim())
      .filter((c): c is string => !!c);
    return Array.from(new Set([...CATEGORIAS_PADRAO, ...existentes]));
  }

  precoPorMultiplicador(valorBase: number, multiplicador: number): number {
    return arredondarPrecoPizza(Number(valorBase) * multiplicador);
  }

  ngOnInit(): void {
    this.carregarPizzas();
    this.carregarAdicionais();
  }

  carregarPizzas(): void {
    this.catalogoService.listarPizzas().subscribe({
      next: pizzas => {
        this.pizzas = pizzas;
        this.cdr.markForCheck();
      },
      error: () => this.exibirMensagem('Não foi possível carregar as pizzas.', 'erro'),
    });
  }

  carregarAdicionais(): void {
    this.catalogoService.listarAdicionais().subscribe({
      next: adicionais => {
        this.adicionais = adicionais;
        this.cdr.markForCheck();
      },
      error: () => this.exibirMensagem('Não foi possível carregar os adicionais.', 'erro'),
    });
  }

  editarPizza(pizza: Pizza): void {
    this.pizzaEmEdicao = pizza;
    this.errosPizza = {};
    this.formPizza = { name: pizza.name, descricao: pizza.descricao, categoria: pizza.categoria, valorBase: pizza.valorBase };
    this.cdr.markForCheck();
  }

  cancelarEdicaoPizza(): void {
    this.pizzaEmEdicao = null;
    this.errosPizza = {};
    this.formPizza = { ...PIZZA_VAZIA };
    this.cdr.markForCheck();
  }

  salvarPizza(): void {
    this.errosPizza = {};
    if (!this.formPizza.name.trim()) {
      this.errosPizza['name'] = 'O nome da pizza é obrigatório.';
    }
    if (!this.formPizza.descricao.trim()) {
      this.errosPizza['descricao'] = 'A descrição da pizza é obrigatória.';
    }
    if (!this.formPizza.categoria.trim()) {
      this.errosPizza['categoria'] = 'A categoria é obrigatória.';
    }
    if (!this.formPizza.valorBase || Number(this.formPizza.valorBase) <= 0) {
      this.errosPizza['valorBase'] = 'O preço base deve ser maior que zero.';
    }
    if (Object.keys(this.errosPizza).length > 0) {
      this.exibirMensagem('Preencha todos os campos obrigatórios da pizza corretamente.', 'erro');
      return;
    }

    const payload: PizzaFormulario = {
      name: this.formPizza.name.trim(),
      descricao: this.formPizza.descricao.trim(),
      categoria: this.formPizza.categoria.trim(),
      valorBase: Number(this.formPizza.valorBase),
    };

    const editando = !!this.pizzaEmEdicao;
    this.salvandoPizza = true;
    const operacao = this.pizzaEmEdicao
      ? this.catalogoService.atualizarPizza(this.pizzaEmEdicao.id, payload)
      : this.catalogoService.criarPizza(payload);

    operacao
      .pipe(
        finalize(() => {
          this.salvandoPizza = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe({
        next: () => {
          this.exibirMensagem(editando ? 'Pizza atualizada com sucesso!' : 'Pizza cadastrada com sucesso!', 'sucesso');
          this.cancelarEdicaoPizza();
          this.carregarPizzas();
          this.catalogoAtualizado.emit();
        },
        error: error => {
          if (error.error?.erros && typeof error.error.erros === 'object') {
            this.errosPizza = { ...error.error.erros };
          }
          this.exibirMensagem(error.error?.mensagem || error.error?.message || 'Não foi possível salvar a pizza.', 'erro');
        },
      });
  }

  excluirPizza(pizza: Pizza): void {
    if (this.confirmandoExclusaoPizzaId !== pizza.id) {
      this.confirmandoExclusaoPizzaId = pizza.id;
      if (this.timerConfirmacaoPizza) {
        clearTimeout(this.timerConfirmacaoPizza);
      }
      this.timerConfirmacaoPizza = setTimeout(() => {
        if (this.confirmandoExclusaoPizzaId === pizza.id) {
          this.confirmandoExclusaoPizzaId = null;
          this.cdr.markForCheck();
        }
      }, 3000);
      this.cdr.markForCheck();
      return;
    }

    if (this.timerConfirmacaoPizza) {
      clearTimeout(this.timerConfirmacaoPizza);
      this.timerConfirmacaoPizza = null;
    }
    this.confirmandoExclusaoPizzaId = null;

    this.catalogoService.excluirPizza(pizza.id).subscribe({
      next: () => {
        this.exibirMensagem('Pizza removida do cardápio.', 'sucesso');
        if (this.pizzaEmEdicao?.id === pizza.id) this.cancelarEdicaoPizza();
        this.carregarPizzas();
        this.catalogoAtualizado.emit();
      },
      error: error => this.exibirMensagem(error.error?.mensagem || error.error?.message || 'Não foi possível remover a pizza (pode estar vinculada a um pedido).', 'erro'),
    });
  }

  editarAdicional(adicional: Adicional): void {
    this.adicionalEmEdicao = adicional;
    this.errosAdicional = {};
    this.formAdicional = { nome: adicional.nome, descricao: adicional.descricao, preco: adicional.preco };
    this.cdr.markForCheck();
  }

  cancelarEdicaoAdicional(): void {
    this.adicionalEmEdicao = null;
    this.errosAdicional = {};
    this.formAdicional = { ...ADICIONAL_VAZIO };
    this.cdr.markForCheck();
  }

  salvarAdicional(): void {
    this.errosAdicional = {};
    if (!this.formAdicional.nome.trim()) {
      this.errosAdicional['nome'] = 'O nome do adicional é obrigatório.';
    }
    if (!this.formAdicional.preco || Number(this.formAdicional.preco) <= 0) {
      this.errosAdicional['preco'] = 'O preço do adicional deve ser maior que zero.';
    }
    if (Object.keys(this.errosAdicional).length > 0) {
      this.exibirMensagem('Preencha o nome e um preço válido para o adicional.', 'erro');
      return;
    }

    const payload: AdicionalFormulario = {
      nome: this.formAdicional.nome.trim(),
      descricao: (this.formAdicional.descricao ?? '').trim(),
      preco: Number(this.formAdicional.preco),
    };

    const editando = !!this.adicionalEmEdicao;
    this.salvandoAdicional = true;
    const operacao = this.adicionalEmEdicao
      ? this.catalogoService.atualizarAdicional(this.adicionalEmEdicao.id, payload)
      : this.catalogoService.criarAdicional(payload);

    operacao
      .pipe(
        finalize(() => {
          this.salvandoAdicional = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe({
        next: () => {
          this.exibirMensagem(editando ? 'Adicional atualizado com sucesso!' : 'Adicional cadastrado com sucesso!', 'sucesso');
          this.cancelarEdicaoAdicional();
          this.carregarAdicionais();
          this.catalogoAtualizado.emit();
        },
        error: error => {
          if (error.error?.erros && typeof error.error.erros === 'object') {
            this.errosAdicional = { ...error.error.erros };
          }
          this.exibirMensagem(error.error?.mensagem || error.error?.message || 'Não foi possível salvar o adicional.', 'erro');
        },
      });
  }

  excluirAdicional(adicional: Adicional): void {
    if (this.confirmandoExclusaoAdicionalId !== adicional.id) {
      this.confirmandoExclusaoAdicionalId = adicional.id;
      if (this.timerConfirmacaoAdicional) {
        clearTimeout(this.timerConfirmacaoAdicional);
      }
      this.timerConfirmacaoAdicional = setTimeout(() => {
        if (this.confirmandoExclusaoAdicionalId === adicional.id) {
          this.confirmandoExclusaoAdicionalId = null;
          this.cdr.markForCheck();
        }
      }, 3000);
      this.cdr.markForCheck();
      return;
    }

    if (this.timerConfirmacaoAdicional) {
      clearTimeout(this.timerConfirmacaoAdicional);
      this.timerConfirmacaoAdicional = null;
    }
    this.confirmandoExclusaoAdicionalId = null;

    this.catalogoService.excluirAdicional(adicional.id).subscribe({
      next: () => {
        this.exibirMensagem('Adicional removido.', 'sucesso');
        if (this.adicionalEmEdicao?.id === adicional.id) this.cancelarEdicaoAdicional();
        this.carregarAdicionais();
        this.catalogoAtualizado.emit();
      },
      error: error => this.exibirMensagem(error.error?.mensagem || error.error?.message || 'Não foi possível remover o adicional (pode estar vinculado a um pedido).', 'erro'),
    });
  }

  private exibirMensagem(texto: string, tipo: 'sucesso' | 'erro'): void {
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
}
