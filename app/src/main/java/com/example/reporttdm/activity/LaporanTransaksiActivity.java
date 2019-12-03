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
import com.example.reporttdm.adapter.LaporanTransaksiAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.GetTransaksiResponse;
import com.example.reporttdm.model.HistoryTransaksi;
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

public class LaporanTransaksiActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LaporanTransaksiAdapter laporanTransaksiAdapter;
    ArrayList<HistoryTransaksi> historyTransaksis = new ArrayList<HistoryTransaksi>();
    Toolbar toolbar;
    FloatingActionButton fab;
    TinyDB tinyDB;
    String tipe = "";
    private SwipeRefreshLayout swipeContainer;
    String timeCurrent;
    private Toko toko;
    private String timeStamp;
    private ArrayList<HistoryTransaksi> temp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_laporan_transaksi);

        tinyDB = new TinyDB(this);

        toko = ((Toko)tinyDB.getObject("toko_login", Toko.class));

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Laporan Transaksi");

        Date date = new Date() ;
        timeCurrent = new SimpleDateFormat("dd").format(date);
        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerLaporanTransaksi);
        laporanTransaksiAdapter = new LaporanTransaksiAdapter(historyTransaksis,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LaporanTransaksiActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(laporanTransaksiAdapter);

        System.out.println("list : "+ historyTransaksis);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeLaporanTransaksi);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLaporan();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fab = (FloatingActionButton) findViewById(R.id.fab_listlaporantransaksi);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        swipeContainer.setRefreshing(true);
        getLaporan();
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
                    Toast.makeText(LaporanTransaksiActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                    printToPDF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.print_excel:
                Toast.makeText(LaporanTransaksiActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                try {
                    printExcel();
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

    public void getLaporan(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetTransaksiResponse> call = api.showTransaksi(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetTransaksiResponse>() {
            @Override
            public void onResponse(Call<GetTransaksiResponse> call, Response<GetTransaksiResponse> response) {
                if (response.body().getStatus_code().equals("1")) {
                    historyTransaksis.clear();
                    historyTransaksis.addAll(response.body().getResult_transaksi());
                    temp = historyTransaksis;
                    laporanTransaksiAdapter.updateList(historyTransaksis);
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(LaporanTransaksiActivity.this,"Belum ada transaksi",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetTransaksiResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(LaporanTransaksiActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

    public void filterHari() {
        temp = new ArrayList<HistoryTransaksi>();
        for (HistoryTransaksi p : historyTransaksis){
            if (p.getTanggal().substring(8,10).equals(timeStamp.substring(0,2))){
                temp.add(p);
            }
        }
        laporanTransaksiAdapter.updateList(temp);
    }

    public void filterBulan() {
        temp = new ArrayList<HistoryTransaksi>();
        for (HistoryTransaksi p : historyTransaksis){
            if (p.getTanggal().substring(5,7).equals(timeStamp.substring(3,5))){
                temp.add(p);
            }
        }
        laporanTransaksiAdapter.updateList(temp);
    }
    public void filterTahun() {
        temp = new ArrayList<HistoryTransaksi>();
        for (HistoryTransaksi p : historyTransaksis){
            if (p.getTanggal().substring(0,4).equals(timeStamp.substring(6,10))){
                temp.add(p);
            }
        }
        laporanTransaksiAdapter.updateList(temp);
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

            File myFile = new File(pdfFolder+ "/Transaksi"+ timeStamp + ".pdf");

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
            document.add(new Paragraph("Laporan Transaksi"));
            document.add(new Paragraph("Tanggal : "+timeStamp));
            PdfPTable table;
            table = new PdfPTable(5);
            table.setWidths(new float[] { 1, 2, 3, 2, 2});

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("No.",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Tanggal",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Total",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Status",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Tipe",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            int i = 1;
            int laba = 0;
            int total = 0;
            for (HistoryTransaksi ts : temp){
                if(true) {
                    table.addCell(String.valueOf(i));
                    table.addCell(ts.getTanggal());
                    if (ts.getHutpit().equals("2")){
                        cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(Double.parseDouble(ts.getJual())))));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }else if(ts.getHutpit().equals("0")){
                        cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(Double.parseDouble(ts.getJual())))));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }else if (ts.getHutpit().equals("1")){
                        cell = new PdfPCell(new Phrase(String.valueOf(doubleToStringNoDecimal(Double.parseDouble(ts.getModal())))));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                    table.addCell(cell);
                    table.addCell(ts.getStatus().equals("1") ? "Lunas":"Belum Lunas");
                    table.addCell(ts.getTipe().equals("0") ? "Pembelian":"Penjualan");
                }
            }
            table.setWidthPercentage(100);
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);
            Toast.makeText(LaporanTransaksiActivity.this,"PDF Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

            document.close();
        }catch (NullPointerException e){
            Toast.makeText(LaporanTransaksiActivity.this,"Data user tidak ditemukan",Toast.LENGTH_SHORT).show();
        }
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
        cell.setCellValue((String) "Total");
        cell = row.createCell(3);
        cell.setCellValue((String) "Status");
        cell = row.createCell(4);
        cell.setCellValue((String) "Tipe");

        int index = 1;
        for (HistoryTransaksi historyTransaksi : temp) {
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue((String) String.valueOf(index));
            cell = row.createCell(1);
            cell.setCellValue((String) historyTransaksi.getTanggal());
            cell = row.createCell(2);
            if (historyTransaksi.getHutpit().equals("2")){
                cell.setCellValue((String)String.valueOf(doubleToStringNoDecimal(Double.parseDouble(historyTransaksi.getJual()))));
            }else if(historyTransaksi.getHutpit().equals("0")){
                cell.setCellValue((String)String.valueOf(doubleToStringNoDecimal(Double.parseDouble(historyTransaksi.getJual()))));
            }else if (historyTransaksi.getHutpit().equals("1")){
                cell.setCellValue((String)String.valueOf(doubleToStringNoDecimal(Double.parseDouble(historyTransaksi.getModal()))));
            }
            cell = row.createCell(3);
            cell.setCellValue((String) (historyTransaksi.getStatus().equals("1") ? "Lunas":"Belum Lunas"));
            cell = row.createCell(4);
            cell.setCellValue((String) (historyTransaksi.getTipe().equals("0") ? "Pembelian":"Penjualan"));
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

            File myFile = new File(pdfFolder+ "/Transaksi"+ timeStamp + ".xlsx");
            FileOutputStream outputStream = new FileOutputStream(myFile);
            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(LaporanTransaksiActivity.this,"Excel Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
