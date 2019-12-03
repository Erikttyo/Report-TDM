package com.example.reporttdm.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.DBarangAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.GetBarangResponse;
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryTransaksiActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DBarangAdapter barangAdapter;
    ArrayList<Barang> barangArrayList = new ArrayList<Barang>();
    Toolbar toolbar;
    TinyDB tinyDB;
    String id_transaksi, sjumlah, jumlah, status, tanggal = "";
    private SwipeRefreshLayout swipeContainer;
    private String nilai;
    private TextView txSJumlah, txTanggal, txStatus, txJumlah;
    private ProgressDialog progressDialog;
    private String timeStamp;
    private User user;
    private Toko toko;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaksi);

        tinyDB = new TinyDB(this);
        user = tinyDB.getObject("user_login",User.class);
        toko = tinyDB.getObject("toko_login",Toko.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Harap tunggu...");

        nilai = "0";

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txSJumlah = (TextView)findViewById(R.id.tx_sjumlah_laporan);
        txJumlah = (TextView)findViewById(R.id.tx_jumlah_laporan);
        txStatus = (TextView)findViewById(R.id.tx_status_laporan);
        txTanggal = (TextView)findViewById(R.id.tx_tgl_laporan);

        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date());

        if (getIntent().hasExtra("id_transaksi")){
            id_transaksi = getIntent().getStringExtra("id_transaksi");
            sjumlah = getIntent().getStringExtra("sjumlah");
            jumlah = getIntent().getStringExtra("jumlah");
            status = getIntent().getStringExtra("status");
            tanggal = getIntent().getStringExtra("tanggal");
            txJumlah.setText(jumlah);
            txSJumlah.setText(sjumlah);
            txTanggal.setText(tanggal);
            txStatus.setText(status);
            setTitle("No. Transaksi : "+id_transaksi);
        }

        //Bundle bundle = getIntent().getExtras();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerHTransaksi);
        barangAdapter = new DBarangAdapter(barangArrayList,this,4);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HistoryTransaksiActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(barangAdapter);

        System.out.println("list : "+ barangArrayList);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeHTransaksi);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListBarang();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(true);
        getListBarang();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getListBarang(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetBarangResponse> call = api.showListTransaksi(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),id_transaksi);
        System.out.println("masuk");
        call.enqueue(new Callback<GetBarangResponse>() {
            @Override
            public void onResponse(Call<GetBarangResponse> call, Response<GetBarangResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code()==1) {
                    barangArrayList.clear();
                    barangArrayList.addAll(response.body().getResult_barang());
                    barangAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(HistoryTransaksiActivity.this,"Tidak ada barang",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetBarangResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(HistoryTransaksiActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
