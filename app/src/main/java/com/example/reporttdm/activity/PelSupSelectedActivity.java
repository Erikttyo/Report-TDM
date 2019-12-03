package com.example.reporttdm.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.PelSup;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PelSupSelectedActivity extends AppCompatActivity {

    Toolbar toolbar;
    List<PelSup> pelSupList = new ArrayList<PelSup>();
    TinyDB tinyDB;
    EditText edtNama, edtNoTelp, edtAlamat;
    Button btnSimpan, btnDelete;
    String id,tipe,flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelsup_selected);

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtNama = (EditText)findViewById(R.id.edt_nama_pelsup);
        edtAlamat = (EditText)findViewById(R.id.edt_alamat_pelsup);
        edtNoTelp = (EditText)findViewById(R.id.edt_notelp_pelsup);
        btnSimpan = (Button)findViewById(R.id.btn_simpan_pelsup);
        btnDelete = (Button)findViewById(R.id.btn_delete_pelsup);

        if (getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            tipe = getIntent().getStringExtra("tipe");
            edtNama.setText(getIntent().getStringExtra("nama"));
            edtAlamat.setText(getIntent().getStringExtra("alamat"));
            edtNoTelp.setText(getIntent().getStringExtra("notelp"));
            flag = getIntent().getStringExtra("flag");
            setTitle("Profile Pelanggan");
        }else{
            flag = "1";
            tipe = getIntent().getStringExtra("tipe");
            setTitle("Profile Suplier");
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PelSupSelectedActivity.this,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                if (flag.equals("0")){
                    updatePelSup();
                }else{
                    addPelsup();
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PelSupSelectedActivity.this,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                deletePelSup(id);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void addPelsup(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.addPelSup(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                edtNama.getText().toString(),edtNoTelp.getText().toString(),edtAlamat.getText().toString(),tipe);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    if (tipe.equals("0")){
                        Toast.makeText(PelSupSelectedActivity.this,"Tambah pelanggan berhasil",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(PelSupSelectedActivity.this,"Tambah suplier berhasil",Toast.LENGTH_SHORT).show();
                    }
                    edtNama.setText("");
                    edtAlamat.setText("");
                    edtNoTelp.setText("");
                }else{
                    Toast.makeText(PelSupSelectedActivity.this,"Gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(PelSupSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updatePelSup(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.updatePelsup(id, edtNama.getText().toString(),edtNoTelp.getText().toString(),edtAlamat.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    if (tipe.equals("0")){
                        Toast.makeText(PelSupSelectedActivity.this,"Update pelanggan berhasil",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(PelSupSelectedActivity.this,"Update suplier berhasil",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(PelSupSelectedActivity.this,"Gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(PelSupSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deletePelSup(String id) {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.deletePelSup(id);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(PelSupSelectedActivity.this, "Delete berhasil", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(PelSupSelectedActivity.this, "Delete gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(PelSupSelectedActivity.this, "Harap koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
