import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthStateService } from '../../core/services/auth-state.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login-screen',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login-screen.html',
  styleUrl: './login-screen.css',
})
export class LoginScreen {
  private readonly authService = inject(AuthService);
  private readonly authState = inject(AuthStateService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  modoAcesso: 'login' | 'cadastro' = 'login';
  cadastroConcluido = false;
  nomeCadastrado = '';
  erro = '';
  emailConflito = false;
  carregando = false;

  mostrarSenhaLogin = false;
  mostrarSenhaCadastro = false;
  mostrarConfirmarSenha = false;

  login = { email: '', senha: '' };
  cadastro = { nome: '', email: '', senha: '', confirmarSenha: '' };

  errosCampos: Record<string, string> = {};
  camposTocados: Record<string, boolean> = {};

  alternarModoAcesso(modo: 'login' | 'cadastro'): void {
    this.modoAcesso = modo;
    this.cadastroConcluido = false;
    this.erro = '';
    this.emailConflito = false;
    this.errosCampos = {};
    this.camposTocados = {};
  }

  tocarCampo(campo: string): void {
    this.camposTocados[campo] = true;
    this.validarCampo(campo);
  }

  validarCampo(campo: string): void {
    delete this.errosCampos[campo];
    if (campo === 'email') {
      this.emailConflito = false;
    }

    if (campo === 'nome') {
      const nome = this.cadastro.nome.trim();
      if (!nome) {
        this.errosCampos['nome'] = 'O nome é obrigatório.';
      } else if (nome.length < 2) {
        this.errosCampos['nome'] = 'O nome deve ter pelo menos 2 caracteres.';
      }
    }

    if (campo === 'email') {
      const email = this.cadastro.email.trim();
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!email) {
        this.errosCampos['email'] = 'O e-mail é obrigatório.';
      } else if (!emailRegex.test(email)) {
        this.errosCampos['email'] = 'Informe um formato de e-mail válido (ex: atendente@pizzaria.com).';
      }
    }

    if (campo === 'senha') {
      if (!this.cadastro.senha || !this.cadastro.senha.trim()) {
        this.errosCampos['senha'] = 'A senha é obrigatória.';
      } else if (this.cadastro.senha.trim().length < 6) {
        this.errosCampos['senha'] = 'A senha deve ter pelo menos 6 caracteres.';
      }
      if (this.camposTocados['confirmarSenha'] || this.cadastro.confirmarSenha) {
        this.validarCampo('confirmarSenha');
      }
    }

    if (campo === 'confirmarSenha') {
      if (!this.cadastro.confirmarSenha) {
        this.errosCampos['confirmarSenha'] = 'A confirmação de senha é obrigatória.';
      } else if (this.cadastro.confirmarSenha !== this.cadastro.senha) {
        this.errosCampos['confirmarSenha'] = 'As senhas não coincidem.';
      }
    }
  }

  get senhaValida(): boolean {
    return this.cadastro.senha.trim().length >= 6;
  }

  get senhasCoincidem(): boolean {
    return !!this.cadastro.confirmarSenha && this.cadastro.senha === this.cadastro.confirmarSenha;
  }

  usarEmailConflitoNoLogin(): void {
    this.login.email = this.cadastro.email.trim();
    this.alternarModoAcesso('login');
  }

  autenticar(): void {
    this.erro = '';
    this.emailConflito = false;

    if (!this.login.email.trim() || !this.login.senha) {
      this.erro = 'Por favor, informe seu e-mail e senha para entrar.';
      return;
    }

    this.carregando = true;
    this.authService
      .login(this.login)
      .pipe(
        finalize(() => {
          this.carregando = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe({
        next: usuario => {
          this.authState.definirUsuario(usuario);
          this.router.navigate(['/interno']);
        },
        error: error => {
          if (error.status === 401) {
            this.erro = error.error?.mensagem || 'E-mail ou senha incorretos. Verifique suas credenciais.';
          } else if (error.status === 0) {
            this.erro = 'Não foi possível conectar ao servidor. Verifique se o backend está em execução.';
          } else {
            this.erro = error.error?.mensagem || error.error?.message || 'E-mail ou senha inválidos.';
          }
          this.cdr.markForCheck();
        },
      });
  }

  cadastrar(): void {
    this.erro = '';
    this.emailConflito = false;

    // Marca todos os campos como tocados e valida
    ['nome', 'email', 'senha', 'confirmarSenha'].forEach(campo => this.tocarCampo(campo));

    if (Object.keys(this.errosCampos).length > 0) {
      this.erro = 'Por favor, corrija os erros nos campos antes de continuar.';
      return;
    }

    this.carregando = true;
    const dadosEnvio = {
      nome: this.cadastro.nome.trim(),
      email: this.cadastro.email.trim(),
      senha: this.cadastro.senha,
    };

    this.authService
      .cadastrar(dadosEnvio)
      .pipe(
        finalize(() => {
          this.carregando = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe({
        next: usuario => {
          this.cadastroConcluido = true;
          this.nomeCadastrado = usuario.nome;
          this.login.email = dadosEnvio.email;
          this.cadastro = { nome: '', email: '', senha: '', confirmarSenha: '' };
          this.errosCampos = {};
          this.camposTocados = {};
          this.cdr.markForCheck();
        },
        error: error => {
          if (error.status === 409) {
            this.emailConflito = true;
            this.camposTocados['email'] = true;
            this.errosCampos = {
              ...this.errosCampos,
              email: 'Este e-mail já está cadastrado no sistema.',
            };
            this.erro = error.error?.mensagem || 'Já existe um atendente cadastrado com este e-mail.';
          } else if (error.status === 400) {
            if (error.error?.erros && typeof error.error.erros === 'object') {
              this.errosCampos = { ...error.error.erros };
            }
            this.erro = error.error?.mensagem || 'Dados inválidos. Por favor, revise as informações.';
          } else if (error.status === 0) {
            this.erro = 'Não foi possível conectar ao servidor. Verifique se o backend está ligado.';
          } else {
            this.erro = error.error?.mensagem || error.error?.message || 'Não foi possível criar a conta. Tente novamente.';
          }
          this.cdr.markForCheck();
        },
      });
  }

  irParaLogin(): void {
    this.cadastroConcluido = false;
    this.modoAcesso = 'login';
    this.erro = '';
  }
}
