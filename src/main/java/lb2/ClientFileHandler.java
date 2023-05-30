package lb2;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientFileHandler {
    private static final int BUFFER_SIZE = 4096;
    private final File file;

    public ClientFileHandler(String filePath) throws IOException{
        // Проверяем, существует ли файл
        file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found!");
        }
    }

    public boolean sendFile(Socket client) throws IOException, NoSuchAlgorithmException, InterruptedException {
        // Получаем размер файла и вычисляем хеш-сумму
        long fileSize = file.length();
        byte[] digest = getHash(file);

        // Отправляем название файла, размер, хеш-сумму и данные файла на сервер
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        dos.writeUTF(file.getName());
        dos.writeLong(fileSize);
        dos.write(digest);

        FileInputStream fis;
        fis = new FileInputStream(file);
        sendToServer(dos, fileSize, fis);

        fis.close();
        dos.flush();
        dos.close();
        return true;
    }
    private static byte[] getHash(File file) throws NoSuchAlgorithmException, IOException {
        FileInputStream fis;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        fis = new FileInputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            md.update(buffer, 0, bytesRead);
        }
        fis.close();
        return md.digest();
    }
    private void sendToServer(DataOutputStream dos, long fileSize, FileInputStream fis) throws IOException, InterruptedException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        long totalBytesRead = 0;
        while ((bytesRead = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
            var progress = (int) ((double)totalBytesRead / fileSize * 100);
            System.out.print("\rTransferred: " + progress + "%");
        }
    }
}
