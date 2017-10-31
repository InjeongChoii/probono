package org.androidtown.modifiedui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        final EditText carerText = (EditText) findViewById(R.id.carerText);

        //전화번호 자동 가져오기
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

        //체크박스로 carer 활성화여부 결정
        checkBox.setChecked(false);
        carerText.setEnabled(false);    //보호자 연락처는 default로 비활성화 상태

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    carerText.setEnabled(true);     //장애인인 경우 : 보호자 연락처 활성화
                    carerText.requestFocus();
                }else{
                    carerText.setEnabled(false);
                }
            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                String userName = nameText.getText().toString();
                String dORv = "";
                String carer = "";

                if(! checkBox.isChecked()){ //비장애인인 경우
                    dORv = "v";
                } else if(checkBox.isChecked()){
                    dORv = "d";
                    carer = carerText.getText().toString();
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder =  new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("회원 가입에 성공했습니다.")
                                        .setPositiveButton("확인",null)
                                        .create()
                                        .show();
                                finish();
                            }else{
                                AlertDialog.Builder builder =  new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("회원 가입에 실패했습니다..")
                                        .setNegativeButton("다시 시도",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userName, dORv, carer, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });


    }

}
