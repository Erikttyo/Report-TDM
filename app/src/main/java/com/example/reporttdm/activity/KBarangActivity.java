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
import com.example.reporttdm.adapter.KBarangAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.GetKategoriResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.KategoriBarang;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KBarangActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private KBarangAdapter kBarangAdapter;
    private Toolbar toolbar;
    private List<KategoriBarang> kategoriBarangList = new ArrayList<KategoriBarang>();
    private TinyDB tinyDB;
    private SwipeRefreshLayout swipeContainer;
    private EditText edtNama;
    private Button btnSimpan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kbarang);

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Kategori Barang");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtNama = (EditText)findViewById(R.id.edt_namak);
        btnSimpan = (Button)findViewById(R.id.btn_simpan_kategori);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtNama.getText().toString().equals("")){
                    Toast.makeText(KBarangActivity.this,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                    addKategori();
                }else{
                    Toast.makeText(KBarangActivity.this,"Harap isi nama kategori",Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerKBarang);
        kBarangAdapter = new KBarangAdapter(kategoriBarangList, KBarangActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(KBarangActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(kBarangAdapter);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeKBarang);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListKategori();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(true);
        getListKategori();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getListKategori(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetKategoriResponse> call = api.showKategori(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetKategoriResponse>() {
            @Override
            public void onResponse(Call<GetKategoriResponse> call, Response<GetKategoriResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    System.out.println("masuk kategoori");
                    kategoriBarangList.clear();
                    kategoriBarangList.addAll(response.body().getResult_kategori());
                    kBarangAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(KBarangActivity.this,"Tidak ada kategori barang",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetKategoriResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(KBarangActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addKategori(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.addKategori(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                edtNama.getText().toString());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    swipeContainer.setRefreshing(true);
                    getListKategori();
                    Toast.makeText(KBarangActivity.this,"Tambah kategori berhasil",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(KBarangActivity.this,"Tambah kategori gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(KBarangActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
