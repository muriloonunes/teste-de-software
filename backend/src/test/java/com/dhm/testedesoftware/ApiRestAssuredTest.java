package com.dhm.testedesoftware;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @Test
    void deveCadastrarPizzaPelaApi() {
        String pizza = """
                {
                  "name": "Margherita",
                  "descricao": "Molho de tomate, mozzarella e manjericão",
                  "categoria": "Tradicionais",
                  "valorBase": 42.90
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(pizza)
                .when()
                .post("/api/pizzas")
                .then()
                .statusCode(201)
                .body("categoria", equalTo("Tradicionais"))
                .body("id", org.hamcrest.Matchers.notNullValue());

    }

    @Test
    void deveRecusarPizzaComPrecoInvalido() {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "name": "Pizza inválida",
                        "descricao": "Sem preço válido",
                        "categoria": "Tradicionais",
                        "valorBase": 0
                      }
                      """)
                .when()
                .post("/api/pizzas")
                .then()
                .statusCode(400)
                .body("mensagem", equalTo("Dados inválidos."));
    }

    @Test
    void deveRecusarVendaSemItens() {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "pedidos": [],
                        "adicionais": []
                      }
                      """)
                .when()
                .post("/api/vendas")
                .then()
                .statusCode(400)
                .body("mensagem", equalTo("Dados inválidos."));
    }

    @Test
    void deveCriarPedidoComPizzaTamanhoQuantidadeEAdicional() {
        Long pizzaId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "name": "Pizza do teste de pedido",
                        "descricao": "Pizza usada no fluxo principal",
                        "categoria": "Especiais",
                        "valorBase": 40.00
                      }
                      """)
                .when()
                .post("/api/pizzas")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        Long adicionalId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "nome": "Borda do teste de pedido",
                        "descricao": "Adicional usado no fluxo principal",
                        "categoria": "Extra",
                        "preco": 6.00
                      }
                      """)
                .when()
                .post("/api/adicionais")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "pedidos": [
                          {"pizzaId": %d, "tamanho": "GRANDE", "quantidade": 2}
                        ],
                        "adicionais": [
                          {"adicionalId": %d, "quantidade": 1}
                        ]
                      }
                      """.formatted(pizzaId, adicionalId))
                .when()
                .post("/api/vendas")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("pedidos[0].tamanho", equalTo("GRANDE"))
                .body("pedidos[0].quantidade", equalTo(2))
                .body("adicionais[0].quantidade", equalTo(1))
                .body("valor", equalTo(150.00F));
    }

    @Test
    void deveCadastrarUsuarioPelaApi() {
        String email = "rest-" + System.nanoTime() + "@bella.com";

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "nome": "Operador REST",
                        "email": "%s",
                        "senha": "segredo123",
                        "ativo": true
                      }
                      """.formatted(email))
                .when()
                .post("/api/usuarios")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("email", equalTo(email))
                .body("ativo", equalTo(true));
    }
}
