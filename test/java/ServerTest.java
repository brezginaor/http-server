import org.example.httpserver.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {

    private Server server;
    private Thread serverThread;

    @BeforeEach
    public void setUp() throws InterruptedException {
        server = new Server(8083);

        // Регистрация маршрутов
        server.addRoute("GET", "/test", (req, res) -> {
            try {
                res.sendText(200, "Test successful");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread = new Thread(() -> {
            try {
                server.startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();

        // Ожидание запуска сервера
        TimeUnit.SECONDS.sleep(1);  // Ждем, пока сервер запустится
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        server.stopServer();  // Корректно останавливаем сервер
        // Принудительно завершаем поток, если он не завершился через определенное время
        serverThread.join(2000);  // Ждем 2 секунды
        if (serverThread.isAlive()) {
            serverThread.interrupt();  // Принудительная остановка потока, если он не завершился
        }
    }

    @Test
    public void testGetRoute() throws Exception {
        String response = sendRequest("GET", "http://localhost:8083/test");
        assertEquals("Test successful", response);
    }

    @Test
    public void testPostRoute() throws Exception {
        String response = sendRequest("POST", "http://localhost:8083/submit");
        assertEquals("Received POST request !!!", response);
    }

    private String sendRequest(String method, String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}