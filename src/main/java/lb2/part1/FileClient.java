package lb2.part1;

import lb2.ClientFileHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.util.Scanner;

@RequiredArgsConstructor
public class FileClient {
    private static final String PATH = "src/main/resources/input/";
    private final Scanner scanner = new Scanner(System.in);
    private final int port;

    @SneakyThrows
    public void start() {
        while (true){
            try {
                System.out.print("Input file name: ");
                String filename = scanner.nextLine();
                Socket client = new Socket("localhost", port);

                ClientFileHandler clientFileHandler = new ClientFileHandler(PATH + filename);
                if (clientFileHandler.sendFile(client)){
                    System.out.println("\nFile " + filename + " was successfully sent to the server");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new FileClient(7777).start();
    }
}