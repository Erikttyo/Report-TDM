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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.adapter.PembelianAdapter;
import com.example.reporttdm.model.Barang;

import java.util.ArrayList;

public class PembelianActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PembelianAdapter pembelianAdapter;
    FloatingActionButton fab;
    Toolbar toolbar;
    ArrayList<Barang> barangList = new ArrayList<Barang>();
    ImageButton btnLanjut;
    public static Activity pembelianActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembelian);

        pembelianActivity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar_pembelian);
        setSupportActionBar(toolbar);
        setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnLanjut = (ImageButton) toolbar.findViewById(R.id.btn_lanjut_beli);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerBeliBarang);
        pembelianAdapter = new PembelianAdapter(barangList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PembelianActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pembelianAdapter);

//        for (int i=0;i<5;i++){
//            barangList.add(new Barang());
//        }
//        pembelianAdapter.addAll(barangList);

        fab = (FloatingActionButton) findViewById(R.id.fab_belibarang);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PembelianActivity.this, DBarangActivity.class);
                intent.putExtra("tipe","1");
                startActivityForResult(intent,1);
            }
        });
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PembelianActivity.this, BayarPembelianActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list", barangList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                String id_barang=data.getStringExtra("id_barang");
                String nama=data.getStringExtra("nama");
                String kode=data.getStringExtra("kode");
                String harga=data.getStringExtra("harga");
                Toast.makeText(PembelianActivity.this,"Barang : "+result,Toast.LENGTH_SHORT).show();
                showDialogAdd(id_barang,nama,kode,harga);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void showDialogAdd(final String id_barang, final String nama, String kode, String harga){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PembelianActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pembelian, null);
        dialogBuilder.setView(dialogView);

        TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_pay_title);
        TextView txNama = (TextView) dialogView.findViewById(R.id.tx_namadb);
        TextView txKode = (TextView) dialogView.findViewById(R.id.tx_kodedb);
        TextView txHarga = (TextView) dialogView.findViewById(R.id.tx_hargadb);
        final EditText edtJumlah = (EditText) dialogView.findViewById(R.id.edt_jumlah);
        final EditText edtHarga = (EditText) dialogView.findViewById(R.id.edt_harga);
        txNama.setText(nama);
        txKode.setText(kode);
        txHarga.setText("Rp. "+harga);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!(edtJumlah.getText().toString().equals("")&&edtHarga.getText().toString().equals(""))){
                    barangList.add(new Barang(id_barang,edtHarga.getText().toString(),edtJumlah.getText().toString(),nama));
                    pembelianAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(PembelianActivity.this, "Harga dan Jumlah harap diisi", Toast.LENGTH_SHORT).show();
                }
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
}
