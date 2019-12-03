package com.example.reporttdm.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.ListTransaksiAdapter;
import com.example.reporttdm.model.Transaksi;

import java.util.ArrayList;

public class ListTransaksiActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ListTransaksiAdapter transaksiAdapter;
    TextView txTotal;
    ArrayList<Transaksi> arrayList = new ArrayList<Transaksi>();
    Toolbar toolbar;
    FloatingActionButton fab;
    public static Activity listTransaksiActivity;
    String pesanan = "";
    int total = 0;
    int jum = 0;
    int delete = 0;
    String pajak,diskon = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaksi);

        listTransaksiActivity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        setTitle("Total Transaksi");
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

        txTotal = (TextView)findViewById(R.id.tx_total_transaksi);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListTransaksi);
        transaksiAdapter = new ListTransaksiAdapter(arrayList,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListTransaksiActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(transaksiAdapter);

        System.out.println("list : "+arrayList);

        for (Transaksi t : arrayList){
            total = total + (Integer.parseInt(t.getJual())*Integer.parseInt(t.getJumlah()));
        }

        txTotal.setText("Rp. "+String.valueOf(total));

        fab = (FloatingActionButton) findViewById(R.id.fab_listtransaksi);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListTransaksiActivity.this, InputBayarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list", arrayList);
                intent.putExtra("pesanan",pesanan);
                intent.putExtras(bundle);
                intent.putExtra("pajak",pajak);
                intent.putExtra("diskon",diskon);
                startActivity(intent);
            }
        });

    }

    public void setFinish(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
    }

    public void showEditTransaksi(final int posisi, String nama, String harga, String jumlah){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ListTransaksiActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_transaksi, null);
        dialogBuilder.setView(dialogView);

        jum = Integer.parseInt(jumlah);
        total = 0;

        TextView txNama = (TextView)dialogView.findViewById(R.id.tx_namadb);
        TextView txKode = (TextView)dialogView.findViewById(R.id.tx_kode);
        TextView txJumItem = (TextView)dialogView.findViewById(R.id.tx_harga);
        TextView txHarga = (TextView)dialogView.findViewById(R.id.tx_jumlah_beli);
        TextView txMin = (TextView)dialogView.findViewById(R.id.btn_min);
        TextView txPlus = (TextView)dialogView.findViewById(R.id.btn_plus);
        final Button btnDelete = (Button)dialogView.findViewById(R.id.btn_delete);
        final TextView txJumlah = (TextView)dialogView.findViewById(R.id.tx_jumlah);
        final EditText edtHarga = (EditText) dialogView.findViewById(R.id.edt_harga);
        final EditText edtDiskon = (EditText) dialogView.findViewById(R.id.edt_diskon);
        txNama.setText(nama);
        txJumItem.setText("Rp "+harga);
        txJumlah.setText(jumlah);
        txPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jum = jum + 1;
                txJumlah.setText(String.valueOf(jum));
            }
        });
        txMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jum!=0){
                    jum = jum - 1;
                }
                txJumlah.setText(String.valueOf(jum));
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delete==0){
                    delete = 1;
                    btnDelete.setText("Deleted");
                }else{
                    delete = 0;
                    btnDelete.setText("Delete");
                }
            }
        });
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                arrayList.get(posisi).setJumlah(txJumlah.getText().toString());
                if (!edtHarga.getText().toString().equals("")){
                    arrayList.get(posisi).setJual(edtHarga.getText().toString());
                }

                if (delete==1){
                    arrayList.remove(posisi);
                }

                for (Transaksi t : arrayList){
                    total = total + (Integer.parseInt(t.getJual())*Integer.parseInt(t.getJumlah()));
                }

                transaksiAdapter.notifyDataSetChanged();
                txTotal.setText("Rp "+String.valueOf(total));
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

    @Override
    public boolean onSupportNavigateUp() {
        arrayList.clear();
        transaksiAdapter.notifyDataSetChanged();
        onBackPressed();
        return true;
    }
}
