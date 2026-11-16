import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api-config';
import { Adicional } from '../models/adicional.model';
import { Pizza } from '../models/pizza.model';

export type PizzaFormulario = Omit<Pizza, 'id'>;
export type AdicionalFormulario = Omit<Adicional, 'id'>;

@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private readonly http = inject(HttpClient);

  listarPizzas(): Observable<Pizza[]> {
    return this.http.get<Pizza[]>(`${API_URL}/pizzas`);
  }

  criarPizza(pizza: PizzaFormulario): Observable<Pizza> {
    return this.http.post<Pizza>(`${API_URL}/pizzas`, pizza);
  }

  atualizarPizza(id: number, pizza: PizzaFormulario): Observable<Pizza> {
    return this.http.put<Pizza>(`${API_URL}/pizzas/${id}`, pizza);
  }

  excluirPizza(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/pizzas/${id}`);
  }

  listarAdicionais(): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(`${API_URL}/adicionais`);
  }

  criarAdicional(adicional: AdicionalFormulario): Observable<Adicional> {
    return this.http.post<Adicional>(`${API_URL}/adicionais`, adicional);
  }

  atualizarAdicional(id: number, adicional: AdicionalFormulario): Observable<Adicional> {
    return this.http.put<Adicional>(`${API_URL}/adicionais/${id}`, adicional);
  }

  excluirAdicional(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/adicionais/${id}`);
  }
}
