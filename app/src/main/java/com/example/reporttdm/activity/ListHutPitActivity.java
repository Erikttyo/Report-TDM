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
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.HutPitAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.GetHutPitResponse;
import com.example.reporttdm.model.HutPit;
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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

public class ListHutPitActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HutPitAdapter hutPitAdapter;
    ArrayList<HutPit> hutPitArrayList = new ArrayList<HutPit>();
    Toolbar toolbar;
    FloatingActionButton fab;
    TinyDB tinyDB;
    String jenis;
    private String tipe = "";
    private SwipeRefreshLayout swipeContainer;
    private Toko toko;
    private String timeCurrent;
    private String timeStamp;
    private ArrayList<HutPit> temp = new ArrayList<HutPit>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_hutpit);

        tinyDB = new TinyDB(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().hasExtra("jenis")){
            if (getIntent().getStringExtra("jenis").equals("1")){
                jenis = "1";
                setTitle("Hutang");
                tipe = "Hutang";
            }else{
                jenis = "0";
                setTitle("Piutang");
                tipe = "Piutang";
            }
        }

        toko = ((Toko)tinyDB.getObject("toko_login", Toko.class));
        Date date = new Date() ;
        timeCurrent = new SimpleDateFormat("dd").format(date);
        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(date);

        //Bundle bundle = getIntent().getExtras();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListHutPit);
        hutPitAdapter = new HutPitAdapter(hutPitArrayList,jenis);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListHutPitActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hutPitAdapter);

        System.out.println("list : "+ hutPitArrayList);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeHutPit);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getListHutPit(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetHutPitResponse> call = api.showHutangPiutang(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetHutPitResponse>() {
            @Override
            public void onResponse(Call<GetHutPitResponse> call, Response<GetHutPitResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    hutPitArrayList.clear();
                    temp.clear();
                    for (HutPit hutPit : response.body().getResult_hutpit()){
                        if (hutPit.getJenis().equals(jenis)){
                            hutPitArrayList.add(hutPit);
                        }
                    }
                    temp = hutPitArrayList;
                    hutPitAdapter.updateList(hutPitArrayList);
                    swipeContainer.setRefreshing(false);
                }else{
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ListHutPitActivity.this,"Gagal menampilkan",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetHutPitResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(ListHutPitActivity.this, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
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

    public void printToPDF() throws FileNotFoundException, DocumentException {

        try {
            String kota,nama;
            Toko toko = ((Toko)tinyDB.getObject("toko_login", Toko.class));
            Document document = new Document();

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
            table = new PdfPTable(4);

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("No.",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Tanggal",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Supplier",bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Jumlah "+tipe,bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            int i = 1;
            int total = 0;
            for (HutPit ts : temp){
                if(true) {
                    table.addCell(String.valueOf(i));
                    table.addCell(ts.getTanggal());
                    table.addCell(ts.getNama());
                    if (jenis.equals("1")){
                        table.addCell(doubleToStringNoDecimal(Double.parseDouble(ts.getModal())));
                        total = total + Integer.parseInt(ts.getModal());
                    }else{
                        table.addCell(doubleToStringNoDecimal(Double.parseDouble(ts.getJual())));
                        total = total + Integer.parseInt(ts.getJual());
                    }
                    i++;
                }
            }
            cell = new PdfPCell(new Phrase("Total",bold));
            cell.setColspan(3);
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
            table.setWidthPercentage(80);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);
            Toast.makeText(ListHutPitActivity.this,"PDF Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

            document.close();
        }catch (NullPointerException e){
            Toast.makeText(ListHutPitActivity.this,"Data user tidak ditemukan",Toast.LENGTH_SHORT).show();
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
        cell.setCellValue((String) "Laporan "+tipe);
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Tanggal : "+timeStamp);

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "No.");
        cell = row.createCell(1);
        cell.setCellValue((String) "Tanggal");
        cell = row.createCell(2);
        cell.setCellValue((String) "Supplier");
        cell = row.createCell(3);
        cell.setCellValue((String) "Jumlah "+tipe);

        int total = 0;
        for (HutPit hutPit : temp) {
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue((String) String.valueOf(rowNum));
            cell = row.createCell(1);
            cell.setCellValue((String) hutPit.getTanggal());
            cell = row.createCell(2);
            cell.setCellValue((String) hutPit.getNama());
            cell = row.createCell(3);
            if (jenis.equals("1")) {
                cell.setCellValue((String) doubleToStringNoDecimal(Double.parseDouble(hutPit.getModal())));
                total = total + Integer.parseInt(hutPit.getModal());
            }else{
                cell.setCellValue((String) doubleToStringNoDecimal(Double.parseDouble(hutPit.getJual())));
                total = total + Integer.parseInt(hutPit.getJual());
            }
        }

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue((String) "Total");
        cell = row.createCell(3);
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
            Toast.makeText(ListHutPitActivity.this,"Excel Berhasil disimpan di folder Kasir",Toast.LENGTH_LONG).show();

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.print_pdf:
                try {
                    Toast.makeText(ListHutPitActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                    printToPDF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.print_excel:
                Toast.makeText(ListHutPitActivity.this, "Harap tunggu...", Toast.LENGTH_SHORT).show();
                try {
                    printExcel();
                }catch (Exception e){
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

    public void filterHari() {
        temp = new ArrayList<HutPit>();
        for (HutPit p : hutPitArrayList){
            if (p.getTanggal().substring(8,10).equals(timeStamp.substring(0,2))){
                temp.add(p);
            }
        }
        hutPitAdapter.updateList(temp);
    }

    public void filterBulan() {
        temp = new ArrayList<HutPit>();
        for (HutPit p : hutPitArrayList){
            if (p.getTanggal().substring(5,7).equals(timeStamp.substring(3,5))){
                temp.add(p);
            }
        }
        hutPitAdapter.updateList(temp);
    }
    public void filterTahun() {
        temp = new ArrayList<HutPit>();
        for (HutPit p : hutPitArrayList){
            if (p.getTanggal().substring(0,4).equals(timeStamp.substring(6,10))){
                temp.add(p);
            }
        }
        hutPitAdapter.updateList(temp);
    }
}
