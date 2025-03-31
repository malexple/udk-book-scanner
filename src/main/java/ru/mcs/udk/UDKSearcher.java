package ru.mcs.udk;

import java.io.*;
import java.nio.file.*;
import org.apache.commons.cli.*;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import ru.mcs.udk.scanner.DocumentScanner;
import ru.mcs.udk.factory.DocumentScannerFactory;
import ru.mcs.udk.wrapper.DocumentInfo;

public class UDKSearcher {

    public static void main(String[] args) throws FileNotFoundException {
        System.setErr(new PrintStream(new FileOutputStream("error.log"), true, StandardCharsets.UTF_8));

        // Создание опций командной строки
        Options options = new Options();
        options.addOption("folder", true, "Путь к папке для поиска файлов");
        options.addOption("format", true, "Формат файлов для поиска (например, pdf, djvu)");
        options.addOption("csv", true, "Имя csv файла для записи результатов");

        // Парсинг аргументов командной строки
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            // Получение значений параметров
            String directoryPath = cmd.getOptionValue("folder");
            String fileFormat = cmd.getOptionValue("format");
            String outputFile = cmd.getOptionValue("csv");

            // Проверка наличия обязательных параметров
            if (directoryPath == null || fileFormat == null || outputFile == null) {
                System.out.println("Необходимо указать все параметры: folder, format и csv.");
                printHelp(options);
                return;
            }

            // Поиск файлов
            List<File> foundFiles = findFilesByExtension(new File(directoryPath), List.of(fileFormat.toLowerCase().split(",")));

            // Запись результатов в файл
            if (foundFiles.isEmpty()) {
                System.out.println("Файлы с расширением ." + fileFormat + " не найдены.");
            } else {
                writeResultsToFile(foundFiles, outputFile);
                System.out.println("Результаты записаны в файл: " + outputFile);
            }

            // проходимся по всем найденным файлам и ищем УДК
            findUdk(outputFile, foundFiles.size());
        } catch (ParseException e) {
            System.out.println("Ошибка при разборе аргументов: " + e.getMessage());
            printHelp(options);
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private static List<File> findFilesByExtension(File directory, List<String> extensions) {
        List<File> foundFiles = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Рекурсивный поиск в подпапках
                        foundFiles.addAll(findFilesByExtension(file, extensions));
                    } else {
                        for (String extension : extensions) {
                            if (file.getName().toLowerCase().endsWith("." + extension)) {
                                foundFiles.add(file);
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("Указанный путь не является папкой.");
        }
        return foundFiles;
    }

    private static void writeResultsToFile(List<File> files, String outputFile) throws IOException {
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (File file : files) {
                writer.write(file.getAbsolutePath() + "\n");
            }
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("UDKSearcher", options);
    }

    private static void findUdk(String fileCsv, int totalFiles) {
        Path inputPath = Paths.get(fileCsv);
        Path outputPath = inputPath.resolveSibling(inputPath.getFileName() + ".out");
        DocumentInfo documentInfo;
        int processedFiles = 0; // Количество обработанных файлов

        try (BufferedReader reader = Files.newBufferedReader(inputPath);
             BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

            String line;
            while ((line = reader.readLine()) != null) {

                String filePath = line.trim();
                File file = new File(filePath);

                // Проверка, существует ли файл
                if (file.exists() && file.isFile()) {
                    processedFiles++;

                    // Получаем нужную реализацию сканера
                    DocumentScanner scanner = DocumentScannerFactory.getScanner(filePath);
                    // Ищем УДК номер
                    documentInfo = scanner.getUDK(file);

                    // Вычисляем процент завершения
                    double progress = (double) processedFiles / totalFiles * 100;
                    System.out.printf("Прогресс: %.2f%%\r", progress);
                    writer.write(String.format("%s;%s;%s;%s;%s;%s;%s", line, documentInfo.getFileSize(), documentInfo.getDuration(), documentInfo.getLanguage(), documentInfo.getUdk(), documentInfo.getError(), documentInfo.getDocumentFormat()));
                } else {
                    writer.write(String.format("%s;%s;%s;%s;%s;%s;%s", line, "File not found", "", "", "", "", "")); // Если файл не найден
                }
                writer.newLine();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        try {
            replaceOriginalFile(inputPath, outputPath);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void replaceOriginalFile(Path original, Path tempFile) throws Exception {
        Path backup = original.resolveSibling(original.getFileName() + ".bak");

        // Создаем бэкап оригинала
        Files.move(original, backup, StandardCopyOption.REPLACE_EXISTING);

        try {
            // Перемещаем временный файл на место оригинала
            Files.move(tempFile, original, StandardCopyOption.ATOMIC_MOVE);
            // Удаляем бэкап после успешной замены
            Files.deleteIfExists(backup);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            // Восстанавливаем из бэкапа при ошибке
            Files.move(backup, original, StandardCopyOption.REPLACE_EXISTING);
            throw e;
        }
    }
}
