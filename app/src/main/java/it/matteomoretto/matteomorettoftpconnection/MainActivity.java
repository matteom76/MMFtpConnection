package it.matteomoretto.matteomorettoftpconnection;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private FTPClient ConnReference;
    private Boolean ConnStatus;
    private MenuItem DisconnectItem;
    private MenuItem ConnectItem;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnStatus = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        DisconnectItem = menu.findItem(R.id.action_disconnect);
        ConnectItem = menu.findItem(R.id.action_connect);
        if (ConnStatus) {
            ConnectItem.setEnabled(false);
            DisconnectItem.setEnabled(true);
        }
        else {
            ConnectItem.setEnabled(true);
            DisconnectItem.setEnabled(false);
        }
        return true;
     }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // MATTEO MORETTO
        //MODIFICA DA Casa 23.46  - 07 09 2017

        int id = item.getItemId();
        switch (id) {
            case R.id.action_connect:
                ConnReference = new FTPClient();
                new MakeFTPConnection().execute();
                break;
            case R.id.action_disconnect:
                new DisconnectFTP().execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MakeFTPConnection extends AsyncTask <Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... param) {
            String host = "matteomoretto76.hopto.org";
            int port = 21;
            String username="admin";
            String password="3007Pollo";

            try {
                // connecting to the host
                ConnReference.connect(host, port);
                // now check the reply code, if positive mean connection success
                if (FTPReply.isPositiveCompletion(ConnReference.getReplyCode())) {
                    // login using username & password
                    ConnStatus=ConnReference.login(username, password);
                    Log.i("Result: ", ConnReference.getStatus());
                    //ConnReference.setFileType(FTP.BINARY_FILE_TYPE);
                    //ConnReference.enterLocalPassiveMode();
                    FTPFile[] directories = ConnReference.listDirectories("volume(sda1)");
                    String[] DirList = new String[directories.length];
                    for (int number = 0; number < directories.length; number++) {
                        DirList[number]=directories[number].getName();
                    }
                    return DirList;
                }
            } catch (Exception e) {
                Log.i("Error: could not connect to host ", host);
                Log.i("Error: ", e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(String[] directories) {

            TextView StatusMsg = (TextView) findViewById(R.id.StatusValue);
            StatusMsg.setText((ConnStatus)?"OK":"NOT CONN");

            if (directories!=null) {
                ListView list = (ListView) findViewById(R.id.ListDirectory);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, directories);
                list.setAdapter(adapter);
            }
        }
    }

    private class DisconnectFTP extends AsyncTask <Void,Void,Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                ConnReference.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            ConnStatus = result;
            TextView StatusMsg = (TextView) findViewById(R.id.StatusValue);
            StatusMsg.setText((ConnStatus)?"OK":"NOT CONN");
            ListView list = (ListView) findViewById(R.id.ListDirectory);
            list.setAdapter(null);
        }
    }

}



