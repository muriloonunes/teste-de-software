package com.dhm.testedesoftware;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SeleniumE2ETest {
    private WebDriver driver;

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
            driver.get(frontendUrl);
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
        driver.findElement(By.id("search")).sendKeys("Margherita");
        assertTrue(driver.findElement(By.cssSelector(".pizza-grid")).getText().contains("Margherita"));
    }

    @Test
    void deveAdicionarPizzaEAtualizarCarrinho() {
        driver.findElement(By.cssSelector(".pizza-card button")).click();
        assertTrue(driver.findElement(By.cssSelector(".cart-button")).getText().contains("1"));
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
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding()).statusCode() == 201;
        } catch (Exception exception) {
            return false;
        }
    }
}
