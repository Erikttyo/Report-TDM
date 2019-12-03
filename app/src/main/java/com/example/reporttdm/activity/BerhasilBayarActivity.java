package com.example.reporttdm.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.helper.BluetoothPrinter;
import com.example.reporttdm.helper.PrinterCommands;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.helper.Utils;
import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.Perangkat;
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.Transaksi;
import com.example.reporttdm.model.User;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BerhasilBayarActivity extends AppCompatActivity {

    Toolbar toolbar;
    TinyDB tinyDB;
    ArrayList<Transaksi> arrayList = new ArrayList<Transaksi>();
    ArrayList<Barang> barangList = new ArrayList<Barang>();
    String id_transaksi="0";
    String pesanan = "0";
    String jual = "0";
    String bayar = "0";
    int kembali = 0;
    TextView txKembalian;
    Button btnTransLagi, btnPrint, btnShare;
    private User user;
    private Toko toko;
    private ProgressDialog dialog;
    private String diskon, pajak = "0";
    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;
    private String timeStamp, printContent = "";
    private final UUID SPP_UUID = UUID
            .fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berhasil_bayar);

        tinyDB = new TinyDB(this);
        user = tinyDB.getObject("user_login",User.class);
        toko = tinyDB.getObject("toko_login",Toko.class);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Harap tunggu...");

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        arrayList = bundle.getParcelableArrayList("list");
        id_transaksi = getIntent().getStringExtra("id_transaksi");
        jual = getIntent().getStringExtra("jual");
        bayar = getIntent().getStringExtra("bayar");
        diskon = getIntent().getStringExtra("diskon");
        pajak = getIntent().getStringExtra("pajak");
        if (getIntent().hasExtra("pesanan")){
            pesanan = getIntent().getStringExtra("pesanan");
        }
        System.out.println("bayar : "+bayar+" - "+jual);
        kembali = Integer.parseInt(bayar.replace(".",""))-Integer.parseInt(jual.replace(".",""));
        Date date = new Date() ;
        //timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
        timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);

        btnTransLagi = (Button)findViewById(R.id.btn_transaksi_ulang);
        btnPrint = (Button)findViewById(R.id.btn_cetak);
        btnShare = (Button)findViewById(R.id.btn_share);
        txKembalian = (TextView)findViewById(R.id.tx_kembalian);
        txKembalian.setText("Kembalian : Rp."+String.valueOf(kembali));
        btnTransLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent returnIntent = new Intent();
                ListTransaksiActivity.listTransaksiActivity.setResult(111);
                ListTransaksiActivity.listTransaksiActivity.finish();
                InputBayarActivity.inputBayarActivity.finish();
                finish();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(BerhasilBayarActivity.this, PrintStruk2Activity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("list", arrayList);
//                intent.putExtras(bundle);
//                intent.putExtra("id_transaksi",id_transaksi);
//                intent.putExtra("jual",jual);
//                intent.putExtra("bayar",bayar);
//                startActivity(intent);
                try {
                    dialog.show();
                    if (pesanan.equals("1")){
                        printStrukPesanan();
                    }else {
                        printStruk2();
                        //printStruk();
                    }
                    //printPinjaman();
                }catch (Exception e){

                }
            }
        });
        setPrintContent();
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, printContent);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void printStruk(){
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
                    mPrinter.printText(toko.getNama());
                    mPrinter.addNewLine();
                    mPrinter.printText("Petugas:"+user.getNama());
                    mPrinter.addNewLine();
                    mPrinter.printText("================================");
                    mPrinter.addNewLine();
                    mPrinter.printText(timeStamp.substring(0,10));
                    mPrinter.addNewLine();
                    mPrinter.printText(timeStamp.substring(11,19));
                    mPrinter.addNewLine();
                    mPrinter.printText("No. Transaksi : "+id_transaksi);
                    mPrinter.addNewLine();
                    mPrinter.printText("================================");
                    mPrinter.addNewLine();
                    for (Transaksi transaksi : arrayList){
                        int spasi = 32 - transaksi.getNama().length();
                        String spasiString = "";
                        for (int i=0;i<spasi;i++){
                            spasiString = spasiString+" ";
                        }
                        mPrinter.printText(transaksi.getNama());
                        mPrinter.addNewLine();
                        String jumhar = transaksi.getJumlah() + " X " + transaksi.getJual()+" : ";
                        int jumlah = Integer.parseInt(transaksi.getJumlah())*Integer.parseInt(transaksi.getJual());
                        String jumlahString = String.valueOf(jumlah);
                        spasi = 32 - jumhar.length() - jumlahString.length();
                        spasiString = "";
                        for (int i=0;i<spasi;i++){
                            spasiString = spasiString+" ";
                        }
                        System.out.println("jumhar : "+jumhar+spasiString+jumlahString);
                        mPrinter.printText(jumhar+jumlahString);
                        mPrinter.addNewLine();
                    }
                    int spasiJual = 22 - jual.length();
                    String spasiStringJual = "";
                    int spasiBayar = 22 - bayar.length();
                    String spasiStringBayar = "";
                    int spasiKembali = 22 - String.valueOf(kembali).length();
                    String spasiStringKembali = "";
                    int spasiPajak = 22 - String.valueOf(pajak).length();
                    String spasiStringPajak = "";
                    int spasiDiskon = 22 - String.valueOf(diskon).length();
                    String spasiStringDiskon = "";
                    for (int i=0;i<spasiJual;i++){
                        spasiStringJual = spasiStringJual+" ";
                    }
                    for (int i=0;i<spasiBayar;i++){
                        spasiStringBayar = spasiStringBayar+" ";
                    }
                    for (int i=0;i<spasiKembali;i++){
                        spasiStringKembali = spasiStringKembali+" ";
                    }
                    for (int i=0;i<spasiPajak;i++){
                        spasiStringPajak = spasiStringPajak+" ";
                    }
                    for (int i=0;i<spasiDiskon;i++){
                        spasiStringDiskon = spasiStringDiskon+" ";
                    }
                    mPrinter.printText("================================");
                    mPrinter.addNewLine();
                    mPrinter.printText("Total   : "+jual);
                    mPrinter.addNewLine();
                    mPrinter.printText("Bayar   : "+bayar);
                    mPrinter.addNewLine();
                    mPrinter.printText("Kembali : "+String.valueOf(kembali));
//                    mPrinter.addNewLine();
//                    mPrinter.printText("Diskon  : "+diskon);
//                    mPrinter.addNewLine();
//                    mPrinter.printText("Pajak   : "+pajak);
//                    mPrinter.addNewLine();
                    mPrinter.printText(" ");
                    mPrinter.addNewLine();
                    mPrinter.printText(" ");
                    mPrinter.addNewLine();
                    mPrinter.finish();
                    dialog.dismiss();
                }

                @Override
                public void onFailed() {
                    Log.d("BluetoothPrinter", "Conection failed");
                    dialog.dismiss();
                }
            });
        }catch (Exception e){
            Toast.makeText(BerhasilBayarActivity.this,"Belum setting perangkat bluetooth",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setPrintContent(){
        printContent = printContent+timeStamp;
        printContent = printContent+"\n"+toko.getNama();
        printContent = printContent+"\n"+toko.getLokasi();
        printContent = printContent+"\n"+toko.getTelephone();
        printContent = printContent+"\n"+"--------------------------------";
        printContent = printContent+"\n"+"No. Transaksi : "+id_transaksi;
        printContent = printContent+"\n"+user.getNama();
        printContent = printContent+"\n"+"--------------------------------";
        for (Transaksi transaksi : arrayList){
            printContent = printContent+"\n"+transaksi.getNama();
            String jumhar = transaksi.getJumlah() + " X " +
                    doubleToStringNoDecimal(transaksi.getJual()) + " = ";
            int jumlah = Integer.parseInt(transaksi.getJumlah())*Integer.parseInt(transaksi.getJual());
            String jumlahString = doubleToStringNoDecimal(String.valueOf(jumlah));
            printContent = printContent+"\n"+jumhar+jumlahString;
        }
        printContent = printContent+"\n"+"--------------------------------";
        printContent = printContent+"\n"+"Total   : "+doubleToStringNoDecimal(jual);
        printContent = printContent+"\n"+"Bayar   : "+doubleToStringNoDecimal(bayar);
        printContent = printContent+"\n"+"Kembali : "+doubleToStringNoDecimal(String.valueOf(kembali));
        printContent = printContent+"\n"+"--------------------------------";
        try {
            printContent = printContent+"\n"+tinyDB.getString("ucapan");
        }catch (Exception e){
            printContent = printContent+"\n"+"Terima kasih telah berbelanja di Toko "+toko.getNama();
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
                    Date date = new Date() ;
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
                    mPrinter.printCustom(toko.getNama(),3,1);
                    mPrinter.printCustom("Petugas:"+user.getNama(),1,1);printContent = printContent+"\n"+user.getNama();
                    mPrinter.printCustom(timeStamp.substring(0,10) + " "+timeStamp.substring(11,19),1,1);
                    mPrinter.printCustom("No. Transaksi : "+id_transaksi,1,1);
                    mPrinter.printCustom("--------------------------------",1,0);
                    for (Transaksi transaksi : arrayList){
                        mPrinter.printCustom(transaksi.getNama(),1,1);
                        String jumhar = transaksi.getJumlah() + " X " +
                                doubleToStringNoDecimal(transaksi.getJual()) + " = ";
                        int jumlah = Integer.parseInt(transaksi.getJumlah())*Integer.parseInt(transaksi.getJual());
                        String jumlahString = doubleToStringNoDecimal(String.valueOf(jumlah));
                        mPrinter.printCustom(jumhar+jumlahString,1,1);
                    }
                    mPrinter.printCustom("--------------------------------",1,0);
                    mPrinter.printCustom("Total   : "+doubleToStringNoDecimal(jual),1,1);
                    mPrinter.printCustom("Bayar   : "+doubleToStringNoDecimal(bayar),1,1);
                    mPrinter.printCustom("Kembali : "+doubleToStringNoDecimal(String.valueOf(kembali)),1,1);
                    mPrinter.printCustom("--------------------------------",1,0);
                    try {
                        mPrinter.printCustom(tinyDB.getString("ucapan"),1,1);
                    }catch (Exception e){
                        mPrinter.printCustom("Terima kasih telah berbelanja di Toko "+toko.getNama(),1,1);
                    }
                    mPrinter.printCustom(" ",1,1);
                    mPrinter.printNewLine();
                    mPrinter.printNewLine();
                    mPrinter.finish();
                    dialog.dismiss();
                }

                @Override
                public void onFailed() {
                    Log.d("BluetoothPrinter", "Conection failed");
                    dialog.dismiss();
                }
            });
        }catch (Exception e){
            Toast.makeText(BerhasilBayarActivity.this,"Belum setting perangkat bluetooth",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void printStrukPesanan(){
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
                    mPrinter.printCustom(toko.getNama(),1,1);
                    mPrinter.printCustom("Petugas:"+user.getNama(),1,1);
                    //printUnicode();
                    mPrinter.printCustom(timeStamp.substring(0,10) + " "+timeStamp.substring(11,19),1,1);
                    mPrinter.printCustom("No. Transaksi : "+id_transaksi,1,1);
                    mPrinter.printCustom("--------------------------------",1,0);
                    for (Transaksi transaksi : arrayList){
                        mPrinter.printCustom(transaksi.getNama(),1,1);
                        String jumhar = transaksi.getJumlah() + " X " + doubleToStringNoDecimal(transaksi.getJual()) +" : ";
                        int jumlah = Integer.parseInt(transaksi.getJumlah())*Integer.parseInt(transaksi.getJual());
                        String jumlahString = doubleToStringNoDecimal(String.valueOf(jumlah));
                        int spasi = 32 - jumhar.length() - jumlahString.length();
                        String spasiString = "";
                        for (int i=0;i<spasi;i++){
                            spasiString = spasiString+" ";
                        }
                        System.out.println("jumhar : "+jumhar+spasiString+jumlahString);
                        mPrinter.printCustom(jumhar+jumlahString,1,1);
                    }
                    int spasiJual = 22 - jual.length();
                    String spasiStringJual = "";
                    for (int i=0;i<spasiJual;i++){
                        spasiStringJual = spasiStringJual+" ";
                    }
                    mPrinter.printCustom("--------------------------------",1,0);
                    mPrinter.printCustom("Total   : "+doubleToStringNoDecimal(jual),1,1);
                    mPrinter.printCustom("--------------------------------",1,0);
                    try {
                        mPrinter.printCustom(tinyDB.getString("ucapan"),1,1);
                    }catch (Exception e){
                        mPrinter.printCustom("Terima kasih telah berbelanja di Toko "+toko.getNama(),1,1);
                    }
                    mPrinter.printCustom(" ",1,0);
//                printCustom("Total Angsuran : "+TAngsuran,1,0);
//                printCustom("================================",1,0);
//
//                printCustom(user.getNama(),1,0);
                    //resetPrint(); //reset printer
                    mPrinter.printNewLine();
                    mPrinter.printCustom(" ",1,0);
                    mPrinter.printNewLine();
                    mPrinter.finish();
                    dialog.dismiss();
                }

                @Override
                public void onFailed() {
                    Log.d("BluetoothPrinter", "Conection failed");
                    dialog.dismiss();
                }
            });
        }catch (Exception e){
            Toast.makeText(BerhasilBayarActivity.this,"Belum setting perangkat bluetooth",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        ListTransaksiActivity.listTransaksiActivity.finish();
        InputBayarActivity.inputBayarActivity.finish();
        super.onBackPressed();
    }

    public static String doubleToStringNoDecimal(String number) {
        Double d = Double.parseDouble(number);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

    protected void printPinjaman() throws IOException {
        Toast.makeText(BerhasilBayarActivity.this,"Printing...",Toast.LENGTH_SHORT).show();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice mBtDevice =
                btAdapter.getRemoteDevice(tinyDB.getObject("device", Perangkat.class).getAddress());
        UUID uuid = mBtDevice.getUuids()[0].getUuid();
        try {
            btsocket = mBtDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(btsocket == null){
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        }
        else{
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream = btsocket.getOutputStream();

                byte[] printformat = { 0x1B, 0*21, FONT_TYPE };
                //outputStream.write(printformat);

                //print title
                //printUnicode();
                //print normal text
                printCustom(toko.getNama(),1,1);
                printCustom("Petugas:"+user.getNama(),1,1);
                //printUnicode();
                printCustom("================================",1,0);
                printCustom(timeStamp.substring(0,10),1,0);
                printCustom(timeStamp.substring(11,19),1,0);
                printCustom("No. Transaksi : "+id_transaksi,1,0);
                printCustom("================================",1,0);
                for (Transaksi transaksi : arrayList){
                    printCustom(transaksi.getNama(),1,0);
                    String jumhar = transaksi.getJumlah() + " X " + transaksi.getJual();
                    int jumlah = Integer.parseInt(transaksi.getJumlah())*Integer.parseInt(transaksi.getJual());
                    String jumlahString = String.valueOf(jumlah);
                    int spasi = 32 - jumhar.length() - jumlahString.length();
                    String spasiString = "";
                    for (int i=0;i<spasi;i++){
                        spasiString = spasiString+" ";
                    }
                    System.out.println("jumhar : "+jumhar+spasiString+jumlahString);
                    printCustom(jumhar+spasiString+jumlahString,1,0);
                }
                int spasiJual = 22 - jual.length();
                String spasiStringJual = "";
                int spasiBayar = 22 - bayar.length();
                String spasiStringBayar = "";
                int spasiKembali = 22 - String.valueOf(kembali).length();
                String spasiStringKembali = "";
                for (int i=0;i<spasiJual;i++){
                    spasiStringJual = spasiStringJual+" ";
                }
                for (int i=0;i<spasiBayar;i++){
                    spasiStringBayar = spasiStringBayar+" ";
                }
                for (int i=0;i<spasiKembali;i++){
                    spasiStringKembali = spasiStringKembali+" ";
                }
                printCustom("================================",1,0);
                printCustom("Total   : "+spasiStringJual+jual,1,0);
                printCustom("Bayar   : "+spasiStringBayar+bayar,1,0);
                printCustom("Kembali : "+spasiStringKembali+String.valueOf(kembali),1,0);
//                printCustom("Total Angsuran : "+TAngsuran,1,0);
//                printCustom("================================",1,0);
//
//                printCustom(user.getNama(),1,0);
                //resetPrint(); //reset printer
                printNewLine();
                printNewLine();

                outputStream.flush();
                //finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    //print unicode
    public void printUnicode(){
        try {
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void resetPrint() {
        try{
            outputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            outputStream.write(PrinterCommands.FS_FONT_ALIGN);
            outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            outputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            outputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String leftRightAlign(String str1, String str2) {
        String ans = str1 +str2;
        if(ans.length() <31){
            int n = (31 - str1.length() + str2.length());
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }
        return ans;
    }


    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(btsocket!= null){
                outputStream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
