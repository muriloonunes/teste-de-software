package com.dhm.testedesoftware;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
}
