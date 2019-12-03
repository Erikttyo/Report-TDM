package com.example.reporttdm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.PelSupAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.GetPelSupResponse;
import com.example.reporttdm.model.PelSup;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPelSupActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    PelSupAdapter pelSupAdapter;
    ArrayList<PelSup> pelSupList = new ArrayList<PelSup>();
    Toolbar toolbar;
    FloatingActionButton fab;
    TinyDB tinyDB;
    String tipe;
    String pelsup = "";
    String jenis = "0";
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pelsup);

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().hasExtra("tipe")){
            if (getIntent().getStringExtra("tipe").equals("0")){
                tipe = "0";
                jenis = "0";
                setTitle("Pelanggan");
                pelsup = "Pelanggan";
            }else if(getIntent().getStringExtra("tipe").equals("1")){
                tipe = "1";
                jenis = "1";
                setTitle("Suplier");
                pelsup = "Suplier";
            }else if(getIntent().getStringExtra("tipe").equals("3")){
                tipe = "3";
                jenis = "0";
                setTitle("Pelanggan");
                pelsup = "Pelanggan";
            }
        }

        //Bundle bundle = getIntent().getExtras();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListPelsup);
        pelSupAdapter = new PelSupAdapter(pelSupList,this,tipe);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListPelSupActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pelSupAdapter);

        System.out.println("list : "+ pelSupList);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipePelsup);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListPelSup();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        fab = (FloatingActionButton) findViewById(R.id.fab_listpelsup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListPelSupActivity.this, PelSupSelectedActivity.class);
                intent.putExtra("tipe",tipe);
                startActivity(intent);
            }
        });
        if (tipe.equals("3")){
            fab.setVisibility(View.GONE);
        }else{
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setFinish(){
        finish();
    }

    public void getListPelSup(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetPelSupResponse> call = api.showListPelSup(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetPelSupResponse>() {
            @Override
            public void onResponse(Call<GetPelSupResponse> call, Response<GetPelSupResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    pelSupList.clear();
                    for (PelSup ps : response.body().getResult_pelsup()){
                        if (ps.getTipe().equals(jenis)){
                            pelSupList.add(ps);
                        }
                    }
                    pelSupAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ListPelSupActivity.this,"Tidak ada "+pelsup,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetPelSupResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ListPelSupActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeContainer.setRefreshing(true);
        getListPelSup();
    }
}
