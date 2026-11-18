package com.dhm.testedesoftware;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SeleniumE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String emailUsuario;
    private final String SENHA_PADRAO = "selenium123";
    private final String BACKEND_URL = System.getProperty("backend.url", "http://localhost:8080");
    private final String FRONTEND_URL = "http://localhost:4200";

    private Long idPizza;
    private Long idUsuario;

    @BeforeEach
    void prepararAmbiente() {
        //cria dados no backend, incluindo um usuario novo a cada teste pra evitar conflito de email
        Assumptions.assumeTrue(criarDadosBackend(),
            "Inicie o backend na porta 8080 para executar os testes Selenium.");

        inicializarNavegador(); //inicia o driver do chrome

        acessarFrontendOuAbortar(); //verifica se o frontend está ativo

        autenticarUsuario(); //faz login com o usuario criado
    }

    @AfterEach
    void fecharNavegador() {
        if (driver != null) {
            driver.quit();
        }

        deletarDadosBackend(); //apaga os dados criados pro teste
    }

    @Test
    void deveExibirCardapioEFiltrarPizzas() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search")))
            .sendKeys("Margherita E2E");

        boolean pizzaEncontrada = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pizza-grid")))
            .getText()
            .contains("Margherita E2E");

        Assertions.assertTrue(pizzaEncontrada);
    }

    @Test
    void deveAdicionarPizzaEAtualizarCarrinho() {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-pizza")))
            .click();

        boolean carrinhoAtualizado = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-button")))
            .getText()
            .contains("1");

        Assertions.assertTrue(carrinhoAtualizado);
    }

    private void inicializarNavegador() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1440,900");

            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } catch (WebDriverException e) {
            Assumptions.assumeTrue(false, "Chrome/ChromeDriver não disponível neste ambiente.");
        }
    }

    private void acessarFrontendOuAbortar() {
        try {
            driver.get(FRONTEND_URL);
        } catch (WebDriverException e) {
            driver.quit();
            Assumptions.assumeTrue(false, "O frontend não está rodando em " + FRONTEND_URL + ". Inicie a aplicação Angular com 'npm start'.");
        }
    }

    private boolean criarDadosBackend() {
        try {
            String jsonPizza = """
                {
                    "name": "Margherita E2E",
                    "descricao": "Pizza criada pelo teste Selenium",
                    "categoria": "Tradicionais",
                    "valorBase": 39.90
                }
                """;

            HttpResponse<String> pizzaCriada = fazerRequisicaoPost(BACKEND_URL + "/api/pizzas", jsonPizza);
            idPizza = extrairId(pizzaCriada.body());
            if (pizzaCriada.statusCode() != 201) return false;

            emailUsuario = "selenium-" + System.nanoTime() + "@bella.com";
            String jsonUsuario = """
                {
                    "nome": "Operador Selenium",
                    "email": "%s",
                    "senha": "%s",
                    "ativo": true
                }
                """.formatted(emailUsuario, SENHA_PADRAO);

            HttpResponse<String> usuarioCriado = fazerRequisicaoPost(BACKEND_URL + "/api/usuarios", jsonUsuario);
            idUsuario = extrairId(usuarioCriado.body());

            return usuarioCriado.statusCode() == 201;

        } catch (Exception e) {
            return false;
        }
    }

    private HttpResponse<String> fazerRequisicaoPost(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        return HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void autenticarUsuario() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")))
            .sendKeys(emailUsuario);

        driver.findElement(By.id("loginSenha"))
            .sendKeys(SENHA_PADRAO);

        driver.findElement(By.cssSelector("form.access-form button[type='submit']"))
            .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search")));
    }

    private void deletarDadosBackend() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            if (idPizza != null) {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BACKEND_URL + "/api/pizzas/" + idPizza))
                    .DELETE()
                    .build();

                httpClient.send(req, HttpResponse.BodyHandlers.discarding());
            }

            if (idUsuario != null) {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BACKEND_URL + "/api/usuarios/" + idUsuario))
                    .DELETE()
                    .build();

                httpClient.send(req, HttpResponse.BodyHandlers.discarding());
            }
        } catch (Exception ignored) {

        }
    }

    private Long extrairId(String json) {
        try {
            Pattern pattern = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
