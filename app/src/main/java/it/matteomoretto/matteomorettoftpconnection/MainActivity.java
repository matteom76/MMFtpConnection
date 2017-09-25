package it.matteomoretto.matteomorettoftpconnection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private FTPClient ConnReference;
    private Boolean ConnStatus;
    private MenuItem DisconnectItem;
    private MenuItem ConnectItem;
    private String ActualPath;
    private String ActualDir;
    private String[] ListOfDir;
    private ArrayList<FileElement> FileList;
    private List<String> PathList;
    private List<String> DirNameList;
    private HashMap<Integer, FileElement> FileListSelected;
    private FileAdapter adapterIstance;
    private ProgressDialog mProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnStatus = false;
        PathList = new ArrayList<String>();
        DirNameList = new ArrayList<String>();
        TextView txtStatus = (TextView) findViewById(R.id.StatusValue);
        SetTextStatus(txtStatus, ConnStatus);
        Button btnPrev = (Button) findViewById(R.id.BtnPrev);
        Button btnSelTutto = (Button) findViewById(R.id.BtnSelAll);
        Button btnSelNone = (Button) findViewById(R.id.BtnSelNone);
        Button btnSelDownload = (Button) findViewById(R.id.BtnSelDownload);
        btnPrev.setEnabled(false);
        btnSelTutto.setEnabled(false);
        btnSelNone.setEnabled(false);
        btnSelDownload.setEnabled(false);


        btnPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActualDir = DirNameList.get(DirNameList.size() - 1);
                ActualPath = PathList.get(PathList.size() - 1);
                DirNameList.remove(DirNameList.size() - 1);
                adapterIstance = null;
                PathList.remove(PathList.size() - 1);
                if (PathList.isEmpty()) {
                    v.setEnabled(false);
                }

                new MakeFTPConnection().execute();

            }
        });

        btnSelTutto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterIstance != null) {
                    adapterIstance.SelectAllFile();
                }
            }
        });

        btnSelNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterIstance != null) {
                    adapterIstance.DeselectAllFile();
                }
            }
        });

        btnSelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownLoadFile().execute();
            }
        });

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
        } else {
            ConnectItem.setEnabled(true);
            DisconnectItem.setEnabled(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // MATTEO MORETTO
        //MODIFICA DA Casa 00.24  - 21 09 2017

        int id = item.getItemId();
        switch (id) {
            case R.id.action_connect:
                ConnReference = new FTPClient();
                adapterIstance = null;
                new MakeFTPConnection().execute();
                break;
            case R.id.action_disconnect:
                new DisconnectFTP().execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MakeFTPConnection extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... param) {
            String host = "matteomoretto76.hopto.org";
            //String host = "192.168.1.1";
            int port = 21;
            String username = "admin";
            String password = "3007Pollo";

            try {
                // connecting to the host

                if (!(ConnStatus)) {

                    ConnReference.connect(host, port);
                    // now check the reply code, if positive mean connection success
                    if (FTPReply.isPositiveCompletion(ConnReference.getReplyCode())) {
                        // login using username & password
                        ConnStatus = ConnReference.login(username, password);
                        ConnReference.setFileType(FTP.BINARY_FILE_TYPE);
                        ConnReference.enterLocalPassiveMode();
                        ActualPath = "volume(sda1)/";
                        ActualDir = "volume(sda1)";
                        PathList.clear();
                        DirNameList.clear();
                        Log.i("Result: ", ConnReference.getStatus());
                    }
                }


                FTPFile[] directories = ConnReference.listDirectories(ActualPath);
                FTPFile[] filesOfdir = ConnReference.listFiles(ActualPath);

                String[] DirList = new String[directories.length];
                for (int number = 0; number < directories.length; number++) {
                    DirList[number] = directories[number].getName();
                }
                ListOfDir = DirList;

                FileList = new ArrayList<FileElement>();
                for (FTPFile ftpFile : filesOfdir) {
                    if ((!(ftpFile.isDirectory())) && (!(ftpFile.getName().equals("core")))) {
                        FileElement fileEl = new FileElement(ftpFile.getName(), ftpFile.getSize());
                        FileList.add(fileEl);
                    }
                }

                return true;

            } catch (Exception e) {
                Log.i("Error: ", host);
                Log.i("Error: ", e.getMessage());
                ConnStatus = false;
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean statusDir) {

            TextView StatusMsg = (TextView) findViewById(R.id.StatusValue);
            SetTextStatus(StatusMsg, ConnStatus);

            if (ConnStatus) {
                if (ListOfDir != null) {
                    ListView list = (ListView) findViewById(R.id.ListDirectory);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                            R.layout.listviewdirfile, ListOfDir);
                    list.setAdapter(adapter);
                    TextView txtActDir = (TextView) findViewById(R.id.ViewActualDir);
                    txtActDir.setText(ActualDir);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            PathList.add(ActualPath);
                            DirNameList.add(ActualDir);
                            Button btnPrev = (Button) findViewById(R.id.BtnPrev);
                            btnPrev.setEnabled(true);
                            TextView actView = (TextView) view;
                            ActualDir = actView.getText().toString();
                            ActualPath = ActualPath + ActualDir + "/";
                            new MakeFTPConnection().execute();
                        }

                        ;
                    });
                }

                if (FileList != null) {
                    ListView list = (ListView) findViewById(R.id.ListFiles);
                    final FileAdapter adapter = new FileAdapter(MainActivity.this, FileList);
                    adapterIstance = adapter;
                    list.setAdapter(adapter);
                    FileListSelected = new HashMap<Integer, FileElement>();
                    Button btnSelTutto = (Button) findViewById(R.id.BtnSelAll);
                    Button btnSelNone = (Button) findViewById(R.id.BtnSelNone);
                    Button btnSelDownload = (Button) findViewById(R.id.BtnSelDownload);
                    if (list.getCount() > 0) {
                        btnSelTutto.setEnabled(true);
                        btnSelNone.setEnabled(true);
                        btnSelDownload.setEnabled(true);
                    } else {
                        btnSelTutto.setEnabled(false);
                        btnSelNone.setEnabled(false);
                        btnSelDownload.setEnabled(false);
                    }
                    list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    list.setItemsCanFocus(false);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (adapter.ToggleSetItemCheck(position)) {
                                    FileElement fElement=FileList.get(position);
                                    FileListSelected.put(position,fElement);
                                    Log.i("FileAggiunto:",FileListSelected.get(position).getFileName());
                            }
                            else
                            {
                                FileListSelected.remove(position);
                            }
                        }
                    });
                }
            }
        }
    }

    private class DisconnectFTP extends AsyncTask<Void, Void, Boolean> {


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
            SetTextStatus(StatusMsg, ConnStatus);
            ListView listDir = (ListView) findViewById(R.id.ListDirectory);
            listDir.setAdapter(null);
            ListView listFile = (ListView) findViewById(R.id.ListFiles);
            listFile.setAdapter(null);
            TextView txtActDir = (TextView) findViewById(R.id.ViewActualDir);
            txtActDir.setText("");
            Button btnSelTutto = (Button) findViewById(R.id.BtnSelAll);
            Button btnSelNone = (Button) findViewById(R.id.BtnSelNone);
            Button btnSelDownload = (Button) findViewById(R.id.BtnSelDownload);
            btnSelTutto.setEnabled(false);
            btnSelNone.setEnabled(false);
            btnSelDownload.setEnabled(false);

        }
    }

    private void SetTextStatus(TextView v, Boolean s) {
        if (s) {
            v.setText("CONNECTED");
            v.setTextColor(getResources().getColor(R.color.green));
        } else {
            v.setText("DISCONNECTED");
            v.setTextColor(getResources().getColor(R.color.red));
        }
    }


    private class DownLoadFile extends AsyncTask<Void, String, Boolean> {

        InputStream inputl = null;
        OutputStream output = null;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Downloading file..");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    cancel(true);
                }
            });
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void...params) {

            int count;

            for (FileElement fileToDownload:FileListSelected.values()) {

                String filePath = ActualPath + fileToDownload.getFileName();
                InputStream fileinput = null;
                try {
                    fileinput = ConnReference.retrieveFileStream(filePath);
                } catch (IOException e) {
                    Log.i("ErrDownload:", e.getMessage());
                    return false;
                }
                try {

                    BufferedInputStream input = new BufferedInputStream(fileinput);
                    output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileToDownload.getFileName());

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        if (isCancelled()) {
                            return false;
                        }
                        total += count;
                        publishProgress(fileToDownload.getFileName(), "" + (int) ((total * 100) / fileToDownload.getFileSize()));
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();


                } catch (Exception e) {
                    /*
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException g) {
                            g.printStackTrace();
                        }
                    }
                    */
                    Log.i("ErrDownload:", e.getMessage());
                    return false;

                } finally {

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (inputl != null) {
                try {
                    inputl.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }

        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[1]));
        }

        @Override
        protected void onCancelled (Boolean result) {
            if (inputl!=null) {
                try {
                    inputl.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output!=null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mProgressDialog = null;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            Log.i("Download:", String.valueOf(status));
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }


    }
}







