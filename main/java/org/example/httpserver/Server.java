package org.example.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

    private final String host;
    private final int port;
    private final RouteHandler routeHandler;

    public Server(int port) {
        host = "localhost";
        this.port = port;
        routeHandler = new RouteHandler();
    }


    public void startServer() throws IOException {

        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(host, port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);


            initializeRoutes();
            while (true) {
                selector.select();




                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        register(key, selector);
                    } else if (key.isReadable()) {
                        handleRequest(key);
                    }
                }
            }
        }
    }

    private void initializeRoutes() {
        routeHandler.addRoute("GET", "/", (req, res) -> {
            try {
                res.sendText(200, "Welcome to the homepage!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        routeHandler.addRoute("POST", "/data", (req, res) -> {
            try {
                res.sendText(200, "Data received: " + req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        routeHandler.addRoute("PUT", "/data", (req, res) -> {
            try {
                res.sendText(200, "Data updated: " + req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        routeHandler.addRoute("PATCH", "/data", (req, res) -> {
            try {
                res.sendText(200, "Data partially updated: " + req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        routeHandler.addRoute("DELETE", "/data", (req, res) -> {
            try {
                res.sendText(200, "Data deleted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        routeHandler.setDefaultRoute((req, res) -> {
            try {
                res.sendText(404, "Not Found: No handler for " + req.getMethod() + " " + req.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
    private void register(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private void handleRequest(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int dataBytes = clientChannel.read(buffer);

        if (dataBytes == -1) {
            clientChannel.close();
            return;
        }

        buffer.flip();
        String requestDataStr = new String(buffer.array(), 0, dataBytes);
        ServerRequest request = ServerRequest.parse(requestDataStr);
        ServerResponse response = new ServerResponse(clientChannel);

        switch (request.getMethod()) {
            case "GET":
                response.sendText(200, "Received GET request !!!");
                break;
            case "POST":
                response.sendText(200, "Received POST request !!! Body is: " + request.getBody());
                break;
            case "PUT":
                response.sendText(200, "Received PUT request with body: " + request.getBody());
                break;
            case "PATCH":
                response.sendText(200, "Received PATCH request with body: " + request.getBody());
                break;
            case "DELETE":
                response.sendText(200, "Received DELETE request");
                break;
            default:
                response.sendText(404, "Not Found");
                break;
        }



    }





}