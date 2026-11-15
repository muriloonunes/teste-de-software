import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api-config';
import { Credenciais, NovoUsuario, Usuario } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  login(credenciais: Credenciais): Observable<Usuario> {
    return this.http.post<Usuario>(`${API_URL}/auth/login`, credenciais);
  }

  cadastrar(novoUsuario: NovoUsuario): Observable<Usuario> {
    return this.http.post<Usuario>(`${API_URL}/usuarios`, { ...novoUsuario, ativo: true });
  }
}
