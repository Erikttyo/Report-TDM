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
import com.example.reporttdm.model.LoginResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnLogin;
    private TextView txDaftar;
    private EditText edtEmail, edtPassword;
    private TinyDB tinyDB;
    private List<User> listUser = new ArrayList<User>();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tinyDB = new TinyDB(this);
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Harap tunggu..");
        try {
            if (tinyDB.getObject("user_login",User.class)!=null){
                startActivity(new Intent(LoginActivity.this,MenuActivity.class));
                finish();
            }
        }catch (Exception e){

        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_regis);
        setSupportActionBar(toolbar);
        setTitle("Login");

        btnLogin = (Button)findViewById(R.id.btn_login);
        txDaftar = (TextView)findViewById(R.id.tx_daftar);
        edtEmail = (EditText)findViewById(R.id.edt_email_login);
        edtPassword = (EditText)findViewById(R.id.edt_pass_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(edtEmail)||isEmpty(edtPassword)){
                    Toast.makeText(LoginActivity.this,"Harap isi semua kolom",Toast.LENGTH_SHORT).show();
                }else{
                    dialog.show();
                    int valid = 0;
                    login();
                }
            }
        });
        txDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterAkunActivity.class));
                finish();
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void login(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<LoginResponse> call = api.login(edtEmail.getText().toString(),edtPassword.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code()==1) {
                    System.out.println("login berhasil");
                    tinyDB.putObject("user_login",response.body().getResult_login().get(0));
                    tinyDB.putObject("toko_login",response.body().getResult_toko().get(0));
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    //Toast.makeText(RegisterTokoActivity.this,"Registrasi berhasil",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    startActivity(intent);
                    finish();
                }else {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login gagal", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
