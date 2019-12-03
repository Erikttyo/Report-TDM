package com.example.reporttdm.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.AkunKasAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.AkunKas;
import com.example.reporttdm.model.GetAkunKasResponse;
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

public class AkunKasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AkunKasAdapter akunKasAdapter;
    private Toolbar toolbar;
    private List<AkunKas> akunKasList = new ArrayList<AkunKas>();
    private TinyDB tinyDB;
    private SwipeRefreshLayout swipeContainer;
    private EditText edtNama;
    private Button btnSimpan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun_kas);

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Akun Kas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtNama = (EditText)findViewById(R.id.edt_nama_akun);
        btnSimpan = (Button)findViewById(R.id.btn_simpan_akun);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtNama.getText().toString().equals("")){
                    Toast.makeText(AkunKasActivity.this,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                    addAkunKas();
                }else{
                    Toast.makeText(AkunKasActivity.this,"Harap isi nama akun kas",Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerAkunKas);
        akunKasAdapter = new AkunKasAdapter(akunKasList, AkunKasActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AkunKasActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(akunKasAdapter);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeAkunKas);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListAkunKas();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(true);
        getListAkunKas();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getListAkunKas(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetAkunKasResponse> call = api.showAkunKas(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetAkunKasResponse>() {
            @Override
            public void onResponse(Call<GetAkunKasResponse> call, Response<GetAkunKasResponse> response) {
                if (response.body().getStatusCode().equals("1")) {
                    akunKasList.clear();
                    akunKasList.addAll(response.body().getResultAkunKas());
                    akunKasAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(AkunKasActivity.this,"Tidak ada akun kas",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetAkunKasResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(AkunKasActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addAkunKas(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.addAkunKas(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                edtNama.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    swipeContainer.setRefreshing(true);
                    getListAkunKas();
                    Toast.makeText(AkunKasActivity.this,"Tambah akun kas berhasil",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AkunKasActivity.this,"Tambah akun kas gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(AkunKasActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
