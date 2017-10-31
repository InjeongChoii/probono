package org.androidtown.modifiedui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView idText = (TextView) findViewById(R.id.idText);
        final CheckBox loginCkbx = (CheckBox)findViewById(R.id.loginCkbx);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        Button dbutton = (Button)findViewById(R.id.disabledButton);
        Button vbutton = (Button)findViewById(R.id.volunteerButton);

        String userID = null;
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try{
            userID = mgr.getLine1Number();
            userID = userID.replace("+82","0");
            if(userID != null) {
                idText.setText(userID);
            }else{
                idText.setText("failed to get phone number");
            }
        }catch (Exception e){}



        //임시 화면전환
        dbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapCallIntent = new Intent(LoginActivity.this,MapCallActivity.class);
                LoginActivity.this.startActivity(mapCallIntent);
            }
        });

        vbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent volunteerIntent = new Intent(LoginActivity.this,VolunteerActivity.class);
                LoginActivity.this.startActivity(volunteerIntent);
            }
        });
        //여기까지 임시 화면전환

        /*
        //디비 연동 로그인
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = idText.getText().toString();
                String dORv = "";
                //String carer= "";
                if(!loginCkbx.isChecked()){ //비장애인인 경우
                    dORv = "v";
                } else if(loginCkbx.isChecked()){
                    dORv = "d";
                }

                final String finalDORv = dORv;
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                String userID = jsonResponse.getString("userID");
                                //String dORv = jsonResponse.getString("dORv");
                               // String carer = jsonResponse.getString("carer");

                                if(finalDORv =="d"){
                                    Intent intent = new Intent(LoginActivity.this, MapCallActivity.class);
                                    intent.putExtra("userID", userID);
                                    LoginActivity.this.startActivity(intent);
                                }else{
                                    Intent intent = new Intent(LoginActivity.this, VolunteerActivity.class);
                                    intent.putExtra("userID", userID);
                                    LoginActivity.this.startActivity(intent);
                                }

                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("로그인에 실패하였습니다.")
                                        .setNegativeButton("다시 시도", null)
                                        .create()
                                        .show();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });*/


        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

    }
}