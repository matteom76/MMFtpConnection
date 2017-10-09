package it.matteomoretto.matteomorettoftpconnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private FTPSetting Setting;

    private static final String SettingPreferences = "SettingPreferences" ;
    private static final String HostKey = "hostKey";
    private static final String UserKey= "userKey";
    private static final String PasswordKey= "passwordKey";
    private static final String PortKey= "portKey";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        ConnStatus = false;
        Setting = FTPSetting.getInstance();

        if (Setting.isFirstsetting()) {
           GetInfoPreferences(Setting);
        }

        PathList = new ArrayList<String>();
        DirNameList = new ArrayList<String>();
        TextView txtStatus = (TextView) findViewById(R.id.StatusValue);
        SetTextStatus(txtStatus, ConnStatus);
        Button btnPrev = getElement(R.id.BtnPrev);
        Button btnSelTutto = getElement(R.id.BtnSelAll);
        Button btnSelNone = getElement(R.id.BtnSelNone);
        Button btnSelDownload = getElement(R.id.BtnSelDownload);
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

    private  <T> T getElement(int element) {
        T findelement = (T) MainActivity.this.findViewById(element);
        return findelement;
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
            case R.id.action_setting:
                Intent PageSetting = new Intent(this,SettingActivity.class);
                startActivity(PageSetting);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void GetInfoPreferences(FTPSetting setting) {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(SettingPreferences, Context.MODE_PRIVATE);
        setting.setHost(sharedPreferences.getString(HostKey,"null"));
        setting.setPort(sharedPreferences.getInt(PortKey,0));
        setting.setPassword(sharedPreferences.getString(PasswordKey,"null"));
        setting.setUser(sharedPreferences.getString(UserKey,"null"));
        setting.setFirstsetting(false);

    }

    private class MakeFTPConnection extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... param) {
            String host = Setting.getHost();
            //String host = "192.168.1.1";
            int port = Setting.getPort();
            String username = Setting.getUser();
            String password = Setting.getPassword();

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

            TextView StatusMsg = getElement(R.id.StatusValue);
            SetTextStatus(StatusMsg, ConnStatus);

            if (ConnStatus) {
                if (ListOfDir != null) {
                    ListView list = getElement(R.id.ListDirectory);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                            R.layout.listviewdirfile, ListOfDir);
                    list.setAdapter(adapter);
                    TextView txtActDir = getElement(R.id.ViewActualDir);
                    txtActDir.setText(ActualDir);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            PathList.add(ActualPath);
                            DirNameList.add(ActualDir);
                            Button btnPrev = getElement(R.id.BtnPrev);
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
                    ListView list = getElement(R.id.ListFiles);
                    final FileAdapter adapter = new FileAdapter(MainActivity.this, FileList);
                    adapterIstance = adapter;
                    list.setAdapter(adapter);
                    FileListSelected = new HashMap<Integer, FileElement>();
                    Button btnSelTutto = getElement(R.id.BtnSelAll);
                    Button btnSelNone = getElement(R.id.BtnSelNone);
                    Button btnSelDownload = getElement(R.id.BtnSelDownload);
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
            else {
                MakeToastMessage(MainActivity.this,getResources().getString(R.string.error_connection));
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
            ListView listDir = getElement(R.id.ListDirectory);
            listDir.setAdapter(null);
            ListView listFile = getElement(R.id.ListFiles);
            listFile.setAdapter(null);
            TextView txtActDir = getElement(R.id.ViewActualDir);
            txtActDir.setText("");
            Button btnSelTutto = getElement(R.id.BtnSelAll);
            Button btnSelNone = getElement(R.id.BtnSelNone);
            Button btnSelDownload = getElement(R.id.BtnSelDownload);
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

        String errorMessage;

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

            File DownloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File PathToCheck = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" + ActualDir);

            if (PathToCheck.exists()) {
                DownloadPath = PathToCheck;
            }
            else {
                if (FileListSelected.size() > 1) {
                    if (PathToCheck.mkdir()) {
                        DownloadPath = PathToCheck;
                    }
                    else {
                        errorMessage = getResources().getString(R.string.error_foldercreation);
                        return false;
                    }
                }
            }

            for (FileElement fileToDownload:FileListSelected.values()) {

                String fileName = fileToDownload.getFileName();
                String filePath = ActualPath + fileName;
                if (!(DownloadFileProgress(DownloadPath,filePath,fileName,fileToDownload.getFileSize()))) {
                    errorMessage = getResources().getString(R.string.error_downloadfile);
                    return false;
                }
                try {
                    ConnReference.completePendingCommand();
                } catch (IOException e) {
                    errorMessage = getResources().getString(R.string.error_duringdownload);
                    e.printStackTrace();
                }
            }

            return true;
        }


        private Boolean DownloadFileProgress(File Destination, String path,String fileN,long size) {
            int count;
            InputStream input = null;
            FileOutputStream output = null;

            try {
                input = new BufferedInputStream(ConnReference.retrieveFileStream(path));
                output = new FileOutputStream(Destination + "/" + fileN);


                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        output.close();
                        input.close();
                        return false;
                    }
                    total += count;
                    publishProgress(fileN, "" + (int) ((total * 100) / size));
                    output.write(data, 0, count);
                }

                output.flush();


            } catch (Exception e) {
                Log.i("ErrDownload:", e.getMessage());
                return false;

            } finally {
                FileUtils.close(input);
                FileUtils.close(output);
            }
            return true;
        }

        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[1]));
        }

        @Override
        protected void onCancelled (Boolean result) {
            mProgressDialog = null;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (!(status)) {
                MakeToastMessage(MainActivity.this,errorMessage);
            }

            mProgressDialog.dismiss();
            mProgressDialog = null;
        }


    }

    private void MakeToastMessage(Context context,String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = toast.getView();
        view.setBackgroundColor(getResources().getColor(R.color.red));
        toast.show();
    }

}







