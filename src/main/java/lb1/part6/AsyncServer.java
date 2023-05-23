package lb1.part6;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncServer {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final int port;
    private ServerSocket server;

    public AsyncServer(int port) {
        this.port = port;
    }

    @SneakyThrows
    public void start() {
        server = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            CompletableFuture.runAsync(() -> {
                try {
                    Socket client = server.accept();
                    CompletableFuture.runAsync(() -> handleClient(client))
                        .thenRunAsync(() -> System.out.println("Client disconnected: " + client));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void handleClient(Socket client) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter writer = new PrintWriter(client.getOutputStream(), true)) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println("Received message: " + inputLine);
                String response = handleRequest(inputLine);
                writer.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String message) {
        // Обработка сообщения от клиента
        return "Echo: " + message;
    }

    public static void main(String[] args) throws IOException {
        new AsyncServer(7777).start();
    }
}
