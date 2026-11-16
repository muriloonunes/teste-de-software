import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  AdicionalFormulario,
  CatalogoService,
  PizzaFormulario,
} from '../../../core/services/catalogo.service';
import { Adicional } from '../../../core/models/adicional.model';
import { Pizza } from '../../../core/models/pizza.model';

@Component({
  selector: 'app-cardapio-manager',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe],
  template: `
    <section class="manager">
      <div class="heading">
        <div><small>ADMINISTRAÇÃO</small><h2>Gerenciar cardápio</h2></div>
        <p *ngIf="mensagem" class="message">{{ mensagem }}</p>
      </div>

      <div class="forms">
        <form (ngSubmit)="salvarPizza()">
          <h3>{{ pizzaEditando ? 'Editar pizza' : 'Nova pizza' }}</h3>
          <input [(ngModel)]="pizzaForm.name" name="pizzaName" placeholder="Nome" required>
          <input [(ngModel)]="pizzaForm.categoria" name="pizzaCategoria" placeholder="Categoria" required>
          <input [(ngModel)]="pizzaForm.descricao" name="pizzaDescricao" placeholder="Descrição" required>
          <input [(ngModel)]="pizzaForm.valorBase" name="pizzaValor" type="number" min="0" step="0.01" placeholder="Preço" required>
          <button type="submit">{{ pizzaEditando ? 'Salvar alterações' : 'Adicionar pizza' }}</button>
          <button *ngIf="pizzaEditando" type="button" class="secondary" (click)="cancelarPizza()">Cancelar</button>
        </form>

        <form (ngSubmit)="salvarAdicional()">
          <h3>{{ adicionalEditando ? 'Editar adicional' : 'Novo adicional' }}</h3>
          <input [(ngModel)]="adicionalForm.nome" name="adicionalNome" placeholder="Nome" required>
          <input [(ngModel)]="adicionalForm.descricao" name="adicionalDescricao" placeholder="Descrição" required>
          <input [(ngModel)]="adicionalForm.preco" name="adicionalPreco" type="number" min="0" step="0.01" placeholder="Preço" required>
          <button type="submit">{{ adicionalEditando ? 'Salvar alterações' : 'Adicionar adicional' }}</button>
          <button *ngIf="adicionalEditando" type="button" class="secondary" (click)="cancelarAdicional()">Cancelar</button>
        </form>
      </div>

      <div class="lists">
        <div>
          <h3>Pizzas</h3>
          <p *ngFor="let pizza of pizzas" class="row">
            <span><strong>{{ pizza.name }}</strong> · {{ pizza.categoria }} · {{ pizza.valorBase | currency:'BRL' }}</span>
            <span><button type="button" (click)="editarPizza(pizza)">Editar</button><button type="button" class="danger" (click)="excluirPizza(pizza)">Excluir</button></span>
          </p>
        </div>
        <div>
          <h3>Adicionais</h3>
          <p *ngFor="let adicional of adicionais" class="row">
            <span><strong>{{ adicional.nome }}</strong> · {{ adicional.preco | currency:'BRL' }}</span>
            <span><button type="button" (click)="editarAdicional(adicional)">Editar</button><button type="button" class="danger" (click)="excluirAdicional(adicional)">Excluir</button></span>
          </p>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .manager { background: var(--dhm-bg-surface); border: 1px solid var(--dhm-border-subtle); border-radius: var(--dhm-radius-md); padding: 24px; }
    .heading, .row { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
    h2 { margin: 8px 0 20px; } h3 { margin: 0 0 12px; }
    .forms, .lists { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 24px; }
    form, .lists > div { display: flex; flex-direction: column; gap: 10px; }
    input { background: var(--dhm-bg-input); border: 1px solid var(--dhm-border-medium); border-radius: var(--dhm-radius-sm); color: var(--dhm-white); padding: 10px; }
    button { background: var(--dhm-red-500); border: 0; border-radius: var(--dhm-radius-sm); color: var(--dhm-white); cursor: pointer; padding: 9px 12px; }
    button.secondary { background: var(--dhm-border-medium); } button.danger { background: transparent; color: var(--dhm-red-500); }
    .row { border-bottom: 1px solid var(--dhm-border-subtle); padding: 10px 0; margin: 0; } .row span:last-child { display: flex; gap: 6px; }
    .message { color: var(--dhm-yellow-400); } small { color: var(--dhm-yellow-400); letter-spacing: 2px; }
  `],
})
export class CardapioManagerComponent {
  @Output() readonly catalogoAtualizado = new EventEmitter<void>();

  private readonly catalogoService = inject(CatalogoService);
  pizzas: Pizza[] = [];
  adicionais: Adicional[] = [];
  pizzaEditando: Pizza | null = null;
  adicionalEditando: Adicional | null = null;
  mensagem = '';
  pizzaForm: PizzaFormulario = { name: '', descricao: '', categoria: '', valorBase: 0 };
  adicionalForm: AdicionalFormulario = { nome: '', descricao: '', preco: 0 };

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.catalogoService.listarPizzas().subscribe(pizzas => (this.pizzas = pizzas));
    this.catalogoService.listarAdicionais().subscribe(adicionais => (this.adicionais = adicionais));
  }

  salvarPizza(): void {
    const request = this.pizzaEditando
      ? this.catalogoService.atualizarPizza(this.pizzaEditando.id, this.pizzaForm)
      : this.catalogoService.criarPizza(this.pizzaForm);
    request.subscribe(() => {
      this.mensagem = 'Pizza salva com sucesso.';
      this.cancelarPizza();
      this.carregar();
      this.catalogoAtualizado.emit();
    });
  }

  salvarAdicional(): void {
    const request = this.adicionalEditando
      ? this.catalogoService.atualizarAdicional(this.adicionalEditando.id, this.adicionalForm)
      : this.catalogoService.criarAdicional(this.adicionalForm);
    request.subscribe(() => {
      this.mensagem = 'Adicional salvo com sucesso.';
      this.cancelarAdicional();
      this.carregar();
      this.catalogoAtualizado.emit();
    });
  }

  editarPizza(pizza: Pizza): void {
    this.pizzaEditando = pizza;
    this.pizzaForm = { name: pizza.name, descricao: pizza.descricao, categoria: pizza.categoria, valorBase: pizza.valorBase };
  }

  editarAdicional(adicional: Adicional): void {
    this.adicionalEditando = adicional;
    this.adicionalForm = { nome: adicional.nome, descricao: adicional.descricao, preco: adicional.preco };
  }

  excluirPizza(pizza: Pizza): void {
    if (!confirm(`Excluir a pizza "${pizza.name}"?`)) return;
    this.catalogoService.excluirPizza(pizza.id).subscribe(() => {
      this.carregar();
      this.catalogoAtualizado.emit();
    });
  }

  excluirAdicional(adicional: Adicional): void {
    if (!confirm(`Excluir o adicional "${adicional.nome}"?`)) return;
    this.catalogoService.excluirAdicional(adicional.id).subscribe(() => {
      this.carregar();
      this.catalogoAtualizado.emit();
    });
  }

  cancelarPizza(): void {
    this.pizzaEditando = null;
    this.pizzaForm = { name: '', descricao: '', categoria: '', valorBase: 0 };
  }

  cancelarAdicional(): void {
    this.adicionalEditando = null;
    this.adicionalForm = { nome: '', descricao: '', preco: 0 };
  }
}
