package lb1.part6;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class AsyncServer{
    private static final int BUFFER_SIZE = 1024;
    private final int port;

    public void start() {
        try (AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open()) {
            InetSocketAddress hostAddress = new InetSocketAddress("localhost", port);
            serverChannel.bind(hostAddress);
            System.out.println("Server opened on " + port);

            serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                @SneakyThrows
                public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                    serverChannel.accept(null, this);
                    System.out.println("Client connected: " + clientChannel.getRemoteAddress());
                    handleClientRequest(clientChannel);
                }

                @Override
                public void failed(Throwable throwable, Void attachment) {
                    System.out.println("Error at client connection: " + throwable.getMessage());
                }
            });

            // Чтобы сервер не останавливался сразу после запуска
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private static void handleClientRequest(AsynchronousSocketChannel clientChannel) {
        while (true){
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            try {
                clientChannel.read(buffer).get();
                buffer.flip();

                String clientMessage = StandardCharsets.UTF_8.decode(buffer).toString();
                System.out.println(clientChannel.getRemoteAddress() + ": " + clientMessage);

                String response = "Server: " + clientMessage;
                ByteBuffer responseBuffer = StandardCharsets.UTF_8.encode(response);

                clientChannel.write(responseBuffer).get();
                buffer.clear();
            } catch (ExecutionException | InterruptedException | IOException e) {
                System.out.println("Connection with " + clientChannel.getRemoteAddress() + " failed");
                return;
            }
        }
    }

    public static void main(String[] args) {
        new AsyncServer(7777).start();
    }
}
