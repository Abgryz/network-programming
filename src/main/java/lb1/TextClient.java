package lb1;

import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TextClient {
    Socket socket;

    @SneakyThrows
    public TextClient(String host, int port) {
        socket = new Socket(host, port);
        System.out.println("Connect to server...");
    }

    @SneakyThrows
    public void connect() {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Scanner scan = new Scanner(System.in);
        String userInput;

        while (!(userInput = scan.nextLine()).equals("/exit")) {
            out.println(userInput);
            System.out.println(in.readLine());
        }
        socket.close();
    }
}