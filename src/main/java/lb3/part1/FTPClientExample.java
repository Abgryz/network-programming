package lb3.part1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

@Slf4j
@RequiredArgsConstructor
public class FTPClientExample {
    private final String server;
    private final int port;
    private final String username;
    private final String password;
    private final static String REMOTE_DIRECTORY = "/ubuntu/dists/";
    private final static String LOCAL_DIRECTORY = "src/main/resources/output";
    private final FTPClient ftpClient = new FTPClient();

    public void start () {
        try {
            // Підключення до FTP серверу
            ftpClient.connect(server, port);
            ftpClient.login(username, password);

            // Перехід до каталогу /ubuntu/dists/
            ftpClient.changeWorkingDirectory(REMOTE_DIRECTORY);

            // Отримання списку назв файлів
            String[] fileNames = ftpClient.listNames();

            // Запис назв файлів до локального файлу
            BufferedWriter writer = new BufferedWriter(new FileWriter(LOCAL_DIRECTORY + "/ubuntu_dists.txt"));
            for (String fileName : fileNames) {
                writer.write(fileName);
                writer.newLine();
                if (fileName.endsWith("updates")){
                    String findingDir = REMOTE_DIRECTORY + fileName + "/main";
                    log.info("Finding manifest in " + findingDir + " ");
                    findAndDownloadManifest(findingDir, LOCAL_DIRECTORY, 1);
                }
            }
            writer.close();
            System.out.println("ubuntu_dists.txt saved");


            System.out.println("Operation completed successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection with FTP server failed.");
        }
    }
    private void findAndDownloadManifest(String remoteDirectory, String localDirectory, int iteration) throws IOException {
        // Перехід до вибраної директорії
        ftpClient.changeWorkingDirectory(remoteDirectory);

        // Отримання списку назв файлів у поточній директорії
        String[] fileNames = ftpClient.listNames();
        for (String fileName : fileNames) {
            // Шукати файл MANIFEST
            if (fileName.equalsIgnoreCase("MANIFEST")) {
                System.out.println("MANIFEST founded");
                // Завантаження файлу MANIFEST у відповідну директорію
                String localFilePath = localDirectory + remoteDirectory + "main/" + fileName;

                fileDownloader(localFilePath, fileName);
                System.out.println("MANIFEST downloaded " + localFilePath);
//                log.info("MANIFEST downloaded: " + localFilePath + " ");
                return;
            }
        }

        // Аналізувати вкладені директорії
        String[] subDirectories = ftpClient.listNames();
        for (String subDirectory : subDirectories) {
            if (ftpClient.changeWorkingDirectory(remoteDirectory + "/" + subDirectory)
                    && (subDirectory.startsWith("installer") || iteration != 1)
            ) {
//                System.out.println("finding in " + remoteDirectory + "/" + subDirectory);
                findAndDownloadManifest(remoteDirectory + "/" + subDirectory, localDirectory, iteration + 1);
                ftpClient.changeToParentDirectory();
            }
        }
    }

    private void fileDownloader(String localFilePath, String fileName) throws IOException {
        File localFile = new File(localFilePath);
        localFile.getParentFile().mkdirs(); // Створення відповідних директорій, якщо вони ще не існують

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
        ftpClient.retrieveFile(fileName, outputStream);
        outputStream.close();
    }

    public static void main(String[] args) {
        new FTPClientExample("ftp.ubuntu.com", 21,  "anonymous", "").start();
    }
}
