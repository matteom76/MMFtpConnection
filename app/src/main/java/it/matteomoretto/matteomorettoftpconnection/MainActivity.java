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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button Connection = (Button) findViewById(R.id.Connect);
        Connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MakeFTPConnection().execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // MATTEO MORETTO
        //MODIFICA DA Casa 22.38

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MakeFTPConnection extends AsyncTask <Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... param) {
            FTPClient ConnReference;
            String host = "matteomoretto76.hopto.org";
            int port = 21;
            String username="admin";
            String password="3007Pollo";
            Boolean status=false;
            ConnReference = new FTPClient();
            try {
                // connecting to the host
                ConnReference.connect(host, port);
                // now check the reply code, if positive mean connection success
                if (FTPReply.isPositiveCompletion(ConnReference.getReplyCode())) {
                    // login using username & password
                    status=ConnReference.login(username, password);
                    Log.i("Result: ", ConnReference.getStatus());
                    //TextView StatusMsg = (TextView) findViewById(R.id.StatusValue);
                    //StatusMsg.setText((status)?"OK":"ERROR");
           /*
               * Set File Transfer Mode
               * To avoid corruption issue you must specified a correct
               * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
               * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
               * transferring text, image, and compressed files.
            */
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

            if (directories!=null) {
                ListView list = (ListView) findViewById(R.id.ListDirectory);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, directories);
                list.setAdapter(adapter);
            }
        }
    }


}



