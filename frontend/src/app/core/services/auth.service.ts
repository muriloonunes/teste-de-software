import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api-config';
import { AtualizarUsuario, Credenciais, NovoUsuario, Usuario } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  login(credenciais: Credenciais): Observable<Usuario> {
    return this.http.post<Usuario>(`${API_URL}/auth/login`, credenciais);
  }

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${API_URL}/usuarios`);
  }

  buscarPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${API_URL}/usuarios/${id}`);
  }

  cadastrar(novoUsuario: NovoUsuario): Observable<Usuario> {
    return this.http.post<Usuario>(`${API_URL}/usuarios`, {
      ...novoUsuario,
      ativo: novoUsuario.ativo ?? true,
    });
  }

  atualizar(id: number, usuario: AtualizarUsuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${API_URL}/usuarios/${id}`, usuario);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/usuarios/${id}`);
  }
}
