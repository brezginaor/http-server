package org.example.httpserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RouteHandlerTest {

    private RouteHandler routeHandler;
    private ServerRequest mockRequest;
    private ServerResponse mockResponse;

    @BeforeEach
    public void setUp() {
        routeHandler = new RouteHandler();
        mockRequest = mock(ServerRequest.class);
        mockResponse = mock(ServerResponse.class);
    }

    @Test
    public void testGetRequestHandler() throws IOException {
        // Добавляем обработчик для GET-запроса
        routeHandler.addRoute("GET", "/", (req, res) -> {
            try {
                res.sendText(200, "Welcome to the homepage!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Мокаем GET запрос
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPath()).thenReturn("/");

        BiConsumer<ServerRequest, ServerResponse> handler = routeHandler.getHandler("GET", "/");
        assertNotNull(handler);

        // Выполнение обработчика
        handler.accept(mockRequest, mockResponse);

        // Проверка, что sendText был вызван с правильными параметрами
        verify(mockResponse).sendText(200, "Welcome to the homepage!");
    }

    @Test
    public void testPostRequestHandler() throws IOException {
        // Добавляем обработчик для POST-запроса
        routeHandler.addRoute("POST", "/data", (req, res) -> {
            try {
                res.sendText(200, "Data received: " + req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Мокаем POST запрос
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPath()).thenReturn("/data");
        when(mockRequest.getBody()).thenReturn("Sample data");

        BiConsumer<ServerRequest, ServerResponse> handler = routeHandler.getHandler("POST", "/data");
        assertNotNull(handler);

        // Выполнение обработчика
        handler.accept(mockRequest, mockResponse);

        // Проверка, что sendText был вызван с правильными параметрами
        verify(mockResponse).sendText(200, "Data received: Sample data");
    }

    @Test
    public void testPutRequestHandler() throws IOException {
        // Добавляем обработчик для PUT-запроса
        routeHandler.addRoute("PUT", "/data", (req, res) -> {
            try {
                res.sendText(200, "Data updated: " + req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Мокаем PUT запрос
        when(mockRequest.getMethod()).thenReturn("PUT");
        when(mockRequest.getPath()).thenReturn("/data");
        when(mockRequest.getBody()).thenReturn("Updated data");

        BiConsumer<ServerRequest, ServerResponse> handler = routeHandler.getHandler("PUT", "/data");
        assertNotNull(handler);

        // Выполнение обработчика
        handler.accept(mockRequest, mockResponse);

        // Проверка, что sendText был вызван с правильными параметрами
        verify(mockResponse).sendText(200, "Data updated: Updated data");
    }

    @Test
    public void testDeleteRequestHandler() throws IOException {
        // Добавляем обработчик для DELETE-запроса
        routeHandler.addRoute("DELETE", "/data", (req, res) -> {
            try {
                res.sendText(200, "Data deleted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Мокаем DELETE запрос
        when(mockRequest.getMethod()).thenReturn("DELETE");
        when(mockRequest.getPath()).thenReturn("/data");

        BiConsumer<ServerRequest, ServerResponse> handler = routeHandler.getHandler("DELETE", "/data");
        assertNotNull(handler);

        // Выполнение обработчика
        handler.accept(mockRequest, mockResponse);

        // Проверка, что sendText был вызван с правильными параметрами
        verify(mockResponse).sendText(200, "Data deleted");
    }

    @Test
    public void testNotFoundRoute() throws IOException {
        // Тест для необработанного маршрута

        // Мокаем GET запрос на неизвестный путь
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPath()).thenReturn("/unknown");

        BiConsumer<ServerRequest, ServerResponse> handler = routeHandler.getHandler("GET", "/unknown");
        assertNull(handler); // Должен вернуть null для неизвестного маршрута
    }
}
