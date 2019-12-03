package com.example.reporttdm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.DBarangAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.GetBarangResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DBarangActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;
    DBarangAdapter dBarangAdapter;
    FloatingActionButton fab;
    Toolbar toolbar;
    List<Barang> barangList = new ArrayList<Barang>();
    int pos,tipe;
    private TinyDB tinyDB;
    private SwipeRefreshLayout swipeContainer;
    private ImageButton imgButton, gridButton, listButton;
    private EditText edtSearch;
    private int modeView = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbarang);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Database Barang");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tinyDB = new TinyDB(this);

        if (getIntent().hasExtra("tipe")){
            if (getIntent().getStringExtra("tipe").equals("1")){
                tipe = 1;
            }else {
                tipe = 0;
            }
        }

        edtSearch = (EditText)findViewById(R.id.edt_searchdb);
        imgButton = (ImageButton)findViewById(R.id.btn_search_barang);
        gridButton = (ImageButton)findViewById(R.id.btn_grid_barang);
        listButton = (ImageButton)findViewById(R.id.btn_list_barang);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter(edtSearch.getText().toString());
            }
        });
        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeView = 1;
                dBarangAdapter = new DBarangAdapter(barangList,DBarangActivity.this,0);
                recyclerView.setLayoutManager(new GridLayoutManager(DBarangActivity.this, 3));
                recyclerView.setAdapter(dBarangAdapter);
                gridButton.setVisibility(View.GONE);
                listButton.setVisibility(View.VISIBLE);
            }
        });
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeView = 0;
                dBarangAdapter = new DBarangAdapter(barangList,DBarangActivity.this,1);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DBarangActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(dBarangAdapter);
                listButton.setVisibility(View.GONE);
                gridButton.setVisibility(View.VISIBLE);
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerDBarang);
        dBarangAdapter = new DBarangAdapter(barangList,this,tipe);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DBarangActivity.this, LinearLayoutManager.VERTICAL, false);
        if (tipe==0){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }else {
            recyclerView.setLayoutManager(layoutManager);
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dBarangAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab_dbarang);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DBarangActivity.this, DBarangSelectedActivity.class);
                startActivity(intent);
            }
        });
        if (tipe==1){
            fab.setVisibility(View.GONE);

        }else {
            fab.setVisibility(View.VISIBLE);
        }

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeDBarang);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(this);
        //swipeContainer.setRefreshing(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setFinish(){
        finish();
    }

    @Override
    public void onRefresh() {
        getListBarang();
    }

    public void getListBarang(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetBarangResponse> call = api.showBarang(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetBarangResponse>() {
            @Override
            public void onResponse(Call<GetBarangResponse> call, Response<GetBarangResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code()==1) {
                    barangList.clear();
                    barangList.addAll(response.body().getResult_barang());
                    dBarangAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
//                    tes();
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(DBarangActivity.this,"Tidak ada barang",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetBarangResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(DBarangActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeContainer.setRefreshing(true);
        getListBarang();
    }

    public void filter(String text){
        List<Barang> temp = new ArrayList();
        for(Barang d: barangList){
            if(d.getNama().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        dBarangAdapter.updateList(temp);
    }
}
