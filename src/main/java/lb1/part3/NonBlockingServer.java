package lb1.part3;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NonBlockingServer {
    private final ServerSocketChannel server;

    @SneakyThrows
    public NonBlockingServer(int port) {
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("localhost", port));
        server.configureBlocking(false);
    }

    @SneakyThrows
    public void start(){
        System.out.println("Server opened in " + server.getLocalAddress());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        while (true) {
            SocketChannel socketChannel = server.accept();
            if (socketChannel != null) {
                System.out.println("Client connected: " + socketChannel);
                executor.submit(() -> clientIO(socketChannel));
            }
        }
    }

    private void clientIO(SocketChannel socketChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            while (socketChannel.read(buffer) > 0) {
                buffer.flip();
                String request = new String(buffer.array(), 0, buffer.limit());
                buffer.clear();
                System.out.printf("[%s] %s Client: %s\n", LocalDateTime.now(), socketChannel.getRemoteAddress(), request);

                String response = "Server: " + request;
                buffer.put(response.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }
        }
        catch (IOException e){
            System.out.println("Connection failed:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new NonBlockingServer(7777).start();
    }
}