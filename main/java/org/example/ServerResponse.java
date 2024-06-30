package org.example;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ServerResponse {

    private final SocketChannel clientChannel;

    public ServerResponse(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void sendText(int code, String responseBody) throws IOException {
        String response = createResponse(code, "text/plain", responseBody);
        sendResponse(response);
        clientChannel.close();
    }

    private String createResponse(int code, String contentType, String responseBody) {
        return "HTTP/1.1 " +
                code +
                " " +
                getCodeStr(code) +
                "\r\n" +
                "Content-Type: " +
                contentType +
                "\r\n" +
                "Content-Length: " +
                responseBody.getBytes().length +
                "\r\n" +
                "\r\n" +
                responseBody;
    }

    private void sendResponse(String response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
        clientChannel.write(buffer);
        clientChannel.close();
    }

    private String getCodeStr(int code) {
        return switch (code) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}
