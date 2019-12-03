package com.example.reporttdm.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.LaporanPembelianAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.GetPembelianResponse;
import com.example.reporttdm.model.ResultPembelianItem;
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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LaporanPembelianAdapter laporanPembelianAdapter;
    ArrayList<ResultPembelianItem> resultPembelianItems = new ArrayList<ResultPembelianItem>();
    Toolbar toolbar;
    FloatingActionButton fab;
    TinyDB tinyDB;
    String tipe = "";
    private SwipeRefreshLayout swipeContainer;
    String timeCurrent;
    private Toko toko;
    private String timeStamp;
    private ArrayList<ResultPembelianItem> temp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        tinyDB = new TinyDB(this);

        toko = ((Toko)tinyDB.getObject("toko_login", Toko.class));

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (getIntent().hasExtra("tipe")){
            if (getIntent().getStringExtra("tipe").equals("penjualan")){
                setTitle("Laporan Penjualan");
                tipe = "Penjualan";
            }else if(getIntent().getStringExtra("tipe").equals("pembelian")){
                tipe = "Pembelian";
            }
        }

        Date date = new Date() ;
        timeCurrent = new SimpleDateFormat("dd").format(date);
        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerLaporan);
        laporanPembelianAdapter = new LaporanPembelianAdapter(resultPembelianItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LaporanActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(laporanPembelianAdapter);

        System.out.println("list : "+ resultPembelianItems);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeLaporan);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLaporanPenjualan();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fab = (FloatingActionButton) findViewById(R.id.fab_listlaporan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        swipeContainer.setRefreshing(true);
        if (tipe.equals("Penjualan")) {
            getLaporanPenjualan();
        }else if (tipe.equals("Pembelian")){
            getLaporanPembelian();
        }

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
                    Toast.makeText(LaporanActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                    if (tipe.equals("Penjualan")) {
                        printToPDF();
                    }else if (tipe.equals("Pembelian")){
                        printToPDF2();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.print_excel:
                Toast.makeText(LaporanActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                try {
                    if (tipe.equals("Penjualan")) {
                        printExcel();
                    }else if (tipe.equals("Pembelian")){
                        printExcel2();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
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

    public void getLaporanPenjualan(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetPembelianResponse> call = api.showPenjualan(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetPembelianResponse>() {
            @Override
            public void onResponse(Call<GetPembelianResponse> call, Response<GetPembelianResponse> response) {
                System.out.println("statuscode : "+response.body().getStatusCode());
                if (response.body().getStatusCode().equals("1")) {
                    resultPembelianItems.clear();
                    for (ResultPembelianItem r : response.body().getResult_pembelian()){
                        resultPembelianItems.add(r);
                    }
                    temp = resultPembelianItems;
                    //historyTransaksis.addAll(response.body().getResult_pembelian());
                    laporanPembelianAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(LaporanActivity.this,"Belum ada penjualan",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetPembelianResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(LaporanActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getLaporanPembelian(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetPembelianResponse> call = api.showPembelian(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetPembelianResponse>() {
            @Override
            public void onResponse(Call<GetPembelianResponse> call, Response<GetPembelianResponse> response) {
                System.out.println("statuscode : "+response.body().getStatusCode());
                if (response.body().getStatusCode().equals("1")) {
                    resultPembelianItems.clear();
                    for (ResultPembelianItem r : response.body().getResult_pembelian()){
                        resultPembelianItems.add(r);
                    }
                    //historyTransaksis.addAll(response.body().getResult_pembelian());
                    laporanPembelianAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(LaporanActivity.this,"Belum ada penjualan",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetPembelianResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(LaporanActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void printToPDF() throws FileNotFoundException, DocumentException {

        try {
            String kota,nama;
            Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 10f);

            File pdfFolder = new File(Environment.getExternalStorageDirectory()+"/Kasir");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("LOG", "Pdf Directory created");
            }

            File myFile = new File(pdfFolder+ "/" + tipe+ timeStamp + ".pdf");

            //PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory()+"/tes.pdf"));
            PdfWriter.getInstance(document, new FileOutputStream(myFile));

            document.open();

            Font bold = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph(toko.getNama(),bold));
            document.add(new Paragraph("Alamat : "+toko.getLokasi()));
            document.add(new Paragraph("No. HP/WA : "+toko.getTelephone()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Laporan Laba "+tipe));
            document.add(new Paragraph("Tanggal : "+timeStamp));
            PdfPTable table;
            table = new PdfPTable(8);
            table.setWidths(new float[] { 1, 2, 2, 3, 1, 2, 3, 3 });

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("No.",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Tanggal",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Kode Barang",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Nama Barang",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("Satuan",bold));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Qty",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Harga Jual",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Harga Beli",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Laba",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            int i = 1;
            int laba = 0;
            int total = 0;
            for (ResultPembelianItem ts : temp){
                if(true) {
                    table.addCell(String.valueOf(i));
                    table.addCell(ts.getTanggal());
                    table.addCell(ts.getKode_barang());
                    table.addCell(ts.getNama_barang());
                    table.addCell(ts.getJumlah());
                    cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(Double.parseDouble(ts.getHarga_jual())))));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(Double.parseDouble(ts.getHarga_dasar())))));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    laba = (Integer.parseInt(ts.getJumlah()) *
                            Integer.parseInt(ts.getHarga_jual())) -
                            (Integer.parseInt(ts.getJumlah()) *
                                    Integer.parseInt(ts.getHarga_dasar()));
                    cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(laba))));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    total = total + laba;
                    i++;
                }
            }
            cell = new PdfPCell(new Phrase("Total",bold));
            cell.setColspan(7);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(total)),bold));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            table.setWidthPercentage(100);
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);
            Toast.makeText(LaporanActivity.this,"PDF Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

            document.close();
        }catch (NullPointerException e){
            Toast.makeText(LaporanActivity.this,"Data user tidak ditemukan",Toast.LENGTH_SHORT).show();
        }
    }

    public void printToPDF2() throws FileNotFoundException, DocumentException {

        try {
            String kota,nama;
            Toko toko = ((Toko)tinyDB.getObject("toko_login", Toko.class));
            Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 0f);

            File pdfFolder = new File(Environment.getExternalStorageDirectory()+"/Kasir");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("LOG", "Pdf Directory created");
            }

            Date date = new Date() ;
            String timeStamp;
            timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

            File myFile = new File(pdfFolder+ "/" + tipe+ timeStamp + ".pdf");

            //PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory()+"/tes.pdf"));
            PdfWriter.getInstance(document, new FileOutputStream(myFile));

            document.open();

            Font bold = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph(toko.getNama(),bold));
            document.add(new Paragraph("Alamat : "+toko.getLokasi()));
            document.add(new Paragraph("No. HP/WA : "+toko.getTelephone()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Laporan Laba "+tipe));
            document.add(new Paragraph("Tanggal : "+timeStamp));
            PdfPTable table;
            table = new PdfPTable(6);
            table.setWidths(new float[] { 1, 3, 2, 1, 3, 3 });

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("No.",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Nama Barang",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Tgl. Beli",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Qty",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Harga",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Jumlah",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            int i = 1;
            int laba = 0;
            int total = 0;
            for (ResultPembelianItem ts : temp){
                if(true) {
                    table.addCell(String.valueOf(i));
                    table.addCell(ts.getNama_barang());
                    table.addCell(ts.getTanggal());
                    table.addCell(ts.getJumlah());
                    cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(Double.parseDouble(ts.getHarga_dasar())))));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(Double.parseDouble(ts.getModal())))));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);
                    total = total + Integer.parseInt(ts.getModal());
                    i++;
                }
            }
            cell = new PdfPCell(new Phrase("Total",bold));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(total)),bold));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            table.setWidthPercentage(100);
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);
            Toast.makeText(LaporanActivity.this,"PDF Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

            document.close();
        }catch (NullPointerException e){
            Toast.makeText(LaporanActivity.this,"Data user tidak ditemukan",Toast.LENGTH_SHORT).show();
        }
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

    private void printExcel(){
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Laporan "+tipe);

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
        cell.setCellValue((String) "Laporan Laba "+tipe);
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Tanggal : "+timeStamp);

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "No.");
        cell = row.createCell(1);
        cell.setCellValue((String) "Tanggal");
        cell = row.createCell(2);
        cell.setCellValue((String) "Kode Barang");
        cell = row.createCell(3);
        cell.setCellValue((String) "Nama Barang");
        cell = row.createCell(4);
        cell.setCellValue((String) "Qty");
        cell = row.createCell(5);
        cell.setCellValue((String) "Harga Jual");
        cell = row.createCell(6);
        cell.setCellValue((String) "Harga Beli");
        cell = row.createCell(7);
        cell.setCellValue((String) "Laba");

        int total = 0;
        for (ResultPembelianItem pembelianItem : temp) {
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue((String) String.valueOf(rowNum));
            cell = row.createCell(1);
            cell.setCellValue((String) pembelianItem.getTanggal());
            cell = row.createCell(2);
            cell.setCellValue((String) pembelianItem.getKode_barang());
            cell = row.createCell(3);
            cell.setCellValue((String) pembelianItem.getNama_barang());
            cell = row.createCell(4);
            cell.setCellValue((String) pembelianItem.getJumlah());
            cell = row.createCell(5);
            cell.setCellValue((String) String.valueOf(doubleToStringNoDecimal(Double.parseDouble(pembelianItem.getHarga_jual()))));
            cell = row.createCell(6);
            cell.setCellValue((String) String.valueOf(doubleToStringNoDecimal(Double.parseDouble(pembelianItem.getHarga_dasar()))));
            laba = (Integer.parseInt(pembelianItem.getJumlah()) *
                    Integer.parseInt(pembelianItem.getHarga_jual())) -
                    (Integer.parseInt(pembelianItem.getJumlah()) *
                            Integer.parseInt(pembelianItem.getHarga_dasar()));
            cell = row.createCell(7);
            cell.setCellValue((String) String.valueOf(doubleToStringNoDecimal(laba)));
            total = total + laba;
        }

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Total");
        cell = row.createCell(7);
        cell.setCellValue((String) doubleToStringNoDecimal(total));

        try {
            File pdfFolder = new File(Environment.getExternalStorageDirectory()+"/Kasir");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("LOG", "Pdf Directory created");
            }

            Date date = new Date() ;
            String timeStamp;
            timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

            File myFile = new File(pdfFolder+ "/" + tipe+ timeStamp + ".xlsx");
            FileOutputStream outputStream = new FileOutputStream(myFile);
            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(LaporanActivity.this,"Excel Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printExcel2(){
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Laporan "+tipe);

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
        cell.setCellValue((String) "Laporan Laba "+tipe);
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Tanggal : "+timeStamp);

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "No.");
        cell = row.createCell(1);
        cell.setCellValue((String) "Nama Barang");
        cell = row.createCell(2);
        cell.setCellValue((String) "Tgl. Beli");
        cell = row.createCell(3);
        cell.setCellValue((String) "Qty");
        cell = row.createCell(4);
        cell.setCellValue((String) "Harga");
        cell = row.createCell(5);
        cell.setCellValue((String) "Jumlah");

        int total = 0;
        for (ResultPembelianItem pembelianItem : temp) {
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue((String) String.valueOf(rowNum));
            cell = row.createCell(1);
            cell.setCellValue((String) pembelianItem.getNama_barang());
            cell = row.createCell(2);
            cell.setCellValue((String) pembelianItem.getTanggal());
            cell = row.createCell(3);
            cell.setCellValue((String) pembelianItem.getJumlah());
            cell = row.createCell(4);
            cell.setCellValue((String) String.valueOf(doubleToStringNoDecimal(Double.parseDouble(pembelianItem.getHarga_dasar()))));
            cell = row.createCell(5);
            cell.setCellValue((String) String.valueOf(doubleToStringNoDecimal(Double.parseDouble(pembelianItem.getModal()))));
            total = total + Integer.parseInt(pembelianItem.getModal());
        }

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Total");
        cell = row.createCell(5);
        cell.setCellValue((String) doubleToStringNoDecimal(total));

        try {
            File pdfFolder = new File(Environment.getExternalStorageDirectory()+"/Kasir");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("LOG", "Pdf Directory created");
            }

            Date date = new Date() ;
            String timeStamp;
            timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

            File myFile = new File(pdfFolder+ "/" + tipe+ timeStamp + ".xlsx");
            FileOutputStream outputStream = new FileOutputStream(myFile);
            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(LaporanActivity.this,"Excel Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filterHari() {
        temp = new ArrayList<ResultPembelianItem>();
        for (ResultPembelianItem p : resultPembelianItems){
            if (p.getTanggal().substring(8,10).equals(timeStamp.substring(0,2))){
                temp.add(p);
            }
        }
        laporanPembelianAdapter.updateList(temp);
    }

    public void filterBulan() {
        temp = new ArrayList<ResultPembelianItem>();
        for (ResultPembelianItem p : resultPembelianItems){
            if (p.getTanggal().substring(5,7).equals(timeStamp.substring(3,5))){
                temp.add(p);
            }
        }
        laporanPembelianAdapter.updateList(temp);
    }
    public void filterTahun() {
        temp = new ArrayList<ResultPembelianItem>();
        for (ResultPembelianItem p : resultPembelianItems){
            if (p.getTanggal().substring(0,4).equals(timeStamp.substring(6,10))){
                temp.add(p);
            }
        }
        laporanPembelianAdapter.updateList(temp);
    }
}
