import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api-config';
import { Adicional } from '../models/adicional.model';
import { Pizza } from '../models/pizza.model';

@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private readonly http = inject(HttpClient);

  listarPizzas(): Observable<Pizza[]> {
    return this.http.get<Pizza[]>(`${API_URL}/pizzas`);
  }

  listarAdicionais(): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(`${API_URL}/adicionais`);
  }
}
