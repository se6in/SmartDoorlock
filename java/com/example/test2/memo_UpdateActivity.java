package com.example.test2;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class memo_UpdateActivity extends AppCompatActivity {
    int _id;
    MemoDB helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_update);

        getSupportActionBar().setTitle("메모 읽기");
        //뒤로가기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        _id = intent.getIntExtra("_id", 0);

        helper = new MemoDB(this);
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from memo where _id=" + _id, null);
        if (cursor.moveToNext()) {
            TextView txtwdate = findViewById(R.id.txtwdate);
            txtwdate.setText("작성일:" + cursor.getString(2));
            TextView edtcontent = findViewById(R.id.edtcontent);
            edtcontent.setText("" + cursor.getString(1));
        }

        ImageView btnsave = findViewById(R.id.btnsave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder box = new AlertDialog.Builder(memo_UpdateActivity.this);
                box.setMessage("수정하시겠습니까?");
                box.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edtcontent = findViewById(R.id.edtcontent);
                        String strcontent = edtcontent.getText().toString();
                        String sql = "update memo set content='" + strcontent + "' ";
                        sql += " where _id=" + _id;
                        db.execSQL(sql);
                        finish();
                    }
                });
                box.setNegativeButton("닫기", null);
                box.show();
            }
        });
        //Toast.makeText(UpdateActivity.this,"_id="+_id,Toast.LENGTH_SHORT).show();
    }

    //뒤로가기 옵션 버튼에 이벤트주기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}