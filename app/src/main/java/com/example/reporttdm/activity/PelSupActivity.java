package com.example.reporttdm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.reporttdm.R;

public class PelSupActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btnPelanggan, btnSuplier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelsup);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Pelanggan dan Suplier");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnPelanggan = (Button)findViewById(R.id.btn_topelanggan);
        btnSuplier = (Button)findViewById(R.id.btn_tosuplier);
        btnPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PelSupActivity.this, ListPelSupActivity.class);
                intent.putExtra("tipe","0");
                startActivity(intent);
            }
        });
        btnSuplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PelSupActivity.this, ListPelSupActivity.class);
                intent.putExtra("tipe","1");
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
