package com.example.test2;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class memo_MainActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "220.69.240.76/memo.php";
    private static String TAG = "test2";

    private ImageView btn_display;
    private TextView mTextViewResult1;
    private TextView txtcontent;

    MemoDB helper;
    SQLiteDatabase db;
    MyAdapter adapter;
    Cursor cursor;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_main);

        btn_display = (ImageView)findViewById(R.id.btn_display);
        txtcontent = (TextView)findViewById(R.id.txtcontent);
        mTextViewResult1 = (TextView)findViewById(R.id.textView_main_result1);

        mTextViewResult1.setMovementMethod(new ScrollingMovementMethod());


        //헤더문구변경
        getSupportActionBar().setTitle("모든 메모");
        getSupportActionBar().setIcon(R.drawable.ic_memo);

        //메모장 어플
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        helper=new MemoDB(this);
        db=helper.getReadableDatabase();

        cursor=db.rawQuery("select * from memo order by wdate desc",null);
        list=findViewById(R.id.list);
        adapter=new MyAdapter(this,cursor);
        list.setAdapter(adapter);
        /*adapter1=new MyAdapter(this.a);
        list.setAdapter(adpater1);
        */

        ImageView back_btn = findViewById(R.id.memo_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"도어락 화면입니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(memo_MainActivity.this, DoorlockActivity.class);
                startActivity(intent);
            }
        });

        //리스터를 걸겠다.
        ImageView btnwrite=findViewById(R.id.btnwrite);
        btnwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity이동
                Intent intent=new Intent(memo_MainActivity.this,memo_InsertActivity.class);
                startActivity(intent);
            }
        });

        /* 메모장 부분 보류
        btn_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter1=new MyAdapter(this,array);
                String memo =adapter1.getItem(0).toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/memo.php", memo);
            }
        });*/

    }

    class MyAdapter extends CursorAdapter{
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //아이템 만들기
            return getLayoutInflater().inflate(R.layout.item,parent,false);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            TextView txtcontent=view.findViewById(R.id.txtcontent);
            txtcontent.setText(cursor.getString(1));
            TextView txtwdate=view.findViewById(R.id.txtwdate);
            txtwdate.setText(cursor.getString(2));

            //ListView에 item을 생성했을때
            ImageView btndel=view.findViewById(R.id.btndel);

            //아이디값 가져오기
            final int _id=cursor.getInt(0);
            //삭제버튼 이벤트 설정
            btndel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //CONFRIM
                    AlertDialog.Builder box=new AlertDialog.Builder(memo_MainActivity.this);
                    box.setMessage(_id+"을(를) 삭제하시겠습니까?");
                    box.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sql="delete from memo where _id="+_id;
                            db.execSQL(sql);
                            //새로고침
                            onRestart();
                        }
                    });
                    box.setNegativeButton("닫기",null);
                    box.show();
                }
            });
            ImageView btnupdate=view.findViewById(R.id.btnupdate);
            btnupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(memo_MainActivity.this,memo_UpdateActivity.class);
                    intent.putExtra("_id", _id);
                    startActivity(intent);
                }
            });
        }
    }
    //옵션메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        //검색버튼
        MenuItem search=menu.findItem(R.id.search);
        SearchView view=(SearchView)search.getActionView();
        //query text가 변했을 때 발생
        //ActionView에 리스너를 걸어준다.
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //엔터를 칠때 검색
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String sql="select * from memo where content like  '%" + newText + "%'";
                cursor=db.rawQuery(sql,null);
                adapter.changeCursor(cursor);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    //옵션메뉴를 눌렀을때 발생하는 이벤트
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemcontent:
                cursor=db.rawQuery("select * from memo order by content",null);
                break;
            case R.id.itemwdate:
                cursor=db.rawQuery("select * from memo order by wdate desc",null);
                break;
        }
        //커서내용이 변경되었으므로 바뀐 커서값을 어덥터에서 바꿔줌
        adapter.changeCursor(cursor);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        cursor=db.rawQuery("select * from memo order by wdate desc",null);
        adapter.changeCursor(cursor);
        super.onRestart();
    }
    class InsertData extends AsyncTask<String, Void,  String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(memo_MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult1.setText(result);
            Log.d(TAG, "POST response  - " + result);


        }


        @Override
        protected String doInBackground(String... params) {

            String memo = (String)params[1];

            String serverURL = (String)params[0];
            String postParameters = "memo=" + memo ;


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

