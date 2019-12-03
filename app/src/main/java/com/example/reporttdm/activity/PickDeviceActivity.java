package com.example.reporttdm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.Perangkat;

public class PickDeviceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TinyDB tinyDB;
    private Perangkat bluetoothDevice = null;
    private TextView txNama;
    private EditText edtUcapan;
    private Button btnPilih, btnSimpanUcapan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_bluetooth);

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Pilih Bluetooth");

        txNama = (TextView)findViewById(R.id.tx_device_name);
        edtUcapan = (EditText)findViewById(R.id.edt_ucapan);
        btnPilih = (Button)findViewById(R.id.btn_pilih_device);
        btnSimpanUcapan = (Button)findViewById(R.id.btn_ucapan);
        btnPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(PickDeviceActivity.this,PickDeviceList.class),1);
            }
        });
        btnSimpanUcapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtUcapan.getText().toString().equals("")){
                    tinyDB.putString("ucapan",edtUcapan.getText().toString());
                    Toast.makeText(PickDeviceActivity.this,"Simpan ucapan berhasil",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PickDeviceActivity.this,"Simpan ucapan gagal",Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            bluetoothDevice = tinyDB.getObject("device",Perangkat.class);
            txNama.setText("Perangkat bluetooh : "+bluetoothDevice.getNama());
        }catch (Exception e){
            Toast.makeText(PickDeviceActivity.this,"Silahkan pilih perangkat bluetooth",Toast.LENGTH_SHORT).show();
        }
        try {
            edtUcapan.setText(tinyDB.getString("ucapan"));
        }catch (Exception e){

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            bluetoothDevice = tinyDB.getObject("device",Perangkat.class);
            txNama.setText("Perangkat bluetooh : "+bluetoothDevice.getNama());
            Toast.makeText(PickDeviceActivity.this,"Berhasil pilih perangkat",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(PickDeviceActivity.this,"pilih perangkat gagal",Toast.LENGTH_SHORT).show();
        }
    }
}
