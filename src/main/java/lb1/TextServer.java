package lb1;

import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class TextServer {
    ServerSocket server;

    @SneakyThrows
    public TextServer(int port){
        this.server = new ServerSocket(port);
        System.out.println("Server started");
    }

    @SneakyThrows
    public void start() {
        while (true) {
            Socket clientSocket = server.accept();
            System.out.println("Client connected: " + clientSocket);
            new Thread(() -> clientInit(clientSocket)).start();
        }
    }

    @SneakyThrows
    private static void clientInit(Socket clientSocket) {
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.printf("[%s][%s] Received message: %s\n", LocalDateTime.now(), clientSocket.toString(), inputLine);
                out.println("Server: " + inputLine);
            }
        }
    }
    public static void main(String[] args) {
        new TextServer(7777).start();
    }
}