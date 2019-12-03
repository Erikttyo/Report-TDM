package com.example.reporttdm.activity;

import android.app.ProgressDialog;
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
import com.example.reporttdm.model.GetTokoResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisPajSelectedActivity extends AppCompatActivity {

    Toolbar toolbar;
    TinyDB tinyDB;
    private EditText edtPajak, edtDiskon;
    private String pajak, diskon;
    private Button btnSimpan;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispaj_selected);

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Diskon dan Pajak");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(DisPajSelectedActivity.this);
        dialog.setMessage("Harap tunggu..");
        dialog.show();

        edtPajak = (EditText)findViewById(R.id.edt_pajak);
        edtDiskon = (EditText)findViewById(R.id.edt_diskon);
        btnSimpan = (Button)findViewById(R.id.btn_simpan_dispaj);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(edtPajak.getText().toString().equals("")||edtDiskon.getText().toString().equals(""))){
                    Toast.makeText(DisPajSelectedActivity.this,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                    if (!(pajak.equals(edtPajak.getText().toString())||diskon.equals(edtDiskon.getText().toString()))){
                        setPajak("1");
                    }else if (!pajak.equals(edtPajak.getText().toString())){
                        setPajak("0");
                    }else if (!diskon.equals(edtDiskon.getText().toString())){
                        setDiskon();
                    }
                }else{
                    Toast.makeText(DisPajSelectedActivity.this,"Kolom pajak atau diskon tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }
            }
        });
        getPajak();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getPajak(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetTokoResponse> call = api.getToko(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetTokoResponse>() {
            @Override
            public void onResponse(Call<GetTokoResponse> call, Response<GetTokoResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code()==1) {
                    System.out.println("masuk");
                    edtPajak.setText(response.body().getResult_toko().get(0).getPajak());
                    edtDiskon.setText(response.body().getResult_toko().get(0).getDiskon());
                    pajak = edtPajak.getText().toString();
                    diskon = edtDiskon.getText().toString();
                    dialog.dismiss();
                }else{
                    dialog.dismiss();
                    Toast.makeText(DisPajSelectedActivity.this,"Gagal memuat",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetTokoResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(DisPajSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setPajak(final String i){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.setPajak(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                edtPajak.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    System.out.println("masuk");
                    if (i.equals("1")){
                        setDiskon();
                    }else{
                        Toast.makeText(DisPajSelectedActivity.this,"Berhasil",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    dialog.dismiss();
                    Toast.makeText(DisPajSelectedActivity.this,"Gagal memuat",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(DisPajSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setDiskon(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.setDiskon(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                edtDiskon.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    System.out.println("masuk");
                    Toast.makeText(DisPajSelectedActivity.this,"Berhasil",Toast.LENGTH_SHORT).show();
                }else{
                    dialog.dismiss();
                    Toast.makeText(DisPajSelectedActivity.this,"Gagal memuat",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(DisPajSelectedActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
