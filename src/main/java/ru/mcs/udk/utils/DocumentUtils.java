package ru.mcs.udk.utils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentUtils {
    private static volatile Tesseract tesseract;

    public static Tesseract getTesseract() {
        Tesseract localInstance = tesseract;
        if (localInstance == null) {
            synchronized (Tesseract.class) {
                localInstance = tesseract;
                if (localInstance == null) {
                    tesseract = localInstance = new Tesseract();
                    tesseract.setDatapath("program\\tesseract\\tessdata\\");
                    tesseract.setLanguage("rus");
                    tesseract.setVariable("user_defined_dpi", "300");
                }
            }
        }
        return localInstance;
    }

    public static String getText(BufferedImage image) throws TesseractException {
        Tesseract tesseract = getTesseract();
        return tesseract.doOCR(image);
    }

    public static String findUDK(String text) {
        // Регулярное выражение для поиска УДК
        Pattern pattern = Pattern.compile("[Уу]\\s*[Дд]\\s*[Кк]\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public static boolean isCyrillic(String fileName) {
        String regex = ".*[\\p{IsCyrillic}]{3,}.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }

    public static String getSizeFile(File file) {
        double sizeInMB = (double) file.length() / (1024 * 1024); // Размер в мегабайтах
        DecimalFormat df = new DecimalFormat("#.##"); // Форматирование до двух знаков после запятой

        return df.format(sizeInMB) + " Mb";
    }
}
