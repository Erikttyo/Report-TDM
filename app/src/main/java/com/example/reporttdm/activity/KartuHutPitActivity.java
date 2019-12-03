package com.example.reporttdm.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.KartuHutangAdapter;
import com.example.reporttdm.helper.BluetoothPrinter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.GetKartuHutangResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.KartuHutang;
import com.example.reporttdm.model.Perangkat;
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KartuHutPitActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    KartuHutangAdapter hutPitAdapter;
    ArrayList<KartuHutang> hutPitArrayList = new ArrayList<KartuHutang>();
    Toolbar toolbar;
    FloatingActionButton fab;
    TinyDB tinyDB;
    String jenis;
    private SwipeRefreshLayout swipeContainer;
    private String id_hutpit, jatuhTempo, total, sisa, dibayar;
    private String nilai;
    private TextView txJatuhTempo, txTotal, txSisa, txDibayar;
    private ProgressDialog progressDialog;
    private String timeStamp;
    private User user;
    private Toko toko;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kartu_hutpit);

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

        txJatuhTempo = (TextView)findViewById(R.id.tx_jatuh_tempo);
        txDibayar = (TextView)findViewById(R.id.tx_dibayar_hutpit);
        txSisa = (TextView)findViewById(R.id.tx_sisa_hutpit);
        txTotal = (TextView)findViewById(R.id.tx_total_hutpit);

        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date());

        if (getIntent().hasExtra("jenis")){
            if (getIntent().getStringExtra("jenis").equals("1")){
                jenis = "1";
            }else{
                jenis = "0";
            }
            setTitle(getIntent().getStringExtra("nama"));
            id_hutpit = getIntent().getStringExtra("id_hutpit");
            jatuhTempo = getIntent().getStringExtra("jatuh_tempo");
            total = getIntent().getStringExtra("total");
            sisa = getIntent().getStringExtra("sisa");
            dibayar = getIntent().getStringExtra("dibayar");
            System.out.println("id_hutpit : "+id_hutpit);
        }

        txDibayar.setText("Dibayar : Rp "+doubleToStringNoDecimal(Double.parseDouble(dibayar)));
        txJatuhTempo.setText("Jatuh tempo : "+jatuhTempo);
        txTotal.setText("Total : Rp "+doubleToStringNoDecimal(Double.parseDouble(total)));
        txSisa.setText("Sisa : Rp "+doubleToStringNoDecimal(Double.parseDouble(sisa)));

        //Bundle bundle = getIntent().getExtras();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerKartuHutPit);
        hutPitAdapter = new KartuHutangAdapter(hutPitArrayList,jenis);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(KartuHutPitActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hutPitAdapter);

        System.out.println("list : "+ hutPitArrayList);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeKartuHutpit);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListHutPit();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fab = (FloatingActionButton)findViewById(R.id.fab_bayar_hutpit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogBayar();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.laporan_menu, menu);
        menu.findItem(R.id.sort_bulan).setVisible(false);
        menu.findItem(R.id.sort_tahun).setVisible(false);
        menu.findItem(R.id.sort_hari).setVisible(false);
        menu.findItem(R.id.print_excel).setVisible(false);
        menu.findItem(R.id.print_pdf).setTitle("Cetak");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.print_pdf:
                printStruk2();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void printStruk2(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        //BluetoothDevice mBtDevice = btAdapter.getBondedDevices().iterator().next();   // Get first paired device
        //BluetoothDevice mBtDevice = tinyDB.getObject("device",BluetoothDevice.class);
        try {
            BluetoothDevice mBtDevice =
                    btAdapter.getRemoteDevice(tinyDB.getObject("device", Perangkat.class).getAddress());
            final BluetoothPrinter mPrinter = new BluetoothPrinter(mBtDevice);
            mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

                @Override
                public void onConnected() {
                    mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
                    //mPrinter.printText("Hello World!");
                    //mPrinter.addNewLine();
                    Date date = new Date() ;
                    //timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
                    mPrinter.printCustom(toko.getNama(),3,1);
                    mPrinter.printCustom("Alamat : "+toko.getLokasi(),1,1);
                    mPrinter.printCustom("HP/WA : "+user.getTelephone(),1,1);
                    mPrinter.printCustom("Petugas:"+user.getNama(),1,1);
                    mPrinter.printCustom("--------------------------------",1,0);
                    //printUnicode();
                    //mPrinter.printCustom("--------------------------------",1,0);
                    mPrinter.printCustom(timeStamp.substring(0,10) + " "+timeStamp.substring(11,19),1,1);
                    mPrinter.printCustom("Pelanggan : "+getIntent().getStringExtra("nama"),1,1);
                    mPrinter.printCustom("Jumlah Utang : "+getIntent().getStringExtra("total"),1,1);
                    mPrinter.printCustom("Sisa Utang : "+getIntent().getStringExtra("sisa"),1,1);
                    mPrinter.printCustom("Status : "+
                            (getIntent().getStringExtra("sisa").equals("0") ? "Lunas" : "Belum Lunas"),1,1);

                    //mPrinter.printCustom("No. Transaksi : "+id_transaksi,1,0);
                    mPrinter.printCustom("--------------------------------",1,0);
                    for (KartuHutang kartuHutang : hutPitArrayList){
                        mPrinter.printCustom(kartuHutang.getTanggal(),1,1);
                        String jumhar = kartuHutang.getJumlah() + " X " + doubleToStringNoDecimal(kartuHutang.getNilai()) + " = ";
                        int jumlah = Integer.parseInt(kartuHutang.getJumlah())*Integer.parseInt(kartuHutang.getNilai());
                        String jumlahString = doubleToStringNoDecimal(String.valueOf(jumlah));
                        mPrinter.printCustom(jumhar+jumlahString,1,1);
                    }
                    mPrinter.printCustom("--------------------------------",1,0);
                    mPrinter.printCustom("Total   : "+doubleToStringNoDecimal(total),1,1);
                    mPrinter.printCustom("Bayar   : "+doubleToStringNoDecimal(dibayar),1,1);
                    mPrinter.printCustom("Sisa    : "+doubleToStringNoDecimal(sisa),1,1);
                    mPrinter.printCustom("--------------------------------",1,0);
                    mPrinter.printCustom("Terima kasih telah berbelanja di Toko "+toko.getNama(),1,1);
                    mPrinter.printCustom(" ",1,0);
                    //resetPrint(); //reset printer
                    mPrinter.printNewLine();
                    mPrinter.printNewLine();
                    mPrinter.finish();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailed() {
                    Log.d("BluetoothPrinter", "Conection failed");
                    progressDialog.dismiss();
                }
            });
        }catch (Exception e){
            Toast.makeText(KartuHutPitActivity.this,"Belum setting perangkat bluetooth",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public String getSisa() {
        return sisa;
    }

    public void setSisa(String sisa) {
        this.sisa = sisa;
    }

    public static String doubleToStringNoDecimal(String number) {
        Double d = Double.parseDouble(number);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

    public void getListHutPit(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetKartuHutangResponse> call = api.showListHutPit(id_hutpit);
        System.out.println("masuk");
        call.enqueue(new Callback<GetKartuHutangResponse>() {
            @Override
            public void onResponse(Call<GetKartuHutangResponse> call, Response<GetKartuHutangResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    hutPitArrayList.clear();
                    int total = 0;
                    int dibayar = 0;
                    int sisa = 0;
                    String jatuhTempo = "";
                    for (KartuHutang hutPit : response.body().getResult_hutpit()){
                        if (hutPit.getJenis().equals(jenis)){
                            hutPitArrayList.add(hutPit);
                            if (jenis.equals("0")) {
                                total = Integer.parseInt(hutPit.getJual());
                            }else{
                                total = Integer.parseInt(hutPit.getModal());
                            }
                            dibayar = dibayar + Integer.parseInt(hutPit.getNilai());
                            jatuhTempo = hutPit.getJatuh_tempo();
                        }
                    }
                    sisa = total - dibayar;
                    txDibayar.setText("Dibayar : Rp "+doubleToStringNoDecimal(Double.parseDouble(String.valueOf(dibayar))));
                    txJatuhTempo.setText("Jatuh tempo : "+jatuhTempo);
                    txTotal.setText("Total : Rp "+doubleToStringNoDecimal(Double.parseDouble(String.valueOf(total))));
                    txSisa.setText("Sisa : Rp "+doubleToStringNoDecimal(Double.parseDouble(String.valueOf(sisa))));
                    setSisa(String.valueOf(sisa));
                    hutPitAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }else if(response.body().getStatus_code().equals("3")){
                    Toast.makeText(KartuHutPitActivity.this, "Sudah lunas", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<GetKartuHutangResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(KartuHutPitActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeContainer.setRefreshing(true);
        getListHutPit();
    }

    public void showDialogBayar(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(KartuHutPitActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_bayar_hutang, null);
        dialogBuilder.setView(dialogView);

        TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_title);
        txTitle.setText("Bayar");
        final EditText edtNilai = (EditText) dialogView.findViewById(R.id.edt_nilai);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.show();
                nilai = edtNilai.getText().toString();
                System.out.println("nilai : "+nilai+" sisa : "+sisa);
                if (Integer.parseInt(nilai)>Integer.parseInt(sisa)){
                    Toast.makeText(KartuHutPitActivity.this, "Maaf nominal melebihi sisa",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else{
                    bayarHutPit();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void bayarHutPit(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = jenis.equals("0") ?
                api.bayarPiutang(((User) tinyDB.getObject("user_login", User.class)).getId_toko(), id_hutpit, nilai) :
                api.bayarHutang(((User) tinyDB.getObject("user_login", User.class)).getId_toko(), id_hutpit, nilai);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(KartuHutPitActivity.this, "Bayar berhasil", Toast.LENGTH_SHORT).show();
                }else if(response.body().getStatus_code().equals("3")){
                    Toast.makeText(KartuHutPitActivity.this, "Sudah lunas", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(KartuHutPitActivity.this, "Bayar gagal", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                getListHutPit();
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    progressDialog.dismiss();
                    Toast.makeText(KartuHutPitActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }
}
