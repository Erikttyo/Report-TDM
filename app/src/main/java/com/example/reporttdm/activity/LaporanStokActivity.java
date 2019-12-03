package com.example.reporttdm.activity;

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
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.DBarangAdapter;
import com.example.reporttdm.helper.SortbyStokASC;
import com.example.reporttdm.helper.SortbyStokDESC;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.GetBarangResponse;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanStokActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DBarangAdapter dBarangAdapter;
    Toolbar toolbar;
    List<Barang> barangList = new ArrayList<Barang>();
    int pos;
    private String tipe = "";
    private TinyDB tinyDB;
    private SwipeRefreshLayout swipeContainer;
    private Toko toko;
    private String timeCurrent;
    private String timeStamp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_stok);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Laporan Stok");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tinyDB = new TinyDB(this);

        tipe = "Stock";
        toko = ((Toko)tinyDB.getObject("toko_login", Toko.class));
        Date date = new Date() ;
        timeCurrent = new SimpleDateFormat("dd").format(date);
        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerLaporanStok);
        dBarangAdapter = new DBarangAdapter(barangList,this,3);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LaporanStokActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dBarangAdapter);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeLaporanStok);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListBarang();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(true);
        getListBarang();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                    Toast.makeText(LaporanStokActivity.this,"Tidak ada barang",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetBarangResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(LaporanStokActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void printToPDF() throws FileNotFoundException, DocumentException {

        try {
            String kota,nama;
            Toko toko = ((Toko)tinyDB.getObject("toko_login", Toko.class));
            Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 10f);

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
            document.add(new Paragraph("Laporan Stock Barang"));
            document.add(new Paragraph("Tanggal : "+timeStamp));
            PdfPTable table;
            table = new PdfPTable(5);
            table.setWidths(new float[] { 1, 3, 4, 1, 1});

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("No.",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Kode Barang",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Nama Barang",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Satuan",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Stock",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            int i = 1;
            int laba = 0;
            for (Barang ts : barangList){
                if(true) {
                    table.addCell(String.valueOf(i));
                    table.addCell(ts.getKode());
                    table.addCell(ts.getNama());
                    table.addCell(ts.getSatuan());
                    table.addCell(ts.getStok());
                    i++;
                }
            }
            table.setWidthPercentage(100);
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(date);

            table = new PdfPTable(1);
            table.setWidthPercentage(80);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);
            Toast.makeText(LaporanStokActivity.this,"PDF Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

            document.close();
        }catch (NullPointerException e){
            Toast.makeText(LaporanStokActivity.this,"Data user tidak ditemukan",Toast.LENGTH_SHORT).show();
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
        cell.setCellValue((String) "Laporan "+tipe);
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Tanggal : "+timeStamp);

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "No.");
        cell = row.createCell(1);
        cell.setCellValue((String) "Kode Barang");
        cell = row.createCell(2);
        cell.setCellValue((String) "Nama Barang");
        cell = row.createCell(3);
        cell.setCellValue((String) "Satuan");
        cell = row.createCell(4);
        cell.setCellValue((String) "Stock");
        for (Barang barang : barangList) {
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue((String) String.valueOf(rowNum));
            cell = row.createCell(1);
            cell.setCellValue((String) barang.getKode());
            cell = row.createCell(2);
            cell.setCellValue((String) barang.getNama());
            cell = row.createCell(3);
            cell.setCellValue((String) barang.getSatuan());
            cell = row.createCell(4);
            cell.setCellValue((String) barang.getStok());

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

            File myFile = new File(pdfFolder+ "/" + tipe+ timeStamp + ".xlsx");
            FileOutputStream outputStream = new FileOutputStream(myFile);
            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(LaporanStokActivity.this,"Excel Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.laporan_menu, menu);
//        menu.findItem(R.id.sort_bulan).setVisible(false);
//        menu.findItem(R.id.sort_hari).setVisible(false);
        menu.findItem(R.id.sort_bulan).setTitle("Sort terbanyak");
        menu.findItem(R.id.sort_hari).setTitle("Sort terkecil");
        menu.findItem(R.id.sort_tahun).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sort_bulan:
                Collections.sort(barangList, new SortbyStokDESC());
                dBarangAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_hari:
                Collections.sort(barangList, new SortbyStokASC());
                dBarangAdapter.notifyDataSetChanged();
                return true;
            case R.id.print_pdf:
                try {
                    Toast.makeText(LaporanStokActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                    printToPDF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.print_excel:
                Toast.makeText(LaporanStokActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                try {
                    printExcel();
                }catch (Exception e){
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
