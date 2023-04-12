package lb1.part2;

import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class TextServerThread {
    private final ServerSocket server;

    @SneakyThrows
    public TextServerThread(int port){
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

    private static void clientInit(Socket clientSocket) {
        {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.printf("[%s] %s Received message: %s\n", LocalDateTime.now(), clientSocket, inputLine);
                    out.println("Server: " + inputLine);
                }
            } catch (IOException e) {
                System.out.println("Connection failed: " + clientSocket);
            }
        }
    }
    public static void main(String[] args) {
        new TextServerThread(7777).start();
    }
}