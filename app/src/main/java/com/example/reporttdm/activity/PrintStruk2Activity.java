package com.example.reporttdm.activity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.reporttdm.R;
import com.example.reporttdm.helper.PrinterCommands;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.helper.Utils;
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.Transaksi;
import com.example.reporttdm.model.User;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PrintStruk2Activity extends AppCompatActivity {

    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;
    private TinyDB tinyDB;
    private User user;
    private Toko toko;
    String id_transaksi = "0";
    String jual = "0";
    String bayar = "0";
    int kembali = 0;
    ArrayList<Transaksi> arrayList = new ArrayList<Transaksi>();
    private String timeStamp,norek,nama,title,pokok,bHasil,SWajib,TAngsuran;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        tinyDB = new TinyDB(PrintStruk2Activity.this);
        user = tinyDB.getObject("user_login",User.class);
        toko = tinyDB.getObject("toko_login",Toko.class);

        Bundle bundle = getIntent().getExtras();
        arrayList = bundle.getParcelableArrayList("list");
        id_transaksi = getIntent().getStringExtra("id_transaksi");
        jual = getIntent().getStringExtra("jual");
        bayar = getIntent().getStringExtra("bayar");
        System.out.println("bayar : "+bayar+" - "+jual);
        kembali = Integer.parseInt(bayar)-Integer.parseInt(jual);

        Date date = new Date() ;
        //timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
        timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);

        title = "title";
        norek = "norek";
        nama = "nama";

        pokok = "pokok";
        bHasil = "bhasil";
        SWajib = "swajib";
        TAngsuran = "tangsuran";
        printPinjaman();

    }

    protected void printPinjaman() {
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
                finish();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = DeviceList.getSocket();
            if(btsocket != null){
                //printText(message.getText().toString());
                printPinjaman();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

