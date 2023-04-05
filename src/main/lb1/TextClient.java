package main.lb1;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TextClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 7777);
            System.out.println("Connected to server");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scan = new Scanner(System.in);
            String userInput;

            while ((userInput = scan.nextLine()) != null) {
                out.println(userInput);
                System.out.println("Server response: " + in.readLine());
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}