package it.matteomoretto.matteomorettoftpconnection;

/**
 * Created by Matteo Moretto on 27/09/2017.
 */

public class FTPSetting {
    private static final FTPSetting ourInstance = new FTPSetting();

    public static FTPSetting getInstance() {
        return ourInstance;
    }

    private FTPSetting() {
    }
}
