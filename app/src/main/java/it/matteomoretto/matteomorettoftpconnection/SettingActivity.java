package it.matteomoretto.matteomorettoftpconnection;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

public class SettingActivity extends ActionBarActivity {

    private Intent ReturnMainPage;
    private FTPSetting Setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Setting = FTPSetting.getInstance();
        EditText host = getElement(R.id.Host);
        host.setText(Setting.getHost());
        EditText port = getElement(R.id.Port);
        port.setText(String.valueOf(Setting.getPort()));
        EditText user = getElement(R.id.User);
        user.setText(Setting.getUser());
        EditText password = getElement(R.id.Password);
        password.setText(Setting.getPassword());

        ReturnMainPage = new Intent(this,MainActivity.class);
        Button CancelBtn = getElement(R.id.BtnCancelSetting);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ReturnMainPage);
            }
        });

        final Button SaveBtn = getElement(R.id.BtnSaveSetting);
        SaveBtn.setEnabled(false);
        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ReturnMainPage);
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
}
