package it.matteomoretto.matteomorettoftpconnection;

/**
 * Created by Matteo Moretto on 14/09/2017.
 */
public class FileElement {

    public FileElement (String fileDescr, long fileSize) {
        this.setFileName(fileDescr);
        this.setFileSize(fileSize);
    }

    private String FileName;
    private long FileSize;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public long getFileSize() {
        return FileSize;
    }

    public void setFileSize(long fileSize) {
        FileSize = fileSize;
    }
}
