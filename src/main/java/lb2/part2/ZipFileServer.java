package lb2.part2;

import lb2.ServerFileHandler;
import lombok.SneakyThrows;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileServer {
    private static final String ARC_DIRECTORY = "src\\main\\resources\\output\\ARC\\";
    private static final String REPORTS_DIRECTORY = "src\\main\\resources\\output\\REPORTS\\";

    @SneakyThrows
    public void start(int port) {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        while (true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client);
                new Thread(() -> {
//                    for (int i = 0; i < 3; i++){
                        try{
                            System.out.println("Trying accept file...");
                            File archive = ServerFileHandler.acceptFile(ARC_DIRECTORY, client);
                            extractArchiveFile(archive);
//                            sendConfirmation(client, FileTransferStatus.SUCCESSFUL.name());
//                            acceptMessage(client);
                            return;
                        } catch (Exception e){
//                            sendConfirmation(client, FileTransferStatus.ERROR.name());
                            e.printStackTrace();
                        }
//                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @SneakyThrows
//    private void acceptMessage(Socket client){
//        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream())))
//        {
//            String inputLine = in.readLine();
//            System.out.printf("[%s] %s ", client, inputLine);
//        } catch (IOException e) {
//            System.out.println("Connection failed: " + client);
//        }
//    }

//    @SneakyThrows
//    private void sendConfirmation(Socket client, String message) {
//        client.getOutputStream().write(message.getBytes());
//        client.getOutputStream().flush();
//    }

    @SneakyThrows
    private void extractArchiveFile(File archiveFile) {
        String fileName = archiveFile.getName();
        String reportDirectoryName = fileName.substring(0, fileName.lastIndexOf('.'));
        File reportDirectory = new File(REPORTS_DIRECTORY, reportDirectoryName);
        reportDirectory.mkdirs();

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(archiveFile))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                Path entryPath = reportDirectory.toPath().resolve(entryName);

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    System.out.println(entryPath);
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zipInputStream, entryPath);
                }

                zipInputStream.closeEntry();
            }
        }
    }

    public static void main(String[] args) {
        new ZipFileServer().start(7777);
    }
}
