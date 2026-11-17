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

export interface Pagamento {
  id: number;
  nome: string;
  taxa: number;
  tipoPagamento: string;
  parcelas: number;
}

export interface NovaVenda {
  clienteId: number;
  usuarioId: number | undefined;
  pagamentoId?: number;
  pedidos: { pizzaId: number; tamanho: TamanhoPizza; quantidade: number }[];
  adicionais: { adicionalId: number; quantidade: number }[];
}

export interface Pedido {
  id: number;
  valor: number;
  cliente: Cliente;
  pagamento?: Pagamento;
  pedidos: { pizza: { name: string }; tamanho: string; quantidade: number }[];
  adicionais?: { adicional: { nome: string }; precoUnitario: number; quantidade: number }[];
}
