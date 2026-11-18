import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { AtualizarUsuario, NovoUsuario, Usuario } from '../../../core/models/usuario.model';
import { AuthStateService } from '../../../core/services/auth-state.service';
import { AuthService } from '../../../core/services/auth.service';

interface UsuarioFormulario {
  nome: string;
  email: string;
  senha: string;
  confirmarSenha: string;
  ativo: boolean;
}

const USUARIO_VAZIO: UsuarioFormulario = {
  nome: '',
  email: '',
  senha: '',
  confirmarSenha: '',
  ativo: true,
};

@Component({
  selector: 'app-usuarios-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios-manager.html',
  styleUrl: './usuarios-manager.css',
})
export class UsuariosManagerComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly authState = inject(AuthStateService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  @Output() readonly usuariosAtualizados = new EventEmitter<number>();

  readonly usuarioLogado = this.authState.usuario;

  usuarios: Usuario[] = [];
  busca = '';
  usuarioEmEdicao: Usuario | null = null;
  salvando = false;

  mostrarSenha = false;
  mostrarConfirmarSenha = false;

  confirmandoExclusaoId: number | null = null;
  private timerConfirmacaoExclusao: ReturnType<typeof setTimeout> | null = null;

  formUsuario: UsuarioFormulario = { ...USUARIO_VAZIO };
  erros: Record<string, string> = {};
  camposTocados: Record<string, boolean> = {};

  mensagem = '';
  tipoMensagem: 'sucesso' | 'erro' = 'sucesso';
  private timerMensagem: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  get usuariosFiltrados(): Usuario[] {
    const termo = this.busca.toLowerCase().trim();
    if (!termo) return this.usuarios;
    return this.usuarios.filter(u =>
      `${u.nome} ${u.email} ${u.ativo ? 'ativo' : 'inativo'}`.toLowerCase().includes(termo),
    );
  }

  get totalAtivos(): number {
    return this.usuarios.filter(u => u.ativo).length;
  }

  get tamanhoSenhaAtual(): number {
    return this.formUsuario.senha.trim().length;
  }

  get senhaValida(): boolean {
    return this.tamanhoSenhaAtual >= 6;
  }

  get senhaInvalida(): boolean {
    if (this.usuarioEmEdicao) {
      return this.formUsuario.senha.length > 0 && !this.senhaValida;
    }
    return (this.camposTocados['senha'] || this.formUsuario.senha.length > 0) && !this.senhaValida;
  }

  get senhasCoincidem(): boolean {
    return (
      this.formUsuario.confirmarSenha.length > 0 &&
      this.formUsuario.senha === this.formUsuario.confirmarSenha
    );
  }

  tocarCampo(campo: string): void {
    this.camposTocados[campo] = true;
    this.validarCampo(campo);
  }

  aoAlterarCampo(campo: string): void {
    this.camposTocados[campo] = true;
    this.validarCampo(campo);
  }

  validarCampo(campo: string): void {
    delete this.erros[campo];
    const editando = !!this.usuarioEmEdicao;

    if (campo === 'nome') {
      const nome = this.formUsuario.nome.trim();
      if (!nome) {
        this.erros['nome'] = 'O nome é obrigatório.';
      } else if (nome.length < 2) {
        this.erros['nome'] = 'O nome deve ter pelo menos 2 caracteres.';
      }
    }

    if (campo === 'email') {
      const email = this.formUsuario.email.trim();
      if (!email) {
        this.erros['email'] = 'O e-mail é obrigatório.';
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        this.erros['email'] = 'Informe um formato de e-mail válido.';
      }
    }

    if (campo === 'senha') {
      const senha = this.formUsuario.senha;
      if (!editando) {
        if (!senha || !senha.trim()) {
          this.erros['senha'] = 'A senha é obrigatória.';
        } else if (senha.trim().length < 6) {
          this.erros['senha'] = 'A senha deve ter pelo menos 6 caracteres.';
        }
        if (this.camposTocados['confirmarSenha'] || this.formUsuario.confirmarSenha) {
          this.validarCampo('confirmarSenha');
        }
      } else {
        if (senha.length > 0 && senha.trim().length < 6) {
          this.erros['senha'] = 'A nova senha deve ter pelo menos 6 caracteres.';
        }
      }
    }

    if (campo === 'confirmarSenha' && !editando) {
      if (!this.formUsuario.confirmarSenha) {
        this.erros['confirmarSenha'] = 'Confirme a senha para continuar.';
      } else if (this.formUsuario.confirmarSenha !== this.formUsuario.senha) {
        this.erros['confirmarSenha'] = 'As senhas não coincidem.';
      }
    }
  }

  carregarUsuarios(): void {
    this.authService.listar().subscribe({
      next: usuarios => {
        this.usuarios = usuarios;
        this.usuariosAtualizados.emit(usuarios.length);
        this.cdr.markForCheck();
      },
      error: () => this.exibirMensagem('Não foi possível carregar a lista de usuários.', 'erro'),
    });
  }

  editarUsuario(usuario: Usuario): void {
    this.usuarioEmEdicao = usuario;
    this.erros = {};
    this.camposTocados = {};
    this.mostrarSenha = false;
    this.mostrarConfirmarSenha = false;
    this.formUsuario = {
      nome: usuario.nome,
      email: usuario.email,
      senha: '',
      confirmarSenha: '',
      ativo: usuario.ativo,
    };
    this.cdr.markForCheck();
  }

  cancelarEdicao(): void {
    this.usuarioEmEdicao = null;
    this.erros = {};
    this.camposTocados = {};
    this.mostrarSenha = false;
    this.mostrarConfirmarSenha = false;
    this.formUsuario = { ...USUARIO_VAZIO };
    this.cdr.markForCheck();
  }

  salvarUsuario(): void {
    const editando = !!this.usuarioEmEdicao;
    const camposParaValidar = editando
      ? ['nome', 'email', 'senha']
      : ['nome', 'email', 'senha', 'confirmarSenha'];

    camposParaValidar.forEach(c => this.tocarCampo(c));

    if (Object.keys(this.erros).length > 0) {
      this.exibirMensagem('Corrija os campos destacados antes de salvar o usuário.', 'erro');
      return;
    }

    const nome = this.formUsuario.nome.trim();
    const email = this.formUsuario.email.trim();
    const senha = this.formUsuario.senha.trim();

    this.salvando = true;

    if (this.usuarioEmEdicao) {
      const id = this.usuarioEmEdicao.id;
      const payload: AtualizarUsuario = {
        nome,
        email,
        ativo: this.formUsuario.ativo,
        ...(senha.length > 0 ? { senha } : {}),
      };

      this.authService
        .atualizar(id, payload)
        .pipe(
          finalize(() => {
            this.salvando = false;
            this.cdr.markForCheck();
          }),
        )
        .subscribe({
          next: atualizado => {
            if (this.usuarioLogado()?.id === atualizado.id) {
              if (!atualizado.ativo) {
                this.authState.encerrarSessao();
                void this.router.navigate(['/login']);
                return;
              }
              this.authState.definirUsuario(atualizado);
            }
            this.exibirMensagem('Usuário atualizado com sucesso!', 'sucesso');
            this.cancelarEdicao();
            this.carregarUsuarios();
          },
          error: error => {
            if (error.error?.erros && typeof error.error.erros === 'object') {
              this.erros = { ...error.error.erros };
            }
            this.exibirMensagem(
              error.error?.mensagem || error.error?.message || 'Não foi possível atualizar o usuário.',
              'erro',
            );
          },
        });
    } else {
      const payload: NovoUsuario = {
        nome,
        email,
        senha,
        ativo: this.formUsuario.ativo,
      };

      this.authService
        .cadastrar(payload)
        .pipe(
          finalize(() => {
            this.salvando = false;
            this.cdr.markForCheck();
          }),
        )
        .subscribe({
          next: () => {
            this.exibirMensagem('Usuário cadastrado com sucesso!', 'sucesso');
            this.cancelarEdicao();
            this.carregarUsuarios();
          },
          error: error => {
            if (error.error?.erros && typeof error.error.erros === 'object') {
              this.erros = { ...error.error.erros };
            }
            this.exibirMensagem(
              error.error?.mensagem || error.error?.message || 'Não foi possível cadastrar o usuário.',
              'erro',
            );
          },
        });
    }
  }

  alternarAtivo(usuario: Usuario): void {
    const payload: AtualizarUsuario = {
      nome: usuario.nome,
      email: usuario.email,
      ativo: !usuario.ativo,
    };

    this.authService.atualizar(usuario.id, payload).subscribe({
      next: atualizado => {
        if (this.usuarioLogado()?.id === atualizado.id && !atualizado.ativo) {
          this.authState.encerrarSessao();
          void this.router.navigate(['/login']);
          return;
        }
        this.exibirMensagem(
          atualizado.ativo ? `Operador ${atualizado.nome} reativado.` : `Operador ${atualizado.nome} desativado.`,
          'sucesso',
        );
        if (this.usuarioEmEdicao?.id === atualizado.id) {
          this.formUsuario.ativo = atualizado.ativo;
        }
        this.carregarUsuarios();
      },
      error: error =>
        this.exibirMensagem(
          error.error?.mensagem || error.error?.message || 'Não foi possível alterar o status do usuário.',
          'erro',
        ),
    });
  }

  excluirUsuario(usuario: Usuario): void {
    if (this.confirmandoExclusaoId !== usuario.id) {
      this.confirmandoExclusaoId = usuario.id;
      if (this.timerConfirmacaoExclusao) {
        clearTimeout(this.timerConfirmacaoExclusao);
      }
      this.timerConfirmacaoExclusao = setTimeout(() => {
        if (this.confirmandoExclusaoId === usuario.id) {
          this.confirmandoExclusaoId = null;
          this.cdr.markForCheck();
        }
      }, 3000);
      this.cdr.markForCheck();
      return;
    }

    if (this.timerConfirmacaoExclusao) {
      clearTimeout(this.timerConfirmacaoExclusao);
      this.timerConfirmacaoExclusao = null;
    }
    this.confirmandoExclusaoId = null;

    const excluindoProprioUsuario = this.usuarioLogado()?.id === usuario.id;

    this.authService.excluir(usuario.id).subscribe({
      next: () => {
        if (excluindoProprioUsuario) {
          this.authState.encerrarSessao();
          void this.router.navigate(['/login']);
          return;
        }
        this.exibirMensagem('Usuário removido com sucesso.', 'sucesso');
        if (this.usuarioEmEdicao?.id === usuario.id) {
          this.cancelarEdicao();
        }
        this.carregarUsuarios();
      },
      error: error =>
        this.exibirMensagem(
          error.error?.mensagem ||
            error.error?.message ||
            'Não foi possível excluir o usuário (pode possuir pedidos registrados — você pode desativá-lo).',
          'erro',
        ),
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
    }, 4500);
  }
}
