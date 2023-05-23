package lb1.part5;

import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ProcessServer {
    private final ServerSocket server;

    @SneakyThrows
    public ProcessServer(int port){
        this.server = new ServerSocket(port);
        System.out.println("Server started");
        new Thread(this::serverConsole).start();
    }

    @SneakyThrows
    public void start() {
        while (true) {
            Socket clientSocket = server.accept();
            System.out.println("Client connected: " + clientSocket);
            ProcessBuilder pb = new ProcessBuilder("java", "lb1.part5.ClientHandler", String.valueOf(clientSocket.getPort()));
            pb.inheritIO();
            pb.start();
        }
    }

    private void serverConsole() {
        Scanner scan = new Scanner(System.in);
        while (true){
            String input = scan.nextLine();
            if (input.equals("/close")) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new ProcessServer(7777).start();
    }
}

