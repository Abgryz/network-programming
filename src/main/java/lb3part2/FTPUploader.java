package lb3part2;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUploader {
    private static final String FILE_PATH_1 = "src\\main\\resources\\input\\anotherfile.txt";
    private static final String FILE_PATH_2 = "src\\main\\resources\\input\\Styx.mp4";
    private static final String server = "localhost";
    private static final int port = 21;
    private static final String username = "ftp";
    private static final String password = "ftp";


    public static void ftpUpload(FTPClient ftpClient, String filePath, String fileType) throws IOException {
        File file = new File(filePath);
        String fileName = file.getName();

        FileInputStream fis = new FileInputStream(file);
        if (fileType.equalsIgnoreCase("TXT")) {
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            ftpClient.storeFile(fileName, fis);
        } else {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.storeFile(fileName, fis);
        }

        fis.close();
    }

    public static void main(String[] args) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("FTP server refused connection.");
                return;
            }

            boolean success = ftpClient.login(username, password);
            if (!success) {
                System.out.println("Could not login to the FTP server.");
                return;
            }

            ftpUpload(ftpClient, FILE_PATH_1, "TXT");
            ftpUpload(ftpClient, FILE_PATH_2, "PDF");

            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
