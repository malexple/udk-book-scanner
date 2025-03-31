package ru.mcs.udk.wrapper;

public class DocumentInfo {
    String language;
    String udk;
    String error;
    String fileSize;
    DocumentFormat documentFormat;
    long duration;

    public DocumentInfo(DocumentFormat documentFormat) {
        this.language = "";
        this.udk = "";
        this.error = "";
        this.fileSize = "";
        this.duration = 0;
        this.documentFormat = documentFormat;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUdk() {
        return udk;
    }

    public void setUdk(String udk) {
        this.udk = udk;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public DocumentFormat getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
