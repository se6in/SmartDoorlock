package com.example.test2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;


public class registerActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "220.69.240.76/insert.php";
    private static String TAG = "test2";

    private EditText mEditTextId;
    private EditText mEditTextPassword;
    private EditText mEditTextName;
    private EditText mEditTextTel;
    private RadioGroup radioGroup;
    private TextView mTextViewResult;
    private RadioButton rg_man_btn;
    private RadioButton rg_women_btn;
    private  String sex="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mEditTextId = (EditText)findViewById(R.id.editText_main_id);
        mEditTextPassword = (EditText)findViewById(R.id.editText_main_password);
        mEditTextName = (EditText)findViewById(R.id.editText_main_name);
        mEditTextTel = (EditText)findViewById(R.id.editText_main_tel);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rg_man_btn = (RadioButton)findViewById(R.id.rg_man_btn);
        rg_women_btn = (RadioButton)findViewById(R.id.rg_women_btn);
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        Button cancel_btn = (Button)findViewById(R.id.cancel_btn);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"로그인화면으로 이동했습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(registerActivity.this, login_activity.class);
                startActivity(intent);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rg_man_btn){
                    Toast.makeText(registerActivity.this, "남성을 눌렀습니다.", Toast.LENGTH_SHORT).show();
                    sex ="male";
                }
                else if(i == R.id.rg_women_btn){
                    Toast.makeText(registerActivity.this, "여성을 눌렀습니다.", Toast.LENGTH_SHORT).show();
                    //String women = rg_women_btn.getText().toString();
                    sex = "female";

                }
            }

        });

        Button buttonInsert = (Button)findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = mEditTextId.getText().toString();
                String password = mEditTextPassword.getText().toString();
                String name = mEditTextName.getText().toString();
                String tel = mEditTextTel.getText().toString();


                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert.php", id,password,name,tel, sex);


                mEditTextId.setText("");
                mEditTextPassword.setText("");
                mEditTextName.setText("");
                mEditTextTel.setText("");



            }
        });

    }



    class InsertData extends AsyncTask<String, Void,  String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(registerActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);

            if(Integer.parseInt(result)==1){
                Toast.makeText(getApplicationContext(),"회원등록에 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(registerActivity.this, login_activity.class);
                startActivity(intent);
            }
            else if(Integer.parseInt(result)==2){
                Toast.makeText(getApplicationContext(),"이름을 입력해주세요.",Toast.LENGTH_SHORT).show();

            }
            else if(Integer.parseInt(result)==3){
                Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();

            }
            else if(Integer.parseInt(result)==4){
                Toast.makeText(getApplicationContext(),"패스워드를 입력해주세요.",Toast.LENGTH_SHORT).show();

            }
            else if(Integer.parseInt(result)==5){
                Toast.makeText(getApplicationContext(),"전화번호를 입력해주세요.",Toast.LENGTH_SHORT).show();

            }
            else if(Integer.parseInt(result)==6){
                Toast.makeText(getApplicationContext(),"성별을 입력해주세요.",Toast.LENGTH_SHORT).show();

            }
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);




        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[1];
            String password = (String)params[2];
            String name = (String)params[3];
            String tel = (String)params[4];
            String sex = (String)params[5];

            String serverURL = (String)params[0];
            String postParameters = "id=" + id + "&password=" + password +
                    "&name=" + name +"&tel=" + tel + "&sex=" +sex;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);

                }



                bufferedReader.close();



                return sb.toString();



            }
            catch (Exception e) {


                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


}

