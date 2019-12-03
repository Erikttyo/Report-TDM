package com.example.reporttdm.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.activity.ListTransaksiActivity;
import com.example.reporttdm.adapter.ListPesananAdapter;
import com.example.reporttdm.adapter.TransaksiAdapter;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.Barang;
import com.example.reporttdm.model.GetBarangResponse;
import com.example.reporttdm.model.GetPesananResponse;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.Pesanan;
import com.example.reporttdm.model.ResultPembelianItem;
import com.example.reporttdm.model.Toko;
import com.example.reporttdm.model.Transaksi;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransaksiFragment extends Fragment {

    List<Transaksi> transaksiList = new ArrayList<Transaksi>();
    List<Transaksi> allTransaksiList = new ArrayList<Transaksi>();
    ArrayList<Transaksi> temp = new ArrayList<Transaksi>();
    ArrayList<Barang> barangList = new ArrayList<Barang>();
    ArrayList<Pesanan> pesananList = new ArrayList<Pesanan>();
    List<ResultPembelianItem> pembelianList = new ArrayList<ResultPembelianItem>();
    RecyclerView recyclerView;
    TransaksiAdapter transaksiAdapter;
    Button btnLanjut, btnSearch, btnAdd, btnBarcode, btnPajak, btnDiskon;
    EditText edtSearch;
    TinyDB tinyDB;
    ProgressDialog dialog;
    int jum;
    String pajak,diskon = "0";
    private Menu menu;
    private int posPesanan = 0;
    AlertDialog alertDialog;
    private int modeView = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaksi, container, false);

        tinyDB = new TinyDB(getContext());
        dialog = new ProgressDialog(getContext());

        setHasOptionsMenu(true);

        dialog.setMessage("Harap tunggu..");
        btnLanjut = (Button)v.findViewById(R.id.btn_lanjuttrans);
        btnSearch = (Button)v.findViewById(R.id.btn_searchtrans);
        btnBarcode = (Button)v.findViewById(R.id.btn_barcodetrans);
        btnAdd = (Button)v.findViewById(R.id.btn_addtrans);
        btnPajak = (Button)v.findViewById(R.id.btn_pajaktrans);
        btnDiskon = (Button)v.findViewById(R.id.btn_diskontrans);
        edtSearch = (EditText)v.findViewById(R.id.edt_searchtrans);
        edtSearch.setVisibility(View.GONE);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerTransaksi);
        transaksiAdapter = new TransaksiAdapter(transaksiList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(transaksiAdapter);

        try {
            pajak = ((Toko)tinyDB.getObject("toko_login", Toko.class)).getPajak();
            diskon = ((Toko)tinyDB.getObject("toko_login", Toko.class)).getDiskon();
        }catch (Exception e){

        }

        jum = 0;
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getJum()>0){
                    pesananList.clear();
                    pembelianList.clear();
                    Intent intent = new Intent(getContext(), ListTransaksiActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("list", temp);
                    intent.putExtras(bundle);
                    intent.putExtra("pajak",pajak);
                    intent.putExtra("diskon",diskon);
                    startActivityForResult(intent,123);
                }else{
                    Toast.makeText(getContext(),"Tidak ada barang yang dipilih",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateScan();
            }
        });
        btnPajak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPajak();
            }
        });
        btnDiskon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDiskon();
            }
        });

        /*dialog.show();
        getListBarang();*/

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtSearch.getVisibility()==View.VISIBLE){
                    Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.anim_close);
                    edtSearch.startAnimation(slideUp);
                    edtSearch.setVisibility(v.GONE);
                }else{
                    edtSearch.setVisibility(v.VISIBLE);
                    Animation slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.anim_expand);
                    edtSearch.startAnimation(slideDown);
                }
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
    }

    public void initiateScan(){
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    public void addTransaksi(int pos){
        jum = jum + 1;
        if (!temp.contains(transaksiList.get(pos))){
            int stok = Integer.parseInt(transaksiList.get(pos).getStok());
            int jumlah = Integer.parseInt(transaksiList.get(pos).getJumlah());
            transaksiList.get(pos).setJumlah(String.valueOf(jumlah+1));
            transaksiList.get(pos).setStok(String.valueOf(stok-1));
            temp.add(transaksiList.get(pos));
        }else{
            int index = temp.indexOf(transaksiList.get(pos));
            int jumlah = Integer.parseInt(temp.get(index).getJumlah());
            int stok = Integer.parseInt(transaksiList.get(pos).getStok());
            temp.get(index).setJumlah(String.valueOf(jumlah+1));
            temp.get(index).setStok(String.valueOf(stok-1));
        }
        btnLanjut.setText("Lanjut > "+String.valueOf(jum));
    }

    public int getJum() {
        return jum;
    }

    public void setJum(int jum) {
        this.jum = jum;
    }

    public void showDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_beli_tambahan, null);
        dialogBuilder.setView(dialogView);

        TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_dialog_title);
        final EditText edtNama = (EditText) dialogView.findViewById(R.id.edt_nama_produk);
        final EditText edtCode = (EditText) dialogView.findViewById(R.id.edt_code_produk);
        final EditText edtHargaD = (EditText) dialogView.findViewById(R.id.edt_hargad_produk);
        final EditText edtHargaJ = (EditText) dialogView.findViewById(R.id.edt_hargaj_produk);
        final TextView txJumlah = (TextView) dialogView.findViewById(R.id.tx_jumlah);
        final TextView btnPlus = (TextView) dialogView.findViewById(R.id.btn_plus);
        final TextView btnMin = (TextView) dialogView.findViewById(R.id.btn_min);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int jum = Integer.parseInt(txJumlah.getText().toString());
                jum = jum + 1;
                txJumlah.setText(String.valueOf(jum));
            }
        });
        btnMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txJumlah.getText().toString().equals("0")){
                    int jum = Integer.parseInt(txJumlah.getText().toString());
                    jum = jum - 1;
                    txJumlah.setText(String.valueOf(jum));
                }
            }
        });

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!(edtHargaD.getText().toString().equals("")&&
                        edtHargaJ.getText().toString().equals("")&&edtNama.getText().toString().equals(""))){
                    showPD();
                    addBarang(edtNama.getText().toString(),txJumlah.getText().toString(),
                            edtHargaD.getText().toString(), edtHargaJ.getText().toString());
                }else{
                    Toast.makeText(getContext(), "Nama dan Harga harap diisi", Toast.LENGTH_SHORT).show();
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

    public void showPD(){
        dialog.show();
    }

    public void showDialogBarcode(final String kode){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_barcode, null);
        dialogBuilder.setView(dialogView);

        TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_dialog_title);
        final EditText edtJumlah = (EditText) dialogView.findViewById(R.id.edt_jumlah);
        edtJumlah.setText("1");

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int i = 0;
                if(!edtJumlah.getText().toString().equals("")){
                    for (Barang b : barangList){
                        i++;
                        if (b.getKode().equals(kode)){
                            temp.add(new Transaksi(b.getId(),b.getNama(),"0",b.getHarga(),b.getStok(),b.getHarga_jual(),"1"));
                            setJum(getJum()+Integer.parseInt(edtJumlah.getText().toString()));
                            btnLanjut.setText("Lanjut > "+String.valueOf(getJum()));
                            break;
                        }
                    }
                    if (i==0){
                        Toast.makeText(getContext(), "Barang tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Nama dan Harga harap diisi", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == 123){
            if (resultCode==111){
                dialog.show();
                temp.clear();
                transaksiList.clear();
                allTransaksiList.clear();
                for (Barang b : barangList){
                    if (Integer.parseInt(b.getStok())>0){
                        transaksiList.add(new Transaksi(b.getId(),b.getNama(),"0",b.getHarga(),b.getStok(),b.getHarga_jual(),"1",b.getImage()));
                        allTransaksiList.add(new Transaksi(b.getId(),b.getNama(),"0",b.getHarga(),b.getStok(),b.getHarga_jual(),"1",b.getImage()));
                    }
                }
                jum = 0;
                btnLanjut.setText("Lanjut > "+String.valueOf(jum));
                transaksiAdapter.notifyDataSetChanged();
                //((MenuActivity)getActivity()).resetTransaksi();
                dialog.dismiss();
            }
        }else if(result != null) {
            if(result.getContents() != null) {
                showDialogBarcode(result.getContents());
                Toast.makeText(getContext(),result.getContents(),Toast.LENGTH_SHORT).show();
            }
        }
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
                    transaksiList.clear();
                    barangList.clear();
                    allTransaksiList.clear();
                    barangList.addAll(response.body().getResult_barang());
                    for (Barang b : response.body().getResult_barang()){
                        if (Integer.parseInt(b.getStok())>0){
                            transaksiList.add(
                                    new Transaksi(b.getId(),b.getNama(),"0",b.getHarga(),b.getStok(),b.getHarga_asli(),b.getHarga_jual(),"1",b.getImage()));
                        }
                        allTransaksiList.add(
                                new Transaksi(b.getId(),b.getNama(),"0",b.getHarga(),b.getStok(),b.getHarga_asli(),b.getHarga_jual(),"1",b.getImage()));
                    }
                    //barangList.addAll(response.body().getResult_barang());
                    transaksiAdapter.notifyDataSetChanged();
                    getListPesanan();
                    //swipeContainer.setRefreshing(false);
//                    tes();
                }else{
                    dialog.dismiss();
                    Toast.makeText(getContext(),"Tidak ada barang",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetBarangResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getListPesanan(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<GetPesananResponse> call = api.showPesanan(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<GetPesananResponse>() {
            @Override
            public void onResponse(Call<GetPesananResponse> call, Response<GetPesananResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    pesananList.clear();
                    pesananList.addAll(response.body().getResult_pesanan());
                    pembelianList.addAll(response.body().getResult_pembelian());
                    int pes = pesananList.size();
                    MenuItem pesananMenuItem = menu.findItem(R.id.pesanan_transaksi);
                    pesananMenuItem.setTitle("Pesanan ("+String.valueOf(pes)+")");
                    System.out.println("pesanan : "+pes);
                    dialog.dismiss();
                }else{
                    dialog.dismiss();
                    Toast.makeText(getContext(),"Tidak ada pesanan",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetPesananResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    //swipeContainer.setRefreshing(false);
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDialogPajak(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pajak, null);
        dialogBuilder.setView(dialogView);

        TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_dialog_title);
        final EditText edtPajak = (EditText) dialogView.findViewById(R.id.edt_pajak_dialog);
        edtPajak.setText(((Toko)tinyDB.getObject("toko_login", Toko.class)).getPajak());
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pajak = edtPajak.getText().toString();
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

    public void showDialogDiskon(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_diskon, null);
        dialogBuilder.setView(dialogView);

        TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_dialog_title);
        final EditText edtDiskon = (EditText) dialogView.findViewById(R.id.edt_diskon_dialog);
        edtDiskon.setText(((Toko)tinyDB.getObject("toko_login", Toko.class)).getDiskon());
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                diskon = edtDiskon.getText().toString();
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
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
        dialog.show();
        temp.clear();
        btnLanjut.setText("Lanjut > 0");
        getListBarang();
    }

    public void addBarang(final String nama, final String jumlah, final String hargad, final String hargaj) {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.addBarang(((User) tinyDB.getObject("user_login", User.class)).getId_toko(), "0",
                nama, "000", hargad, jumlah, hargaj, "0", "","");
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(getContext(), "Tambah barang berhasil", Toast.LENGTH_SHORT).show();
                    temp.add(new Transaksi("0",nama,jumlah,hargad,jumlah,
                            hargaj,"1"));
                    setJum(getJum()+Integer.parseInt(jumlah));
                    btnLanjut.setText("Lanjut > "+String.valueOf(getJum()));
                    getListBarang();
                    //dialog.dismiss();
                }else{
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Tambah barang gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
//                    swipeContainer.setRefreshing(false);
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        menu.clear();    //remove all items
//        getActivity().getMenuInflater().inflate(R.menu.transaksi_menu, menu);
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.transaksi_menu, menu);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //        // Handle item selection
        switch(item.getItemId()) {
            case R.id.clear_transaksi:
                dialog.show();
                temp.clear();
                transaksiList.clear();
                allTransaksiList.clear();
                for (Barang b : barangList){
                    if (Integer.parseInt(b.getStok())>0){
                        transaksiList.add(new Transaksi(b.getId(),b.getNama(),"0",b.getHarga(),b.getStok(),b.getHarga_asli(),b.getHarga_jual(),"1",b.getImage()));
                    }
                    allTransaksiList.add(new Transaksi(b.getId(),b.getNama(),"0",b.getHarga(),b.getStok(),b.getHarga_asli(),b.getHarga_jual(),"1",b.getImage()));
                }
                jum = 0;
                btnLanjut.setText("Lanjut > "+String.valueOf(jum));
                transaksiAdapter.notifyDataSetChanged();
                //((MenuActivity)getActivity()).resetTransaksi();
                dialog.dismiss();
                return true;
            case R.id.pesanan_transaksi:
                temp.clear();
                showDialogListPesanan();
            case R.id.view_mode:
                transaksiAdapter = new TransaksiAdapter(transaksiList, this);
                recyclerView.setAdapter(transaksiAdapter);
                if (modeView==0) {
                    modeView = 1;
                    transaksiAdapter.setMode(modeView);
                    MenuItem viewItem = menu.findItem(R.id.view_mode);
                    viewItem.setTitle("Mode List");
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                }else{
                    modeView = 0;
                    transaksiAdapter.setMode(modeView);
                    MenuItem viewItem = menu.findItem(R.id.view_mode);
                    viewItem.setTitle("Mode Grid");
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int getPosPesanan() {
        return posPesanan;
    }

    public void setPosPesanan(int posPesanan) {
        this.posPesanan = posPesanan;
    }

    public void addPesanan(int posPesanan){
        temp.clear();
        int i = 0;
        ResultPembelianItem pembelianItem = null;
        for (ResultPembelianItem rp : pembelianList){
            if (pesananList.get(posPesanan).getId_transaksi().equals(rp.getId_transaksi())){
                pembelianItem = rp;
                if (allTransaksiList.get(i).getId().equals(rp.getId_barang())){
                    allTransaksiList.get(i).setJumlah(rp.getJumlah());
                }
                temp.add(new Transaksi("0",pembelianItem.getNama_barang(),pembelianItem.getJumlah(),
                        pembelianItem.getHarga_dasar(),pembelianItem.getStok(),
                        pembelianItem.getHarga_jual(),"1"));
                setJum(getJum()+Integer.parseInt(pembelianItem.getJumlah()));
            }
            i++;
        }
        transaksiAdapter.notifyDataSetChanged();
        btnLanjut.setText("Lanjut > "+String.valueOf(getJum()));
        alertDialog.dismiss();
        Intent intent = new Intent(getContext(), ListTransaksiActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", temp);
        intent.putExtras(bundle);
        intent.putExtra("pajak",pajak);
        intent.putExtra("diskon",diskon);
        intent.putExtras(bundle);
        intent.putExtra("pesanan",pesananList.get(posPesanan).getId_transaksi());
        startActivityForResult(intent,123);
//
//        System.out.println("pindah");
//        getContext().startActivity(intent);
    }

    public void showDialogListPesanan(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_list_pesanan, null);
        dialogBuilder.setView(dialogView);

        TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_title);
        RecyclerView recyclerPesanan;
        ListPesananAdapter pesananAdapter;
        recyclerPesanan = (RecyclerView) dialogView.findViewById(R.id.recyclerListPesanan);
        pesananAdapter = new ListPesananAdapter(pesananList,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(dialogView.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerPesanan.setLayoutManager(layoutManager);
        recyclerPesanan.setItemAnimator(new DefaultItemAnimator());
        recyclerPesanan.setAdapter(pesananAdapter);
        pesananAdapter.notifyDataSetChanged();


        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void filter(String text){
        List<Transaksi> temp = new ArrayList();
        for(Transaksi d: transaksiList){
            if(d.getNama().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        transaksiAdapter.updateList(temp);
    }

    public void deletePesanan(String id_transaksi) {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.deletePesanan(id_transaksi);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(getContext(), "Delete berhasil", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    getListPesanan();
                }else{
                    Toast.makeText(getContext(), "Delete gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getContext(), "Harap koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDeleteConfirm(final String id_transaksi){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deletePesanan(id_transaksi);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Apakah anda yakin delete pesanan ?").setPositiveButton("Ya", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener).show();
    }

}
