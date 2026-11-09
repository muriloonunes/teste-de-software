////Atue como um Designer UI/UX Sênior e Desenvolvedor Front-end Especialista. Crie uma interface moderna, limpa e altamente atraente para o site de uma pizzaria premium chamada "Bella Pizza". O objetivo principal do site é converter visitas em pedidos de forma rápida e intuitiva.
////
////A interface deve seguir os seguintes critérios:
////
////1. PALETA DE CORES E ESTILO VISUAL:
////- Fundo: Modo escuro sofisticado (grafite/cinza escuro #121212) para fazer as fotos das pizzas se destacarem, ou um branco limpo com contrastes quentes.
////- Cores de Destaque: Vermelho vivo/terracota (#E53E3E) e Amarelo ouro (#ECC94B) para botões de ação (CTA) e elementos importantes (estimulam o apetite).
////- Tipografia: Serifada moderna e elegante para os títulos (ex: Playfair Display) e Sans-serif limpa e legível para os textos e botões (ex: Inter ou Roboto).
////
////2. ESTRUTURA DA PÁGINA (Single Page Responsiva):
////- HEADER: Logo à esquerda, menu de navegação simples (Cardápio, Sobre Nós, Contato) e um ícone de Carrinho flutuante à direita com contador de itens.
////- HERO SECTION (Destaque): Frase de impacto curta e apetitosa (Ex: "A verdadeira pizza artesanal, quentinha na sua mesa."). Inclua um botão de destaque "Ver Cardápio" e outro "Pedir Agora". Adicione uma imagem ou placeholder central de uma pizza premium com fumaça sutil ou efeito dinâmico.
////- CARDÁPIO INTERATIVO (Menu):
////  * Filtros por categorias no topo (Pizzas Tradicionais, Pizzas Especiais, Doces, Bebidas).
////  * Cards de produtos contendo: Imagem chamativa da pizza, Nome do sabor, Descrição curta dos ingredientes, Preço destacado e um botão "+" ou "Adicionar ao Carrinho".
////- CARRINHO / FINALIZAÇÃO: Uma barra lateral (sidebar) ou tela de checkout simplificada onde o usuário vê os itens escolhidos, pode alterar a quantidade (+/-), vê o cálculo automático do total com taxas de entrega e um botão destacado: "Finalizar Pedido via WhatsApp".
////
////3. INTERATIVIDADE E LÓGICA (HTML, CSS e JavaScript):
////- Os filtros do cardápio devem funcionar instantaneamente sem recarregar a página.
////- O botão de adicionar ao carrinho deve atualizar o contador do header e o valor total imediatamente.
////- Persistência de dados: Salve os itens do carrinho no localStorage para que o cliente não perca o pedido se atualizar a página.
////- Ao clicar em "Finalizar Pedido via WhatsApp", gere uma mensagem formatada automaticamente contendo: a lista de pizzas escolhidas, quantidades, valor total e um link direto que abre o WhatsApp do estabelecimento com o texto pronto.
////
////4. COMPATIBILIDADE E RESPONSIVIDADE:
////- O layout deve ser totalmente responsivo, priorizando a experiência Mobile-First (visto que a maioria dos pedidos é feita pelo celular).
////- Use animações suaves de hover nos botões e transições sutis ao adicionar itens ao carrinho.
//
//Gere o código completo, limpo e documentado utilizando tecnologias modernas (ex: HTML5 estruturado, CSS/Tailwind para estilização e JavaScript puro ou React, dependendo da sua preferência).

package com.dhm.testedesoftware.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Helpers comuns aos controllers REST. Mantém respostas 404 consistentes.
 */
public final class CrudController {
    private CrudController() {}

    public static <T> ResponseEntity<T> orNotFound(Optional<T> result) {
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static void requireId(Long id) {
        if (id == null || id < 1) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Id inválido.");
        }
    }
}
