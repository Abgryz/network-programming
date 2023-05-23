package lb2.part2;
import lb2.ClientFileHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
public class ZipFileClient {
    private final int port;
    private final static String PATH = "src\\main\\resources\\input\\ZVIT";

    @SneakyThrows
    public void start() {
        // Путь к директории ZVIT
        Path zvitDirectoryPath = Paths.get(PATH);

        // Получаем список директорий в директории ZVIT
        List<Path> reportDirectories = getReportDirectories(zvitDirectoryPath);

        if (!reportDirectories.isEmpty()) {
            // Сортируем директории по дате в обратном порядке
            reportDirectories.sort(Collections.reverseOrder());

            // Получаем последнюю по дате звітну папку
            Path latestReportDirectory = reportDirectories.get(0);
            System.out.println("Latest report directory: " + latestReportDirectory);
            String archiveFileName = createArchive(latestReportDirectory);

            Socket client = new Socket("localhost", port);
//            for (int i = 0; i < 3; i++){
                try {
                    new ClientFileHandler(archiveFileName).sendFile(client);

//                    serverListener(client);
                    return;
                } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
                    e.printStackTrace();
                }

//            }
        } else {
            System.out.println("No report directories found in directory");
        }
    }

    private static List<Path> getReportDirectories(Path zvitDirectoryPath) {
        List<Path> reportDirectories = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(zvitDirectoryPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    reportDirectories.add(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reportDirectories;
    }

    private static String createArchive(Path reportDirectory) throws IOException {
        String reportDirectoryName = reportDirectory.getFileName().toString();
        String archiveFileName = PATH + "\\" + reportDirectoryName + "_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss")) + ".zip";

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(archiveFileName))) {
            Files.walk(reportDirectory)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(reportDirectoryName + File.separator + reportDirectory.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        System.out.println("Archive created: " + archiveFileName);
        return archiveFileName;
    }

//    private static void serverListener(Socket client) throws IOException {
//        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
//        String str;
//        while(!(str = in.readLine()).equals(FileTransferStatus.SUCCESSFUL.name())){
//            if(str.equals(FileTransferStatus.ERROR.name())){
//                out.println(FileTransferStatus.ERROR.name());
//                throw new RuntimeException();
//            }
//        }
//        out.println(FileTransferStatus.SUCCESSFUL.name());
//        System.out.println("Reports successfully sent to server!");
//    }
}

