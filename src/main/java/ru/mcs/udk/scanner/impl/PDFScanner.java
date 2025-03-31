package ru.mcs.udk.scanner.impl;

import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import ru.mcs.udk.wrapper.DocumentFormat;
import ru.mcs.udk.wrapper.DocumentInfo;
import ru.mcs.udk.scanner.DocumentScanner;
import ru.mcs.udk.utils.DocumentUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static ru.mcs.udk.utils.DocumentUtils.findUDK;
import static ru.mcs.udk.utils.DocumentUtils.getSizeFile;
import static ru.mcs.udk.utils.DocumentUtils.getText;
import static ru.mcs.udk.utils.DocumentUtils.isCyrillic;

public class PDFScanner implements DocumentScanner {

    @Override
    public DocumentInfo getUDK(File file) {
        // Засекаем время начала поиска
        long startTime = System.currentTimeMillis();
        DocumentInfo documentInfo = new DocumentInfo(DocumentFormat.PDF);
        documentInfo.setFileSize(getSizeFile(file));

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            // Устанавливаем диапазон страниц для анализа (первые шесть страниц)
            stripper.setStartPage(1);
            stripper.setEndPage(Math.min(6, document.getNumberOfPages()));
            String text = stripper.getText(document);

            documentInfo.setUdk(findUDK(text));
            documentInfo.setLanguage(isCyrillic(file.getName()) ? "ru" : "en");

            // если ничего не нашли получаем изображения и отправляем в tesseract
            if ((documentInfo.getUdk() == null || documentInfo.getUdk().isBlank()) && documentInfo.getLanguage().equals("ru")) {
                documentInfo.setUdk(getUdkByImage(document));
            }
        } catch (IOException | TesseractException e) {
            documentInfo.setError(e.getMessage());
        }
        // Засекаем конечное время поиска
        long endTime = System.currentTimeMillis();
        documentInfo.setDuration(endTime - startTime);
        return documentInfo;
    }

    private static String getUdkByImage(PDDocument document) throws IOException, TesseractException {
        PDFRenderer renderer = new PDFRenderer(document);

        for (int pageIndex = 0; pageIndex < 6; pageIndex++) {
            BufferedImage image = renderer.renderImageWithDPI(pageIndex, 100);
            String text = getText(image);
            String udk = findUDK(text);
            if (!udk.isEmpty()) {
                return udk;
            }
        }

        return "";
    }
}
