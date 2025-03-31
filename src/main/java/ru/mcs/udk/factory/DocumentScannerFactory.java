package ru.mcs.udk.factory;

import ru.mcs.udk.scanner.DocumentScanner;
import ru.mcs.udk.scanner.impl.DJVUScanner;
import ru.mcs.udk.scanner.impl.PDFScanner;

public class DocumentScannerFactory {
    public static DocumentScanner getScanner(String filePath) {
        if (filePath.toLowerCase().endsWith(".pdf")) {
            return new PDFScanner();
        } else if (filePath.toLowerCase().endsWith(".djvu")) {
            return new DJVUScanner();
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат файла: " + filePath);
        }
    }
}