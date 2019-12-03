package com.example.reporttdm.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.fragment.DatePickerFragment;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.PelSup;
import com.example.reporttdm.model.Pembelian;
import com.example.reporttdm.model.Pemesanan;
import com.example.reporttdm.model.Transaksi;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputBayarActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton btnLanjut, btnPesanan;
    TextView txTotal, txBayar, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9
            , btnC, btn0, btnDot, txNama;
    String temp;
    ArrayList<Transaksi> arrayList = new ArrayList<Transaksi>();
    ArrayList<Barang> barangList = new ArrayList<Barang>();
    int modal = 0;
    int jual = 0;
    int jum = 0;
    TinyDB tinyDB;
    ArrayList<PelSup> pelSupList = new ArrayList<PelSup>();
    String id_transaksi="0";
    String pesanan = "";
    String id_pelsup = "0";
    String bayar = "";
    public static Activity inputBayarActivity;
    private CheckBox checkPiutang;
    private String jatuhTempo = "";
    private EditText edtJatuhTempo;
    private String pajak,diskon = "0";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_bayar);

        inputBayarActivity = this;

        tinyDB = new TinyDB(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Harap tunggu...");

        toolbar = (Toolbar) findViewById(R.id.toolbar_input_bayar);
        setSupportActionBar(toolbar);
        setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        arrayList = bundle.getParcelableArrayList("list");
        if (getIntent().hasExtra("pesanan")){
            pesanan = getIntent().getStringExtra("pesanan");
        }
        if (getIntent().hasExtra("diskon")){
            diskon = getIntent().getStringExtra("diskon");
            pajak = getIntent().getStringExtra("pajak");
        }

        for (Transaksi t : arrayList){
            modal = modal + (Integer.parseInt(t.getModal())*Integer.parseInt(t.getJumlah()));
            jual = jual + (Integer.parseInt(t.getJual())*Integer.parseInt(t.getJumlah()));
            jum = jum + Integer.parseInt(t.getJumlah());
        }

        for (Transaksi t : arrayList){
            System.out.println("jual : "+t.getJual());
            barangList.add(new Barang(t.getId(),t.getNama(),t.getModal(),t.getJumlah(),t.getJual()));
        }

        int disc = (Integer.valueOf(diskon)/100)*jual;
        int tax = (Integer.valueOf(pajak)/100)*jual;
        jual = jual+tax-disc;
        diskon = String.valueOf(disc);
        pajak = String.valueOf(tax);

        final DecimalFormat decim = new DecimalFormat("#,###.##");


        edtJatuhTempo = (EditText)findViewById(R.id.edt_jatuh_tempo);
        btnLanjut = (ImageButton) toolbar.findViewById(R.id.btn_lanjut_bayar);
        btnPesanan = (ImageButton) toolbar.findViewById(R.id.btn_add_pesanan);
        txTotal = (TextView) toolbar.findViewById(R.id.tx_total_bayar);
        txNama = (TextView) findViewById(R.id.tx_nama_piutang);
        checkPiutang = (CheckBox) findViewById(R.id.check_piutang_trans);
        checkPiutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPiutang.isChecked()){
                    Intent intent = new Intent(InputBayarActivity.this, ListPelSupActivity.class);
                    intent.putExtra("tipe","3");
                    startActivityForResult(intent,1);
                }else{
                    txNama.setText("");
                }
            }
        });
        btnPesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPesanan();
            }
        });
        txTotal.setText("Rp. "+String.valueOf(jual));
        txBayar = (TextView) findViewById(R.id.tx_jumlah_bayar);
        btn0 = (TextView) findViewById(R.id.btn_0);
        btn1 = (TextView) findViewById(R.id.btn_1);
        btn2 = (TextView) findViewById(R.id.btn_2);
        btn3 = (TextView) findViewById(R.id.btn_3);
        btn4 = (TextView) findViewById(R.id.btn_4);
        btn5 = (TextView) findViewById(R.id.btn_5);
        btn6 = (TextView) findViewById(R.id.btn_6);
        btn7 = (TextView) findViewById(R.id.btn_7);
        btn8 = (TextView) findViewById(R.id.btn_8);
        btn9 = (TextView) findViewById(R.id.btn_9);
        btnC = (TextView) findViewById(R.id.btn_c);
        btnDot = (TextView) findViewById(R.id.btn_dot);
        temp = "0";
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "0")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "1")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "2")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "3")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "4")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "5")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "6")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "7")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "8")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                if (temp.length()<11) {
                    temp = temp.replaceAll("[\\u0020|A-Z|a-z|.]", "");
                    //txBayar.setText(temp+"0");
                    txBayar.setText("Rp "+decim.format(Integer.valueOf(temp + "9")).replace(",", "."));
                }else{
                    txBayar.setText("Rp ");
                    temp = "0";
                }
            }
        });
        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = txBayar.getText().toString();
                txBayar.setText("Rp "+temp+".");
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txBayar.setText("Rp ");
            }
        });
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bayar = txBayar.getText().toString().replaceAll("[\\u0020|A-Z|a-z|.]","");
                if (bayar.equals("")){
                    bayar = "0";
                }
                if ((jual<=Integer.parseInt(bayar))||checkPiutang.isChecked()) {
                    if (pesanan.equals("")) {
                        dialog.show();
                        addTransaksi();
                    }else{
                        deletePesanan();
                    }
                }else{
                    Toast.makeText(InputBayarActivity.this,"Jumlah bayar kurang",Toast.LENGTH_SHORT).show();
                }
            }
        });
        edtJatuhTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void addTransaksi(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        String isCash = "1";
        if (checkPiutang.isChecked()) {
            isCash = "0";
        }
        Pembelian pembelian = new Pembelian(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                String.valueOf(modal),String.valueOf(jual),String.valueOf(jum),"1",isCash, "" ,id_pelsup,edtJatuhTempo.getText().toString(),
                ((User)tinyDB.getObject("user_login", User.class)).getNama(),barangList);
        Call<InsertResponse> call = api.addTransaksi(pembelian);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(InputBayarActivity.this,"Transaksi berhasil",Toast.LENGTH_SHORT).show();
                    id_transaksi = String.valueOf(response.body().getId_transaksi());
                    //showStrukDialog();
                    Intent intent = new Intent(InputBayarActivity.this, BerhasilBayarActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("list", arrayList);
                    intent.putExtras(bundle);
                    intent.putExtra("id_transaksi",id_transaksi);
                    intent.putExtra("jual",String.valueOf(jual).replaceAll("[\\u0020|A-Z|a-z|.]",""));
                    //intent.putExtra("bayar",txBayar.getText().toString().replaceAll("[\\u0020|A-Z|a-z|.]",""));
                    intent.putExtra("bayar",bayar);
                    intent.putExtra("pajak", pajak);
                    intent.putExtra("diskon", diskon);
                    dialog.dismiss();
                    startActivity(intent);
                }else{
                    dialog.dismiss();
                    System.out.println("gagal : "+response.body().getStatus());
                    Toast.makeText(InputBayarActivity.this,"Transaksi gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(InputBayarActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addPesanan(String nama_pesanan, String nama_pemesan, String meja, String orang, String ket){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        String isCash = "1";
        Pemesanan pemesanan = new Pemesanan(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                String.valueOf(modal),String.valueOf(jual),String.valueOf(jum),"1",isCash, "" , nama_pesanan,
                nama_pemesan, meja, orang, ket,barangList);
        Call<InsertResponse> call = api.addPesanan(pemesanan);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(InputBayarActivity.this,"Pesanan berhasil",Toast.LENGTH_SHORT).show();
                    id_transaksi = String.valueOf(response.body().getId_transaksi());
                    bayar = txBayar.getText().toString().replaceAll("[\\u0020|A-Z|a-z]","");
                    if (bayar.equals("")){
                        bayar = "0";
                    }
                    Intent intent = new Intent(InputBayarActivity.this, BerhasilBayarActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("list", arrayList);
                    intent.putExtras(bundle);
                    intent.putExtra("id_transaksi",id_transaksi);
                    intent.putExtra("jual",String.valueOf(jual).replaceAll("[\\u0020|A-Z|a-z|.]",""));
                    //intent.putExtra("bayar",txBayar.getText().toString().replaceAll("[\\u0020|A-Z|a-z|.]",""));
                    intent.putExtra("bayar",bayar);
                    intent.putExtra("pajak", pajak);
                    intent.putExtra("diskon", diskon);
                    intent.putExtra("pesanan", "1");
                    dialog.dismiss();
                    startActivity(intent);
                }else{
                    System.out.println("gagal : "+response.body().getStatus());
                    Toast.makeText(InputBayarActivity.this,"Pesanan gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(InputBayarActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deletePesanan(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        String isCash = "1";
        Call<InsertResponse> call = api.deletePesanan(pesanan);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(InputBayarActivity.this,"Transaksi berhasil",Toast.LENGTH_SHORT).show();
                    id_transaksi = pesanan;
                    //showStrukDialog();
                    Intent intent = new Intent(InputBayarActivity.this, BerhasilBayarActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("list", arrayList);
                    intent.putExtras(bundle);
                    intent.putExtra("id_transaksi",id_transaksi);
                    intent.putExtra("jual",String.valueOf(jual).replaceAll("[\\u0020|A-Z|a-z|.]",""));
                    intent.putExtra("bayar",txBayar.getText().toString().replaceAll("[\\u0020|A-Z|a-z|.]",""));
                    startActivity(intent);
                }else{
                    System.out.println("gagal : "+response.body().getStatus());
                    Toast.makeText(InputBayarActivity.this,"Transaksi gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(InputBayarActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showStrukDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Intent intent = new Intent(InputBayarActivity.this, PrintStruk2Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("list", arrayList);
                        intent.putExtras(bundle);
                        intent.putExtra("id_transaksi",id_transaksi);
                        intent.putExtra("jual",String.valueOf(jual).replaceAll("[\\u0020|A-Z|a-z|.]",""));
                        intent.putExtra("bayar",txBayar.getText().toString().replaceAll("[\\u0020|A-Z|a-z|.]",""));
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(InputBayarActivity.this);
        builder.setMessage("Print struk ?").setPositiveButton("Ya", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener).show();
    }

    public void showDialogPesanan(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(InputBayarActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pesanan, null);
        dialogBuilder.setView(dialogView);

        TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_pay_title);
        final EditText edtNama = (EditText) dialogView.findViewById(R.id.edt_nama_pesanan);
        final EditText edtPemesan = (EditText) dialogView.findViewById(R.id.edt_pemesan_pesanan);
        final EditText edtMeja = (EditText) dialogView.findViewById(R.id.edt_meja_pesanan);
        final EditText edtOrang = (EditText) dialogView.findViewById(R.id.edt_orang_pesanan);
        final EditText edtKeterangan = (EditText) dialogView.findViewById(R.id.edt_ket_pesanan);

        dialogBuilder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isEmpty(edtNama)||isEmpty(edtPemesan)||isEmpty(edtMeja)||isEmpty(edtOrang)){
                    Toast.makeText(InputBayarActivity.this,"Harap isi semua kolom",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(InputBayarActivity.this,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                    addPesanan(edtNama.getText().toString(), edtPemesan.getText().toString(),
                            edtMeja.getText().toString(), edtOrang.getText().toString(),
                            edtKeterangan.getText().toString());
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

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                String id_pelsup=data.getStringExtra("id_pelsup");
                String nama=data.getStringExtra("nama");
                txNama.setText("Pelanggan : "+nama);
                this.id_pelsup = id_pelsup;
                Toast.makeText(InputBayarActivity.this,"Pelanggan : "+result,Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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
