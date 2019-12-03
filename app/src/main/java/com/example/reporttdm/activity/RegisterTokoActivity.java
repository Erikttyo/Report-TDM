package com.example.reporttdm.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterTokoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnRegis;
    private EditText edtNama, edtLokasi, edtNamaPem, edtTelp;
    private TinyDB tinyDB;
    private List<User> listUser = new ArrayList<User>();
    private List<Toko> listToko = new ArrayList<Toko>();
    private String namaPem, email, telpon, alamat, pass;
    private int toko_id, user_id;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_toko);

        tinyDB = new TinyDB(this);

        dialog = new ProgressDialog(RegisterTokoActivity.this);
        dialog.setMessage("Harap tunggu..");


        if (getIntent().hasExtra("nama")){
            namaPem = getIntent().getStringExtra("nama");
            email = getIntent().getStringExtra("email");
            telpon = getIntent().getStringExtra("telpon");
            alamat = getIntent().getStringExtra("alamat");
            pass = getIntent().getStringExtra("password");
            user_id = Integer.parseInt(getIntent().getStringExtra("id_user"));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_regis);
        setSupportActionBar(toolbar);
        setTitle("register Toko");

        btnRegis = (Button)findViewById(R.id.btn_regist);
        edtNama = (EditText)findViewById(R.id.edt_namat_regis);
        edtNamaPem = (EditText)findViewById(R.id.edt_namapt_regis);
        edtLokasi = (EditText)findViewById(R.id.edt_lokasit_regis);
        edtTelp = (EditText)findViewById(R.id.edt_telpont_regis);
        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(edtNama)||isEmpty(edtNamaPem)||isEmpty(edtLokasi)||isEmpty(edtTelp)){
                    Toast.makeText(RegisterTokoActivity.this,"Harap isi semua kolom",Toast.LENGTH_SHORT).show();
                }else {
                    dialog.show();
                    registerToko();
                    Toast.makeText(RegisterTokoActivity.this, "Registrasi berhasil",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterTokoActivity.this, MenuActivity.class));
                    finish();
                }
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void registerToko(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.registerToko(edtNama.getText().toString(),user_id,
                edtTelp.getText().toString(),edtNamaPem.getText().toString(),edtLokasi.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    System.out.println("regis berhasil");
                    Intent intent = new Intent(RegisterTokoActivity.this, LoginActivity.class);
                    Toast.makeText(RegisterTokoActivity.this,"Registrasi berhasil",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(RegisterTokoActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
