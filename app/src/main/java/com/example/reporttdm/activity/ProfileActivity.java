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
import com.example.reporttdm.model.GetProfileResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ProgressDialog dialog;
    private TinyDB tinyDB;
    private EditText edtNama, edtEmail, edtTelpon, edtAlamat, edtPass, edtPass2;
    private Button btnSimpanProfile;
    private String oldPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tinyDB = new TinyDB(this);

        dialog = new ProgressDialog(ProfileActivity.this);
        dialog.setMessage("Harap tunggu..");
        dialog.show();

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtNama = (EditText)findViewById(R.id.edt_nama_profile);
        edtAlamat = (EditText)findViewById(R.id.edt_alamat_profile);
        edtEmail = (EditText)findViewById(R.id.edt_email_profile);
        edtTelpon = (EditText)findViewById(R.id.edt_telpon_profile);
        edtPass = (EditText)findViewById(R.id.edt_password_profile);
        edtPass2 = (EditText)findViewById(R.id.edt_password2_profile);
        btnSimpanProfile = (Button)findViewById(R.id.btn_simpan_profile);
        btnSimpanProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oldPass.equals(edtPass.getText().toString())){
                    dialog.show();
                    updateProfile();
                }else{
                    if (edtPass.getText().toString().equals(edtPass2.getText().toString())){
                        dialog.show();
                        updateProfile();
                    }else {
                        Toast.makeText(ProfileActivity.this,"Konfirmasi password salah",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        getProfile();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getProfile(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetProfileResponse> call = api.getUser(((User)tinyDB.getObject("user_login", User.class)).getId());
        System.out.println("masuk");
        call.enqueue(new Callback<GetProfileResponse>() {
            @Override
            public void onResponse(Call<GetProfileResponse> call, Response<GetProfileResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code()==1) {
                    edtNama.setText(response.body().getResult_user().get(0).getNama());
                    edtAlamat.setText(response.body().getResult_user().get(0).getAlamat());
                    edtEmail.setText(response.body().getResult_user().get(0).getEmail());
                    edtTelpon.setText(response.body().getResult_user().get(0).getTelephone());
                    edtPass.setText(response.body().getResult_user().get(0).getPassword());
                    oldPass = response.body().getResult_user().get(0).getPassword();
                    dialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateProfile(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.updateUser(((User)tinyDB.getObject("user_login", User.class)).getId(),
                edtNama.getText().toString(),edtEmail.getText().toString(),edtTelpon.getText().toString(),
                edtAlamat.getText().toString(),edtPass.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    dialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                    User user = ((User)tinyDB.getObject("user_login", User.class));
                    user.setNama(edtNama.getText().toString());
                    user.setEmail(edtEmail.getText().toString());
                    user.setTelephone(edtTelpon.getText().toString());
                    user.setAlamat(edtAlamat.getText().toString());
                    user.setPassword(edtPass.getText().toString());
                    tinyDB.putObject("user_login",user);
                    getProfile();
                }else{
                    dialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Update gagal", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
