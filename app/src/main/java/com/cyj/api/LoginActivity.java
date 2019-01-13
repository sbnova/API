package com.cyj.api;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cyj.api.datas.User;
import com.cyj.api.utils.ConnectServer;
import com.cyj.api.utils.ContextUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {

    private android.widget.EditText emailEdt;
    private android.widget.EditText passwordEdt;
    private android.widget.Button loginBtn;
    private android.widget.TextView findPwTxt;
    private android.widget.Button signupBtn;
    private android.widget.CheckBox autoLoginCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();
        setupEvents();
        setValues();
    }

    @Override
    public void setupEvents() {

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                서버에 아이디/비번을 던져서 실제 회원인지 체크.

                ConnectServer.postRequestSignIn(mContext, emailEdt.getText().toString(),
                        passwordEdt.getText().toString(), new ConnectServer.JsonResponseHandler() {
                            @Override
                            public void onResponse(final JSONObject json) {
//                                서버에서 응답이 무사히 돌아오면 할일
                                Log.d("로그인응답", json.toString());

                                try {
                                    final int code = json.getInt("code");
                                    final String message = json.getString("message");

                                    if (code == 200) {
                                        JSONObject data = json.getJSONObject("data");
                                        JSONObject userJson = data.getJSONObject("user");

                                        User user = User.getUserFromJson(userJson);

                                        Log.d("로그인응답", "로그인한사람이름 : " + user.getName());

                                        String token = data.getString("token");

                                        if(autoLoginCheckBox.isChecked()){
//                                            자동로그인을 하고싶다 => SharedPreferences을 이용해서 토큰을 (반영구) 저장
                                            ContextUtil.setToken(mContext, token);
                                        }
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

            }
        });

    }

    @Override
    public void setValues() {

    }

    @Override
    public void bindViews() {
        this.signupBtn = (Button) findViewById(R.id.signupBtn);
        this.autoLoginCheckBox = (CheckBox) findViewById(R.id.autoLoginCheckBox);
        this.findPwTxt = (TextView) findViewById(R.id.findPwTxt);
        this.loginBtn = (Button) findViewById(R.id.loginBtn);
        this.passwordEdt = (EditText) findViewById(R.id.passwordEdt);
        this.emailEdt = (EditText) findViewById(R.id.emailEdt);

    }
}
