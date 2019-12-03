package com.example.reporttdm.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.KasAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.AkunKas;
import com.example.reporttdm.model.GetAkunKasResponse;
import com.example.reporttdm.model.GetKasResponse;
import com.example.reporttdm.model.ResultKas;
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArusKasActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private KasAdapter kasAdapter;
    private Toolbar toolbar;
    private ArrayList<ResultKas> resultKasList = new ArrayList<ResultKas>();
    private TinyDB tinyDB;
    private SwipeRefreshLayout swipeContainer;
    private Spinner spinAkunKas;
    //private String[] arrayAkunKas = {"Semua","Bank","Hutang","Piutang"};
    private ArrayList<String> arrayAkunKas = new ArrayList<>();
    private HashMap<String, String> hashAkunKas = new HashMap<String, String>();
    private int masuk, keluar, saldo = 0;
    private TextView txMasuk, txKeluar, txSaldo;
    private ArrayAdapter<String> adapter;
    private ArrayList<ResultKas> temp = new ArrayList<>();
    private String timeStamp = "";
    private Toko toko;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arus_kas);

        tinyDB = new TinyDB(this);
        toko = ((Toko) tinyDB.getObject("toko_login", Toko.class));

        Date date = new Date();
        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Arus Kas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinAkunKas = findViewById(R.id.spin_arus_kas);
        spinAkunKas.setOnItemSelectedListener(this);

        txMasuk = findViewById(R.id.tx_kas_masuk);
        txKeluar = findViewById(R.id.tx_kas_keluar);
        txSaldo = findViewById(R.id.tx_kas_saldo);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerKas);
        kasAdapter = new KasAdapter(resultKasList, ArusKasActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ArusKasActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(kasAdapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeKas);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListKas();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(true);

        adapter = new ArrayAdapter<String>(ArusKasActivity.this, android.R.layout.simple_spinner_item, arrayAkunKas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAkunKas.setAdapter(adapter);

        getListAkunKas();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.print_pdf:
                try {
                    Toast.makeText(ArusKasActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                    printToPDF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.print_excel:
                Toast.makeText(ArusKasActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                try {
                    printExcel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case R.id.sort_hari:
                filterHari();
                return true;
            case R.id.sort_bulan:
                filterBulan();
                return true;
            case R.id.sort_tahun:
                filterTahun();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void printToPDF() throws FileNotFoundException, DocumentException {
        try {
            Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 10f);

            File pdfFolder = new File(Environment.getExternalStorageDirectory() + "/Kasir");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("LOG", "Pdf Directory created");
            }

            File myFile = new File(pdfFolder + "/ArusKas" + timeStamp + ".pdf");

            //PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory()+"/tes.pdf"));
            PdfWriter.getInstance(document, new FileOutputStream(myFile));

            document.open();

            Font bold = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph(toko.getNama(), bold));
            document.add(new Paragraph("Alamat : " + toko.getLokasi()));
            document.add(new Paragraph("No. HP/WA : " + toko.getTelephone()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Laporan Arus Kas"));
            document.add(new Paragraph("Tanggal : " + timeStamp));
            PdfPTable table;
            table = new PdfPTable(6);
            table.setWidths(new float[]{1, 2, 3, 2, 2, 2});

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("No.", bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Tanggal", bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Keterangan", bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Masuk", bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Keluar", bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Saldo", bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            int i = 1;
            saldo = 0;
            for (ResultKas ts : temp) {
                table.addCell(String.valueOf(i));
                table.addCell(ts.getTanggal());
                table.addCell(ts.getKeterangan());
                if (ts.getJenis().equals("1")){
                    cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(ts.getNominal()))));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase("0"));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    saldo = saldo + Integer.parseInt(ts.getNominal());
                }else if (ts.getJenis().equals("0")){
                    cell = new PdfPCell(new Phrase("0"));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(ts.getNominal()))));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    saldo = saldo - Integer.parseInt(ts.getNominal());
                }
                cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(String.valueOf(saldo)))));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                i++;
            }
            table.setWidthPercentage(100);
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);
            Toast.makeText(ArusKasActivity.this, "PDF Berhasil disimpan di folder Kasir", Toast.LENGTH_LONG).show();

            document.close();
        } catch (NullPointerException e) {
            Toast.makeText(ArusKasActivity.this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void printExcel() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Laporan Arus Kas");

        int rowNum = 0;
        System.out.println("Creating excel");
        Cell cell;
        int laba = 0;

        Row row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) toko.getNama());
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Alamat : "+toko.getLokasi());
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "No. HP/WA : "+toko.getTelephone());
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) " ");
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Laporan Transaksi");
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Tanggal : "+timeStamp);

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "No.");
        cell = row.createCell(1);
        cell.setCellValue((String) "Tanggal");
        cell = row.createCell(2);
        cell.setCellValue((String) "Keterangan");
        cell = row.createCell(3);
        cell.setCellValue((String) "Masuk");
        cell = row.createCell(4);
        cell.setCellValue((String) "Keluar");
        cell = row.createCell(5);
        cell.setCellValue((String) "Saldo");

        int index = 1;
        saldo = 0;
        for (ResultKas resultKas : temp) {
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue((String) String.valueOf(index));
            cell = row.createCell(1);
            cell.setCellValue((String) resultKas.getTanggal());
            cell = row.createCell(2);
            cell.setCellValue((String) resultKas.getKeterangan());
            if (resultKas.getJenis().equals("1")){
                cell = row.createCell(3);
                cell.setCellValue((String)String.valueOf(doubleToStringNoDecimal(resultKas.getNominal())));
                cell = row.createCell(4);
                cell.setCellValue((String)"0");
                saldo = saldo + Integer.parseInt(resultKas.getNominal());
            }else if(resultKas.getJenis().equals("0")){
                cell = row.createCell(3);
                cell.setCellValue((String)"0");
                cell = row.createCell(4);
                cell.setCellValue((String)String.valueOf(doubleToStringNoDecimal(resultKas.getNominal())));
                saldo = saldo - Integer.parseInt(resultKas.getNominal());
            }
            cell = row.createCell(5);
            cell.setCellValue((String) doubleToStringNoDecimal(String.valueOf(saldo)));
            index++;
        }

        try {
            File pdfFolder = new File(Environment.getExternalStorageDirectory()+"/Kasir");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("LOG", "Pdf Directory created");
            }

            Date date = new Date() ;
            String timeStamp;
            timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

            File myFile = new File(pdfFolder+ "/ArusKas"+ timeStamp + ".xlsx");
            FileOutputStream outputStream = new FileOutputStream(myFile);
            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(ArusKasActivity.this,"Excel Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filterHari() {
        temp = new ArrayList<ResultKas>();
        for (ResultKas p : resultKasList) {
            if (p.getTanggal().substring(0, 2).equals(timeStamp.substring(0, 2))) {
                temp.add(p);
            }
        }
        kasAdapter.updateList(temp);
        calculateBalance(temp);
    }

    public void filterBulan() {
        temp = new ArrayList<ResultKas>();
        for (ResultKas p : resultKasList) {
            if (p.getTanggal().substring(3, 5).equals(timeStamp.substring(3, 5))) {
                temp.add(p);
            }
        }
        kasAdapter.updateList(temp);
        calculateBalance(temp);
    }

    public void filterTahun() {
        temp = new ArrayList<ResultKas>();
        for (ResultKas p : resultKasList) {
            if (p.getTanggal().substring(6, 10).equals(timeStamp.substring(6, 10))) {
                temp.add(p);
            }
        }
        kasAdapter.updateList(temp);
        calculateBalance(temp);
    }

    public void getListAkunKas() {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetAkunKasResponse> call = api.showAkunKas(((User) tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetAkunKasResponse>() {
            @Override
            public void onResponse(Call<GetAkunKasResponse> call, Response<GetAkunKasResponse> response) {
                if (response.body().getStatusCode().equals("1")) {
                    arrayAkunKas.clear();
                    hashAkunKas.clear();
                    arrayAkunKas.add("Semua");
                    hashAkunKas.put("0", "Semua");
                    for (AkunKas ak : response.body().getResultAkunKas()) {
                        hashAkunKas.put(ak.getId(), ak.getNama());
                        arrayAkunKas.add(ak.getNama());
                    }
                    adapter.notifyDataSetChanged();
                    getListKas();
                } else {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ArusKasActivity.this, "Tidak ada akun kas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetAkunKasResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ArusKasActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getListKas() {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetKasResponse> call = api.showKas(((User) tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetKasResponse>() {
            @Override
            public void onResponse(Call<GetKasResponse> call, Response<GetKasResponse> response) {
                System.out.println("re : " + response.body().getStatusCode());
                if (response.body().getStatusCode().equals("1")) {
                    resultKasList.clear();
                    resultKasList.addAll(response.body().getResultKas());
                    temp = resultKasList;
                    kasAdapter.updateList(resultKasList);
                    calculateBalance(resultKasList);
                    swipeContainer.setRefreshing(false);
                } else {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ArusKasActivity.this, "Tidak ada arus kas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetKasResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ArusKasActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        filter(arrayAkunKas.get(i));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void calculateBalance(List<ResultKas> calculateList) {
        keluar = 0;
        masuk = 0;
        saldo = 0;
        for (ResultKas rk : calculateList) {
            if (rk.getJenis().equals("0")) {
                keluar = keluar + Integer.parseInt(rk.getNominal());
            } else if (rk.getJenis().equals("1")) {
                masuk = masuk + Integer.parseInt(rk.getNominal());
            }
        }
        saldo = masuk - keluar;
        txMasuk.setTextColor(Color.BLACK);
        txMasuk.setText("Rp " + doubleToStringNoDecimal(String.valueOf(masuk)));
        txKeluar.setTextColor(Color.RED);
        txKeluar.setText("Rp " + doubleToStringNoDecimal(String.valueOf(keluar)));
        txSaldo.setTextColor(Color.BLACK);
        txSaldo.setText("Rp " + doubleToStringNoDecimal(String.valueOf(saldo)));
    }

    public static String doubleToStringNoDecimal(String value) {
        double d = Double.parseDouble(value);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");
        return formatter.format(d).replace(",", ".");
    }

    public void filter(String text) {
        temp = new ArrayList<ResultKas>();
        if (!text.equals("Semua")) {
            for (ResultKas rk : resultKasList) {
                if (rk.getNama().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(rk);
                }
            }
        } else {
            temp = resultKasList;
        }
        kasAdapter.updateList(temp);
        calculateBalance(temp);
    }
}
