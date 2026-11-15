export interface Usuario {
  id: number;
  nome: string;
  email: string;
  ativo: boolean;
}

export interface Credenciais {
  email: string;
  senha: string;
}

export interface NovoUsuario {
  nome: string;
  email: string;
  senha: string;
}
