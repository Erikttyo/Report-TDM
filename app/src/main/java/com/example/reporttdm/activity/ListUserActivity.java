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
import com.example.reporttdm.adapter.UserAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.GetUserResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListUserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<User> userArrayList = new ArrayList<User>();
    Toolbar toolbar;
    FloatingActionButton fab;
    TinyDB tinyDB;
    String tipe;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("User");

        if (getIntent().hasExtra("tipe")){
            if (getIntent().getStringExtra("tipe").equals("0")){
                tipe = "0";
                setTitle("Pelanggan");
            }else{
                tipe = "1";
                setTitle("Suplier");
            }
        }

        //Bundle bundle = getIntent().getExtras();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListUser);
        userAdapter = new UserAdapter(userArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListUserActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(userAdapter);

        System.out.println("list : "+ userArrayList);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeUser);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListUser();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fab = (FloatingActionButton) findViewById(R.id.fab_listuser);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListUserActivity.this, CreateUserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getListUser(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetUserResponse> call = api.showUser(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Call<GetUserResponse> call, Response<GetUserResponse> response) {
                if (response.body().getStatus_code().equals("1")) {
                    userArrayList.clear();
                    userArrayList.addAll(response.body().getResult_user());
                    userAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ListUserActivity.this,"Gagal menampilkan",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetUserResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ListUserActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeContainer.setRefreshing(true);
        getListUser();
    }
}
