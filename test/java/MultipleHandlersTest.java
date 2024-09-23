package org.example.httpserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class MultipleHandlersTest {

    private RouteHandler routeHandler;
    private ServerRequest mockRequest;
    private ServerResponse mockResponse;

    @BeforeEach
    public void setUp() {
        routeHandler = new RouteHandler();
        mockRequest = mock(ServerRequest.class);
        mockResponse = mock(ServerResponse.class);

        // Добавляем несколько обработчиков
        routeHandler.addRoute("GET", "/home", (req, res) -> {
            try {
                res.sendText(200, "GET: Welcome to the homepage!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        routeHandler.addRoute("POST", "/data", (req, res) -> {
            try {
                res.sendText(200, "POST: Data received: " + req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        routeHandler.addRoute("PUT", "/data", (req, res) -> {
            try {
                res.sendText(200, "PUT: Data updated: " + req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        routeHandler.addRoute("DELETE", "/data", (req, res) -> {
            try {
                res.sendText(200, "DELETE: Data deleted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testMultipleHandlers() throws IOException {
        // Тест GET-запроса на маршрут "/home"
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPath()).thenReturn("/home");

        BiConsumer<ServerRequest, ServerResponse> getHandler = routeHandler.getHandler("GET", "/home");
        assertNotNull(getHandler);
        getHandler.accept(mockRequest, mockResponse);
        verify(mockResponse).sendText(200, "GET: Welcome to the homepage!");

        // Тест POST-запроса на маршрут "/data"
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPath()).thenReturn("/data");
        when(mockRequest.getBody()).thenReturn("Sample data");

        BiConsumer<ServerRequest, ServerResponse> postHandler = routeHandler.getHandler("POST", "/data");
        assertNotNull(postHandler);
        postHandler.accept(mockRequest, mockResponse);
        verify(mockResponse).sendText(200, "POST: Data received: Sample data");

        // Тест PUT-запроса на маршрут "/data"
        when(mockRequest.getMethod()).thenReturn("PUT");
        when(mockRequest.getPath()).thenReturn("/data");
        when(mockRequest.getBody()).thenReturn("Updated data");

        BiConsumer<ServerRequest, ServerResponse> putHandler = routeHandler.getHandler("PUT", "/data");
        assertNotNull(putHandler);
        putHandler.accept(mockRequest, mockResponse);
        verify(mockResponse).sendText(200, "PUT: Data updated: Updated data");

        // Тест DELETE-запроса на маршрут "/data"
        when(mockRequest.getMethod()).thenReturn("DELETE");
        when(mockRequest.getPath()).thenReturn("/data");

        BiConsumer<ServerRequest, ServerResponse> deleteHandler = routeHandler.getHandler("DELETE", "/data");
        assertNotNull(deleteHandler);
        deleteHandler.accept(mockRequest, mockResponse);
        verify(mockResponse).sendText(200, "DELETE: Data deleted");
    }
}
