package org.example;

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

    public Server(int port) {
        host = "localhost";
        this.port = port;
    }

    public void startServer() throws IOException {

        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(host, port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

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
            case "GET" -> response.sendText(200, "Received GET request !!!");
            case "POST" -> response.sendText(200, "Received POST request !!! Body is: " + request.getBody());
            case "PUT" -> response.sendText(200, "Received PUT request with body: " + request.getBody());
            case "PATCH" -> response.sendText(200, "Received PATCH request with body: " + request.getBody());
            case "DELETE" -> response.sendText(200, "Received DELETE request");
            default -> response.sendText(404, "Not Found");
        }



    }


}
