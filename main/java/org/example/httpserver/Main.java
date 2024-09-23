package org.example.httpserver;


import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server=new Server(8082);
        try {
            server.startServer();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}