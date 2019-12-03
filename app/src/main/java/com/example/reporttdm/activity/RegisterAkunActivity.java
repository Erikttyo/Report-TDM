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
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.RegisterResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterAkunActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnRegis;
    private TextView txLogin;
    private TinyDB tinyDB;
    private EditText edtNama, edtEmail, edtTelepon, edtAlamat, edtPass, edtCPass;
    private List<User> listUser = new ArrayList<User>();
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dialog = new ProgressDialog(RegisterAkunActivity.this);
        dialog.setMessage("Harap tunggu..");

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_regis);
        setSupportActionBar(toolbar);
        setTitle("register Akun");

        btnRegis = (Button)findViewById(R.id.btn_regis);
        txLogin = (TextView)findViewById(R.id.tx_login);
        edtNama = (EditText)findViewById(R.id.edt_nama_regis);
        edtEmail = (EditText)findViewById(R.id.edt_email_regis);
        edtTelepon = (EditText)findViewById(R.id.edt_telpon_regis);
        edtAlamat = (EditText) findViewById(R.id.edt_alamat_regis);
        edtPass = (EditText)findViewById(R.id.edt_password_regis);
        edtCPass = (EditText)findViewById(R.id.edt_password2_regis);
        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(edtNama)||isEmpty(edtEmail)||isEmpty(edtTelepon)||isEmpty(edtAlamat)
                        ||isEmpty(edtPass)||isEmpty(edtCPass)){
                    Toast.makeText(RegisterAkunActivity.this,"Harap isi semua kolom",Toast.LENGTH_SHORT).show();
                }else if(!edtPass.getText().toString().equals(edtCPass.getText().toString())) {
                    Toast.makeText(RegisterAkunActivity.this,"Konfirmasi password salah",Toast.LENGTH_SHORT).show();
                }else {
                    dialog.show();
                    register();
                }
            }
        });
        txLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterAkunActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void register(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<RegisterResponse> call = api.register(edtNama.getText().toString(),edtEmail.getText().toString(),
                edtTelepon.getText().toString(),edtAlamat.getText().toString(),edtPass.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    System.out.println("regis berhasil");
                    Intent intent = new Intent(RegisterAkunActivity.this, RegisterTokoActivity.class);
                    intent.putExtra("nama",edtNama.getText().toString());
                    intent.putExtra("email",edtEmail.getText().toString());
                    intent.putExtra("telpon",edtTelepon.getText().toString());
                    intent.putExtra("alamat",edtAlamat.getText().toString());
                    intent.putExtra("password",edtPass.getText().toString());
                    intent.putExtra("id_user",String.valueOf(response.body().getResult_regis()));
                    //Toast.makeText(RegisterAkunActivity.this,"Tambah alamat berhasil",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(RegisterAkunActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
