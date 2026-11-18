package com.dhm.testedesoftware;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SeleniumE2ETest {
    private WebDriver driver;
    private WebDriverWait wait;
    private String email;
    private final String senha = "selenium123";

    @BeforeEach
    void abrirNavegadorQuandoFrontendEstiverDisponivel() {
        String frontendUrl = System.getProperty("frontend.url", "http://localhost:4200");
        Assumptions.assumeTrue(frontendDisponivel(frontendUrl),
                "Inicie o Angular com 'npm start' para executar os testes Selenium.");
        Assumptions.assumeTrue(criarPizzaDeTeste(),
                "Inicie o backend na porta 8080 para executar os testes Selenium.");
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1440,900");
            driver = new org.openqa.selenium.chrome.ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.get(frontendUrl);
            autenticarUsuarioDeTeste();
        } catch (org.openqa.selenium.WebDriverException exception) {
            Assumptions.assumeTrue(false, "Chrome/ChromeDriver não disponível neste ambiente.");
        }
    }

    @AfterEach
    void fecharNavegador() {
        if (driver != null) driver.quit();
    }

    @Test
    void deveExibirCardapioEFiltrarPizzas() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search"))).sendKeys("Margherita E2E");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pizza-grid")))
                .getText().contains("Margherita E2E"));
    }

    @Test
    void deveAdicionarPizzaEAtualizarCarrinho() {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-pizza"))).click();
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-button")))
                .getText().contains("1"));
    }

    private boolean frontendDisponivel(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            return connection.getResponseCode() < 500;
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean criarPizzaDeTeste() {
        try {
            String body = """
                    {"name":"Margherita E2E","descricao":"Pizza criada pelo teste Selenium",
                     "categoria":"Tradicionais","valorBase":39.90}
                    """;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(System.getProperty("backend.url", "http://localhost:8080") + "/api/pizzas"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) {
                return false;
            }
            email = "selenium-" + System.nanoTime() + "@bella.com";
            String usuario = """
                    {"nome":"Operador Selenium","email":"%s","senha":"%s","ativo":true}
                    """.formatted(email, senha);
            HttpRequest cadastro = HttpRequest.newBuilder()
                    .uri(URI.create(System.getProperty("backend.url", "http://localhost:8080") + "/api/usuarios"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(usuario))
                    .build();
            return HttpClient.newHttpClient().send(cadastro, HttpResponse.BodyHandlers.discarding()).statusCode() == 201;
        } catch (Exception exception) {
            return false;
        }
    }

    private void autenticarUsuarioDeTeste() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail"))).sendKeys(email);
        driver.findElement(By.id("loginSenha")).sendKeys(senha);
        driver.findElement(By.cssSelector("form.access-form button[type='submit']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search")));
    }
}
