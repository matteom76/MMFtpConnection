package it.matteomoretto.matteomorettoftpconnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.Toast;

public class SettingActivity extends ActionBarActivity {

    private Intent ReturnMainPage;
    private FTPSetting Setting;

    private static final String SettingPreferences = "SettingPreferences" ;
    private static final String HostKey = "hostKey";
    private static final String UserKey= "userKey";
    private static final String PasswordKey= "passwordKey";
    private static final String PortKey= "portKey";
    private static final String DestinationKey= "destinationKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Setting = FTPSetting.getInstance();
        final EditText host = getElement(R.id.Host);
        host.setText(Setting.getHost());
        final EditText port = getElement(R.id.Port);
        port.setText(String.valueOf(Setting.getPort()));
        final EditText user = getElement(R.id.User);
        user.setText(Setting.getUser());
        final EditText password = getElement(R.id.Password);
        password.setText(Setting.getPassword());



        ReturnMainPage = new Intent(this,MainActivity.class);
        Button CancelBtn = getElement(R.id.BtnCancelSetting);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //startActivity(ReturnMainPage);
            }
        });



        final Button SaveBtn = getElement(R.id.BtnSaveSetting);
        SaveBtn.setEnabled(false);
        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(SettingPreferences, SettingActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (!(String.valueOf(port.getText()).equals(""))) {
                    String hosttxt = String.valueOf(host.getText());
                    int porttxt = Integer.parseInt(String.valueOf(port.getText()));
                    String usertxt = String.valueOf(user.getText());
                    String passwordtxt = String.valueOf(password.getText());
                    Setting.setHost(hosttxt);
                    Setting.setPort(porttxt);
                    Setting.setUser(usertxt);
                    Setting.setPassword(passwordtxt);

                    editor.putString(HostKey, hosttxt);
                    editor.putInt(PortKey, porttxt);
                    editor.putString(UserKey, usertxt);
                    editor.putString(PasswordKey, passwordtxt);
                    editor.commit();
                    finish();
                    //startActivity(ReturnMainPage);
                }
                else

                {
                    MakeToastMessage(SettingActivity.this,getResources().getString(R.string.error_not0));
                }


            }
        });

        TableLayout ViewPage = (TableLayout) findViewById(R.id.settingLayout);
        for (int i=0;i<ViewPage.getChildCount();i++)  {
            ViewGroup actrowview = (ViewGroup) ViewPage.getChildAt(i);
            for (int j=0;j<actrowview.getChildCount();j++) {
                View actview = actrowview.getChildAt(j);
                if (actview instanceof EditText) {
                    ((EditText) actview).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            SaveBtn.setEnabled(true);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
        }

    }

    private  <T> T getElement(int element) {
        T findelement = (T) SettingActivity.this.findViewById(element);
        return findelement;
    }


    private void MakeToastMessage(Context context,String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = toast.getView();
        view.setBackgroundColor(getResources().getColor(R.color.red));
        toast.show();
    }


}

