import { Adicional } from './adicional.model';
import { Pizza } from './pizza.model';

export type TamanhoPizza = 'PEQUENA' | 'MEDIA' | 'GRANDE';

export interface ItemCarrinho {
  pizza: Pizza;
  tamanho: TamanhoPizza;
  quantidade: number;
  adicionais: Adicional[];
}

export interface DadosEntrega {
  nome: string;
  telefone: string;
  endereco: string;
}

export interface Cliente {
  id: number;
  nome: string;
  telefone: string;
  endereco: string;
}

export interface NovaVenda {
  clienteId: number;
  usuarioId: number | undefined;
  pedidos: { pizzaId: number; tamanho: TamanhoPizza; quantidade: number }[];
  adicionais: { adicionalId: number; quantidade: number }[];
}

export interface Pedido {
  id: number;
  valor: number;
  cliente: Cliente;
  pedidos: { pizza: { name: string }; tamanho: string; quantidade: number }[];
}
