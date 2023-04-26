package lb1.part1;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class TextServerQueue {
    private final ServerSocket server;

    @SneakyThrows
    public TextServerQueue(int port, int backlog){
        this.server = new ServerSocket(port, backlog);
        System.out.println("Server started");
        new Thread(this::serverConsole).start();
    }

    @SneakyThrows
    public void start() {
        while (true) {
            Socket clientSocket = server.accept();
            System.out.println("Client connected: " + clientSocket);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true))
            {
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

    @SneakyThrows
    private void serverConsole() {
        Scanner scan = new Scanner(System.in);
        while (true){
            String input = scan.nextLine();
            if (input.equals("/close")) {
                server.close();
            }
        }
    }
    public static void main(String[] args) {
        new TextServerQueue(7777, 1).start();
    }
}
