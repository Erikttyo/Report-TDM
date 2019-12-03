package com.example.reporttdm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.reporttdm.R;

public class HutPitActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btnHutang, btnPiutang;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hutang_piutang);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Hutang dan Piutang");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnHutang = (Button)findViewById(R.id.btn_tohutang);
        btnPiutang = (Button)findViewById(R.id.btn_topiutang);
        btnHutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HutPitActivity.this, ListHutPitActivity.class);
                intent.putExtra("jenis","1");
                startActivity(intent);
            }
        });
        btnPiutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HutPitActivity.this, ListHutPitActivity.class);
                intent.putExtra("jenis","0");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
