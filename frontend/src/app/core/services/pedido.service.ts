import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api-config';
import { Cliente, DadosEntrega, NovaVenda, Pagamento, Pedido } from '../models/pedido.model';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private readonly http = inject(HttpClient);

  listarPagamentos(): Observable<Pagamento[]> {
    return this.http.get<Pagamento[]>(`${API_URL}/pagamentos`);
  }

  criarCliente(dadosEntrega: DadosEntrega): Observable<Cliente> {
    return this.http.post<Cliente>(`${API_URL}/clientes`, dadosEntrega);
  }

  criarVenda(venda: NovaVenda): Observable<Pedido> {
    return this.http.post<Pedido>(`${API_URL}/vendas`, venda);
  }

  listarHistorico(usuarioId: number): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${API_URL}/vendas/historico/${usuarioId}`);
  }
}
