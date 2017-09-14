package it.matteomoretto.matteomorettoftpconnection;

/**
 * Created by Matteo Moretto on 14/09/2017.
 */
public class FileElement {

    public FileElement (String fileDescr) {
        this.setFileName(fileDescr);
    }

    private String FileName;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
}
