package com.example.reporttdm.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.fragment.DatePickerFragment;
import com.example.reporttdm.helper.NumberTextWatcherForThousand;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.AkunKas;
import com.example.reporttdm.model.GetAkunKasResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_SHORT;

public class MutasiKasActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private TinyDB tinyDB;
    private EditText edtTanggal, edtWaktu, edtNominal, edtKeterangan;
    private Button btnSimpan;
    private Spinner spinDariKas;
    private Spinner spinKeKas;
    private ArrayList<String> arrayAkunKas = new ArrayList<>();
    private HashMap<String,String> hashAkunKas = new HashMap<String,String>();
    private String dari = "0";
    private String ke = "0";
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    private ProgressDialog progressDialog;
    private String timeStamp = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutasi_kas);

        tinyDB = new TinyDB(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Harap tunggu...");

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Mutasi Kas");

        spinDariKas = findViewById(R.id.spin_dari_kas);
        spinDariKas.setOnItemSelectedListener(this);
        adapter =new ArrayAdapter<String>(MutasiKasActivity.this,android.R.layout.simple_spinner_item, arrayAkunKas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDariKas.setAdapter(adapter);

        spinKeKas = findViewById(R.id.spin_ke_kas);
        spinKeKas.setOnItemSelectedListener(this);
        adapter2 =new ArrayAdapter<String>(MutasiKasActivity.this,android.R.layout.simple_spinner_item, arrayAkunKas);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinKeKas.setAdapter(adapter2);

        edtNominal = findViewById(R.id.edt_nominal_kas);
        edtNominal.addTextChangedListener(new NumberTextWatcherForThousand(edtNominal));

        edtTanggal = findViewById(R.id.edt_tanggal_kas);
        edtTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTanggalDialog();
            }
        });

        edtWaktu = findViewById(R.id.edt_waktu_kas);
        edtWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaktuDialog();
            }
        });
        edtKeterangan = findViewById(R.id.edt_ket_kas);

        btnSimpan = findViewById(R.id.btn_simpan_kas);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinDariKas.getSelectedItemPosition() == 0){
                    Toast.makeText(MutasiKasActivity.this,
                            "Harap pilih akun kas dari", Toast.LENGTH_SHORT).show();
                }else if(spinKeKas.getSelectedItemPosition() == 0){
                    Toast.makeText(MutasiKasActivity.this,
                            "Harap pilih akun kas ke", Toast.LENGTH_SHORT).show();
                }else if(isEmpty(edtKeterangan)||isEmpty(edtNominal)||isEmpty(edtTanggal)||
                        isEmpty(edtWaktu)){
                    Toast.makeText(MutasiKasActivity.this,
                            "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
                }else {
                    addKas();
                }
            }
        });

        Date date = new Date() ;
        timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(date);
        edtTanggal.setText(timeStamp);
        timeStamp = new SimpleDateFormat("HH:mm").format(date);
        edtWaktu.setText(timeStamp);

        getListAkunKas();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.spin_dari_kas){
            /*Toast.makeText(MutasiKasActivity.this,
                    "Dari akun "+arrayAkunKas[i],Toast.LENGTH_SHORT).show();*/
            dari = hashAkunKas.get(arrayAkunKas.get(i));
        }else{
            /*Toast.makeText(MutasiKasActivity.this,
                    "Ke akun "+arrayAkunKas[i],Toast.LENGTH_SHORT).show();*/
            ke = hashAkunKas.get(arrayAkunKas.get(i));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void getListAkunKas(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetAkunKasResponse> call = api.showAkunKas(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetAkunKasResponse>() {
            @Override
            public void onResponse(Call<GetAkunKasResponse> call, Response<GetAkunKasResponse> response) {
                if (response.body().getStatusCode().equals("1")) {
                    arrayAkunKas.clear();
                    hashAkunKas.clear();
                    arrayAkunKas.add("Pilih Akun");
                    hashAkunKas.put("0","Pilih Akun");
                    for (AkunKas ak : response.body().getResultAkunKas()){
                        hashAkunKas.put(ak.getNama(),ak.getId());
                        arrayAkunKas.add(ak.getNama());
                    }
                    adapter.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                }else{
                    Toast.makeText(MutasiKasActivity.this,"Tidak ada akun kas",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetAkunKasResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(MutasiKasActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addKas(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.mutasiKas(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                edtNominal.getText().toString().replace(",",""),
                edtTanggal.getText().toString()+" "+edtWaktu.getText().toString()+":00", ke, dari);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(MutasiKasActivity.this,"Mutasi kas berhasil", LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MutasiKasActivity.this,"Mutasi kas gagal", LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    progressDialog.dismiss();
                    Toast.makeText(MutasiKasActivity.this, "Harap periksa koneksi internet", LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showTanggalDialog() {
        DatePickerFragment date = new DatePickerFragment();
        final Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtTanggal.setText(String.valueOf(year) + "-" + String.format("%02d", (month + 1))
                        + "-" + String.format("%02d",dayOfMonth));
            }
        };
        date.setCallBack(dateSetListener);
        try{
            date.show(getSupportFragmentManager(),"Tanggal");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void showWaktuDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtWaktu.setText( String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Waktu");
        mTimePicker.show();
    }
}
