package it.matteomoretto.matteomorettoftpconnection;

import android.content.Context;

/**
 * Created by Matteo Moretto on 27/09/2017.
 */

public class FTPSetting {
    private static final FTPSetting ourInstance = new FTPSetting();

    private String host;
    private String user;
    private String password;
    private int port;
    private boolean firstsetting;


    public static FTPSetting getInstance() {
        return ourInstance;
    }

    private FTPSetting() {
        /*
        setHost("matteomoretto76.hopto.org");
        setUser("admin");
        setPassword("3007Pollo");
        setPort(21);
        */
        setHost("nullvalue");
        setUser("nullvalue");
        setPassword("nullvalue");
        setPort(0);
        setFirstsetting(true);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public boolean isFirstsetting() {
        return firstsetting;
    }

    public void setFirstsetting(boolean firstsetting) {
        this.firstsetting = firstsetting;
    }
}
