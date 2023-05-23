package lb2.part1;

import lb2.ClientFileHandler;
import lb2.ServerFileHandler;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@RequiredArgsConstructor
public class FileServer {
    private final static String PATH = "src/main/resources/output/";
    private final int port;

    public void start() {
        try(ServerSocket server = new ServerSocket(port)){
            System.out.println("Server opened in " + server.getLocalPort());

            while (true) {
                Socket client = server.accept();
                System.out.println(client + " connected");
//                ServerFileHandler.acceptFile(PATH, client);
                new Thread(() -> ServerFileHandler.acceptFile(PATH, client)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new FileServer(7777).start();
    }
}
