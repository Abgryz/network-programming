package lb1.part4;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class SelectServer {
    private final Selector selector;
    private final ServerSocketChannel server;

    @SneakyThrows
    public SelectServer(int port){
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("localhost", port));
    }

    @SneakyThrows
    public void start() {
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server opening on " + server.getLocalAddress());

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            for (SelectionKey key : selectedKeys) {
                if (key.isAcceptable()) {
                    accept();
                } else if (key.isReadable()) {
                    read(key);
                }
            }
            selectedKeys.clear();
        }
    }

    @SneakyThrows
    private void accept(){
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("Client connected: " + client.getRemoteAddress());
    }

    private void read(SelectionKey key){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel client = (SocketChannel) key.channel();
        try {
            if (client.read(buffer) > 0) {
                buffer.flip();
                String request = new String(buffer.array(), 0, buffer.limit());
                System.out.printf("[%s] Received message: %s", client.getRemoteAddress(), request);

                String response = "Server: " + request;
                buffer.clear();
                buffer.put(response.getBytes());
                buffer.flip();
                client.write(buffer);
                buffer.clear();
            }
        } catch (IOException e){
            System.out.println("Connection with client failed!");
        } catch (BufferOverflowException e){
            System.out.println("Message is too long: buffer is overflowed");
        }
    }

    public static void main(String[] args) {
        new SelectServer(7777).start();
    }
}