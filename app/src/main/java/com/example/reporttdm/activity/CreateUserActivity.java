package com.example.reporttdm.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Toolbar toolbar;
    TinyDB tinyDB;
    EditText edtNama, edtEmail, edtNoTelp, edtAlamat, edtPass, edtCPass;
    Button btnSimpan;
    String id,tipe,flag;
    private Spinner spinTipe;
    private ArrayAdapter arrayAdapter;
    private List<String> listNama = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Add User");

        edtNama = (EditText)findViewById(R.id.edt_nama_add);
        edtAlamat = (EditText)findViewById(R.id.edt_alamat_add);
        edtNoTelp = (EditText)findViewById(R.id.edt_telpon_add);
        edtPass = (EditText)findViewById(R.id.edt_password_add);
        edtCPass = (EditText)findViewById(R.id.edt_password2_add);
        spinTipe = (Spinner)findViewById(R.id.spin_tipe_add);
        edtEmail = (EditText)findViewById(R.id.edt_email_add);
        btnSimpan = (Button)findViewById(R.id.btn_add_user);

        listNama.add("Tipe user");
        listNama.add("Admin");
        listNama.add("Kasir");
        spinTipe.setOnItemSelectedListener(this);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listNama);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTipe.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateUserActivity.this,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                if (isEmpty(edtNama)||isEmpty(edtPass)||isEmpty(edtEmail)||isEmpty(edtAlamat)||isEmpty(edtCPass)||
                        isEmpty(edtNoTelp)||(spinTipe.getSelectedItemPosition()==0)){
                    Toast.makeText(CreateUserActivity.this,"Harap isi semua kolom",Toast.LENGTH_SHORT).show();
                }else if(!edtPass.getText().toString().equals(edtCPass.getText().toString())){
                    Toast.makeText(CreateUserActivity.this,"Konfirmasi password salah",Toast.LENGTH_SHORT).show();
                }else{
                    if (tipe.equals("Kasir")){
                        addUser("2");
                    }else if (tipe.equals("Admin")){
                        addUser("3");
                    }
                }
            }
        });

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void addUser(String tipe){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.addUser(edtNama.getText().toString(), ((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                edtNoTelp.getText().toString(),edtEmail.getText().toString(),edtAlamat.getText().toString(), tipe, edtPass.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    System.out.println("regis berhasil");
                    Toast.makeText(CreateUserActivity.this, "Add user berhasil", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CreateUserActivity.this, "Add user gagal", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(CreateUserActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if((spinner.getId() == R.id.spin_tipe_add)&&spinner.getSelectedItemPosition()>0) {
            int s = spinner.getSelectedItemPosition();
            tipe = listNama.get(s);
            Toast.makeText(CreateUserActivity.this,"Tipe user "+listNama.get(s),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
