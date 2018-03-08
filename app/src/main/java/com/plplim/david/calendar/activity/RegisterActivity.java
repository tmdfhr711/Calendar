package com.plplim.david.calendar.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.plplim.david.calendar.R;
import com.plplim.david.calendar.util.IdValidateRequest;
import com.plplim.david.calendar.util.RegisterRequest;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity{

    private AlertDialog dialog;

    private String userID;
    private String userPassword;
    private String userEmail;
    private String userGroup;
    private String userAuth;

    private boolean idValidate = false;
    private boolean groupValidate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText idText = (EditText) findViewById(R.id.registeractivity_idtext);
        final EditText passwordText = (EditText) findViewById(R.id.registeractivity_passwordText);
        final EditText emailText = (EditText) findViewById(R.id.registeractivity_emailText);
        final EditText groupText = (EditText) findViewById(R.id.registeractivity_grouptext);
        final Button registerButton = (Button) findViewById(R.id.registeractivity_registerButton);
        final Button idValidateButton = (Button) findViewById(R.id.registeractivity_idvalidateButton);
        final Button groupValidateButton = (Button) findViewById(R.id.registeractivity_groupvalidateButton);

        idValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                if (idValidate) {
                    return;
                }
                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다")
                            .setPositiveButton("확인", null)
                            .create();

                    dialog.show();
                    return;
                }

                Response.Listener<String> IdResonseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                idText.setEnabled(false);
                                idValidate = true;
                                idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                idValidateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                IdValidateRequest idValidateRequest = new IdValidateRequest(userID,IdResonseListener );
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(idValidateRequest);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userEmail = emailText.getText().toString();
                String userGroup = groupText.getText().toString();
                String userAuth = FirebaseInstanceId.getInstance().getToken();


                //ID 중복확인을 하지 않았을 경우
                if (!idValidate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("먼저 중복 체크를 해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                //모든 정보를 입력하지 않았을 경우
                if (userID.equals("") || userPassword.equals("") || userEmail.equals("") || userGroup.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈 칸 없이 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> registerResonseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 등록에 성공했습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 등록에 실패했습니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userEmail, userGroup, userAuth, registerResonseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);


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
