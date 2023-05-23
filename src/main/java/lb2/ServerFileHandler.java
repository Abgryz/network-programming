package lb2;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerFileHandler {
    private static final int BUFFER_SIZE = 4096;
    public static File acceptFile(String path, Socket client){
        try {
            DataInputStream dis = new DataInputStream(client.getInputStream());
            // Читаем название файла, размер и хеш-сумму
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            byte[] digest = new byte[32];
            dis.readFully(digest, 0, 32);
            FileOutputStream fos = new FileOutputStream(path + fileName);

            var md = createFileAndGetHash(fileSize, dis, fos);

            // Проверяем, совпадают ли хеш-суммы
            byte[] receivedDigest = md.digest();
            if (!MessageDigest.isEqual(digest, receivedDigest)) {
                throw new RuntimeException("Invalid hash!");
            }

            System.out.println("File " + fileName + " was successfully saved on the server");
            fos.close();
            return new File(path + fileName);
        } catch (FileNotFoundException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(client + " disconnected!");
        }
        return null;
    }
     private static MessageDigest createFileAndGetHash(Long fileSize, DataInputStream dis, FileOutputStream fos) throws NoSuchAlgorithmException, IOException {
         // Создаем новый файл в директории сервера с полученным названием
         byte[] buffer = new byte[BUFFER_SIZE];
         int bytesRead;
         long totalBytesRead = 0;
         MessageDigest md = MessageDigest.getInstance("SHA-256");

         // Читаем данные файла и вычисляем хеш-сумму
         while (totalBytesRead < fileSize) {
             bytesRead = dis.read(buffer);
             fos.write(buffer, 0, bytesRead);
             md.update(buffer, 0, bytesRead);
             totalBytesRead += bytesRead;
         }
         return md;
     }
}
