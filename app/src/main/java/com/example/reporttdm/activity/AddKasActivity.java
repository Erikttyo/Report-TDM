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

public class AddKasActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private TinyDB tinyDB;
    private EditText edtTanggal, edtWaktu, edtNominal, edtKeterangan;
    private Button btnSimpan;
    private Spinner spinAkunKas;
    private ArrayList<String> arrayAkunKas = new ArrayList<>();
    private HashMap<String,String> hashAkunKas = new HashMap<String,String>();
//    private String[] arrayAkunKas = {"Pilih Akun","Bank","Hutang","Piutang"};
    private String jenis = "";
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private String timeStamp = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kas);

        tinyDB = new TinyDB(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Harap tunggu...");

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (getIntent().hasExtra("jenis")){
            jenis = getIntent().getStringExtra("jenis");
            setTitle(jenis.equals("1") ?
                    "Penambahan Kas" : "Pengurangan Kas");
        }

        btnSimpan = findViewById(R.id.btn_simpan_kas);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((spinAkunKas.getSelectedItemPosition() != 0) &&
                        (!isEmpty(edtTanggal)&&!isEmpty(edtNominal)&&!isEmpty(edtWaktu)&&!isEmpty(edtKeterangan))){
                    progressDialog.show();
                    addKas();
                }else {
                    Toast.makeText(AddKasActivity.this, "Harap isi semua kolom",LENGTH_SHORT).show();
                }
            }
        });

        spinAkunKas = findViewById(R.id.spin_arus_kas);
        spinAkunKas.setOnItemSelectedListener(this);
        adapter =new ArrayAdapter<String>(AddKasActivity.this,android.R.layout.simple_spinner_item, arrayAkunKas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAkunKas.setAdapter(adapter);

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

        Date date = new Date() ;
        timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(date);
        edtTanggal.setText(timeStamp);
        timeStamp = new SimpleDateFormat("HH:mm").format(date);
        edtWaktu.setText(timeStamp);

        getListAkunKas();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

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
                }else{
                    Toast.makeText(AddKasActivity.this,"Tidak ada akun kas",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetAkunKasResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(AddKasActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
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

    public void addKas(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.addKas(((User)tinyDB.getObject("user_login", User.class)).getId_toko(),
                hashAkunKas.get(arrayAkunKas.get(spinAkunKas.getSelectedItemPosition())), edtNominal.getText().toString().replace(",",""),
                edtTanggal.getText().toString()+" "+edtWaktu.getText().toString()+":00",edtKeterangan.getText().toString(), jenis);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(AddKasActivity.this,"Tambah kas berhasil", LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddKasActivity.this,"Tambah kas gagal", LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    progressDialog.dismiss();
                    Toast.makeText(AddKasActivity.this, "Harap periksa koneksi internet", LENGTH_SHORT).show();
                }
            }
        });
    }
}
