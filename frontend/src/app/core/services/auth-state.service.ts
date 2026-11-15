import { Injectable, signal } from '@angular/core';

import { Usuario } from '../models/usuario.model';

const CHAVE_USUARIO = 'dhm-pizzaria-usuario';

/**
 * Mantém o estado de sessão do atendente autenticado, compartilhado entre
 * a tela de login e o painel interno (cada uma em sua própria rota).
 */
@Injectable({ providedIn: 'root' })
export class AuthStateService {
  private readonly usuarioSignal = signal<Usuario | null>(this.lerUsuarioPersistido());

  readonly usuario = this.usuarioSignal.asReadonly();

  get estaAutenticado(): boolean {
    return this.usuarioSignal() !== null;
  }

  definirUsuario(usuario: Usuario): void {
    this.usuarioSignal.set(usuario);
    sessionStorage.setItem(CHAVE_USUARIO, JSON.stringify(usuario));
  }

  encerrarSessao(): void {
    this.usuarioSignal.set(null);
    sessionStorage.removeItem(CHAVE_USUARIO);
  }

  private lerUsuarioPersistido(): Usuario | null {
    try {
      const bruto = sessionStorage.getItem(CHAVE_USUARIO);
      return bruto ? (JSON.parse(bruto) as Usuario) : null;
    } catch {
      return null;
    }
  }
}
