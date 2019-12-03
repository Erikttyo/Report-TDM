package com.example.reporttdm.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.PelSupAdapter;
import com.example.reporttdm.fragment.DatePickerFragment;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.GetPelSupResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.PelSup;
import com.example.reporttdm.model.Pembelian;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BayarPembelianActivity extends AppCompatActivity {

    Toolbar toolbar;
    CheckBox checkCash, checkHutang;
    ArrayList<Barang> barangList = new ArrayList<Barang>();
    TinyDB tinyDB;
    int modal = 0;
    int jual = 0;
    int jum = 0;
    ProgressDialog dialog;
    Button btnBayar;
    EditText edtKet, edtJatuhTempo;
    LinearLayout layoutJatuhTempo;
    TextView txSuplier;
    RecyclerView recyclerView;
    PelSupAdapter pelSupAdapter;
    ArrayList<PelSup> pelSupList = new ArrayList<PelSup>();
    private String id_pelsup = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bayar_pembelian);

        tinyDB = new TinyDB(this);

        dialog = new ProgressDialog(BayarPembelianActivity.this);
        dialog.setMessage("Harap tunggu..");

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txSuplier = findViewById(R.id.tx_suplier);
        layoutJatuhTempo = findViewById(R.id.layout_jatuh_tempo);
        edtJatuhTempo = findViewById(R.id.edt_jatuh_tempo);
        edtJatuhTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        edtKet = (EditText)findViewById(R.id.edt_keterangan);
        edtKet.setVisibility(View.GONE);
        btnBayar = (Button)findViewById(R.id.btn_bayar);
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransaksi();
            }
        });

        Bundle bundle = getIntent().getExtras();
        barangList = bundle.getParcelableArrayList("list");
        for (Barang b : barangList){
            System.out.println("tes : "+b.getNama()+", "+b.getHarga()+", "+b.getStok());
            modal = modal + (Integer.parseInt(b.getHarga())*Integer.parseInt(b.getStok()));
            jum = jum + Integer.parseInt(b.getStok());
        }
        setTitle(String.valueOf(modal));
        checkCash = (CheckBox)findViewById(R.id.check_cash);
        checkHutang = (CheckBox)findViewById(R.id.check_hutang);
        checkCash.setChecked(true);
        layoutJatuhTempo.setVisibility(View.GONE);
        checkCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCash.isChecked()){
                    checkHutang.setChecked(false);
                    edtKet.setVisibility(View.GONE);
                    layoutJatuhTempo.setVisibility(View.GONE);
                }
            }
        });
        checkHutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkHutang.isChecked()){
                    checkCash.setChecked(false);
                    edtKet.setVisibility(View.VISIBLE);
                    layoutJatuhTempo.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListPelsup);
        pelSupAdapter = new PelSupAdapter(pelSupList,this,"4");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BayarPembelianActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pelSupAdapter);

        dialog.show();
        getListPelSup();
    }

    public void setSupplier(int pos){
        txSuplier.setText("Suplier : "+pelSupList.get(pos).getNama().toString());
        id_pelsup = pelSupList.get(pos).getId_pelsup().toString();
        Toast.makeText(this, "Suplier "+txSuplier.getText().toString()+" dipilih",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void addTransaksi(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        String isCash;
        if (checkCash.isChecked()){
            isCash = "1";
        }else{
            isCash = "0";
        }
        Pembelian pembelian = new Pembelian(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                String.valueOf(modal),"0",String.valueOf(jum),"0",isCash,edtKet.getText().toString(),id_pelsup,edtJatuhTempo.getText().toString(),
                ((User)tinyDB.getObject("user_login", User.class)).getNama(),barangList);
        Call<InsertResponse> call = api.addTransaksi(pembelian);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(BayarPembelianActivity.this,"Transaksi berhasil",Toast.LENGTH_SHORT).show();
                    PembelianActivity.pembelianActivity.finish();
                    finish();
                }else{
                    Toast.makeText(BayarPembelianActivity.this,"Transaksi gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(BayarPembelianActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                        if (ps.getTipe().equals("1")){
                            pelSupList.add(ps);
                            Log.d("pembelian","nama suplier : "+ps.getNama());
                        }
                    }
                    Log.d("pembelian", "jumlah suplier : "+String.valueOf(pelSupList.size()));
                    pelSupAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    dialog.dismiss();
                    Toast.makeText(BayarPembelianActivity.this,"Tidak ada Supplier",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetPelSupResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(BayarPembelianActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        final Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtJatuhTempo.setText(String.valueOf(year) + "-" + String.format("%02d", (month + 1))
                        + "-" + String.format("%02d",dayOfMonth));
            }
        };

        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(onDateSetListener);
        date.show(getSupportFragmentManager(),"Jatuh Tempo");
    }

}
