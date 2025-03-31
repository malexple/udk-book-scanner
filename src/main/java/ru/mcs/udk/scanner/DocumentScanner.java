package ru.mcs.udk.scanner;

import ru.mcs.udk.wrapper.DocumentInfo;
import java.io.File;

public interface DocumentScanner {

    DocumentInfo getUDK(File file);
}
