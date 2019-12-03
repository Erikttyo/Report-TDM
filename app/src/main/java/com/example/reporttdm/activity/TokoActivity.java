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
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokoActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ProgressDialog dialog;
    private TinyDB tinyDB;
    EditText edtNama, edtNamaP, edtTelp, edtLokasi;
    Button btnSimpan;
    String exist = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toko);

        tinyDB = new TinyDB(this);

        dialog = new ProgressDialog(TokoActivity.this);
        dialog.setMessage("Harap tunggu..");
        dialog.show();

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Edit Profile Toko");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtNama = (EditText)findViewById(R.id.edt_nama_toko);
        edtNamaP = (EditText)findViewById(R.id.edt_namap_toko);
        edtTelp = (EditText)findViewById(R.id.edt_telpon_toko);
        edtLokasi = (EditText)findViewById(R.id.edt_lokasi_toko);
        btnSimpan = (Button)findViewById(R.id.btn_simpan_toko);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if (exist.equals("1")) {
                    updateToko();
                }else{
                    registerToko();
                }
            }
        });
        getToko();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getToko(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetTokoResponse> call = api.getToko(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetTokoResponse>() {
            @Override
            public void onResponse(Call<GetTokoResponse> call, Response<GetTokoResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code()==1) {
                    edtNama.setText(response.body().getResult_toko().get(0).getNama());
                    edtNamaP.setText(response.body().getResult_toko().get(0).getNama_pemilik());
                    edtLokasi.setText(response.body().getResult_toko().get(0).getLokasi());
                    edtTelp.setText(response.body().getResult_toko().get(0).getTelephone());
                    exist = "1";
                    dialog.dismiss();
                }else{
                    exist = "0";
                    Toast.makeText(TokoActivity.this, "Belum mengisi data toko", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<GetTokoResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(TokoActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateToko(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.updateToko(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                edtNama.getText().toString(),edtTelp.getText().toString(),edtNamaP.getText().toString(),
                edtLokasi.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    dialog.dismiss();
                    Toast.makeText(TokoActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                    Toko toko = ((Toko)tinyDB.getObject("toko_login", Toko.class));
                    toko.setNama(edtNama.getText().toString());
                    toko.setTelephone(edtTelp.getText().toString());
                    toko.setNama_pemilik(edtNamaP.getText().toString());
                    toko.setLokasi(edtLokasi.getText().toString());
                    tinyDB.putObject("toko_login",toko);
                    getToko();
                }else{
                    dialog.dismiss();
                    Toast.makeText(TokoActivity.this, "Update gagal", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(TokoActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerToko(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.registerToko(edtNama.getText().toString(),Integer.parseInt(((User)tinyDB.getObject("user_login", User.class)).getId()),
                edtTelp.getText().toString(),edtNamaP.getText().toString(),edtLokasi.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    System.out.println("regis berhasil");
                    Toast.makeText(TokoActivity.this,"Update berhasil",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{
                    dialog.dismiss();
                    Toast.makeText(TokoActivity.this, "Update gagal", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(TokoActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
