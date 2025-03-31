package ru.mcs.udk.scanner.impl;

import net.sourceforge.tess4j.TesseractException;
import ru.mcs.udk.scanner.DocumentScanner;
import ru.mcs.udk.utils.DocumentUtils;
import ru.mcs.udk.wrapper.DocumentFormat;
import ru.mcs.udk.wrapper.DocumentInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static ru.mcs.udk.utils.DocumentUtils.findUDK;
import static ru.mcs.udk.utils.DocumentUtils.getText;
import static ru.mcs.udk.utils.DocumentUtils.isCyrillic;

public class DJVUScanner implements DocumentScanner {
    @Override
    public DocumentInfo getUDK(File djvuFile) {
        // Засекаем время начала поиска
        long startTime = System.currentTimeMillis();
        DocumentInfo documentInfo = new DocumentInfo(DocumentFormat.DJVU);
        documentInfo.setFileSize(DocumentUtils.getSizeFile(djvuFile));

        String outputFile = "page_%d_djvu.tiff";
        Process process;
        try {
            process = new ProcessBuilder("program/djvu/ddjvu.exe",
                    "-format=tiff",
                    "-quality=90",
                    "-page=1-6",
                    "-eachpage",
                    djvuFile.getAbsolutePath(),
                    "images/" + outputFile).start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                for (int index = 1; index <= 6; index++) {
                    BufferedImage image = ImageIO.read(new File(String.format("images/page_%s_djvu.tiff", index)));
                    String text = getText(image);
                    String udk = findUDK(getText(image));
                    if (!udk.isEmpty()) {
                        documentInfo.setLanguage(isCyrillic(text) ? "ru" : "en");
                        documentInfo.setUdk(udk);
                        break;
                    }
                }
            }
        } catch (InterruptedException | IOException | TesseractException e) {
            documentInfo.setError(e.getMessage());
        }
        // Засекаем конечное время поиска
        long endTime = System.currentTimeMillis();
        documentInfo.setDuration(endTime - startTime);
        return documentInfo;
    }
}
