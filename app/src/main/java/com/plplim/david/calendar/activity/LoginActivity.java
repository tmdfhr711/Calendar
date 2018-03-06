package com.plplim.david.calendar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.plplim.david.calendar.R;
import com.plplim.david.calendar.adapter.TodoListAdapter;
import com.plplim.david.calendar.model.Todo;
import com.plplim.david.calendar.util.LoginRequest;
import com.plplim.david.calendar.util.RequestHandler;
import com.plplim.david.calendar.util.SharedPreferenceUtil;
import com.plplim.david.calendar.util.UserInformationApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private RequestHandler requestHandler = new RequestHandler();
    private SharedPreferenceUtil sharedPreferenceUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferenceUtil = new SharedPreferenceUtil(LoginActivity.this);
        TextView registerButton = (TextView) findViewById(R.id.loginactivity_registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        final EditText idText = (EditText) findViewById(R.id.loginactivity_idtext);
        final EditText passwordText = (EditText) findViewById(R.id.loginactivity_passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginactivity_loginbutton);

        Log.e("LoginStatus", sharedPreferenceUtil.getValue("loginStatus", ""));
        if (sharedPreferenceUtil.getValue("loginStatus", "").equals("login")) {
            //로그인 상태인 경우
            Log.e("userID", sharedPreferenceUtil.getValue("userID", "userID"));
            Log.e("userGroup", sharedPreferenceUtil.getValue("userGroup", "userGroup"));
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = idText.getText().toString();
                final String userPassword = passwordText.getText().toString();

                Response.Listener<String> responsListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("로그인에 성공했습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                UserInformationApi userInfo = new UserInformationApi(LoginActivity.this);
                                                userInfo.execute(userID);
                                                sharedPreferenceUtil.put("loginStatus", "login");
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("계정을 다시 확인하세요")
                                        .setNegativeButton("다시 시도", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responsListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
