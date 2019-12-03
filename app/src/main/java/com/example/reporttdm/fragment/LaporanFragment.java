package com.example.reporttdm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.reporttdm.R;
import com.example.reporttdm.activity.LaporanActivity;
import com.example.reporttdm.activity.LaporanStokActivity;
import com.example.reporttdm.activity.LaporanTransaksiActivity;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.User;

public class LaporanFragment extends Fragment {

    private Button btnUmum, btnTrans, btnPenBar, btnPembar, btnModal, btnPeng, btnBackOffice;
    TinyDB tinyDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laporan, container, false);

        tinyDB = new TinyDB(getContext());

        btnUmum = (Button)v.findViewById(R.id.btn_lumum);
        btnTrans = (Button)v.findViewById(R.id.btn_lstransaksi);
        btnPenBar = (Button)v.findViewById(R.id.btn_lpenbarang);
        btnPembar = (Button)v.findViewById(R.id.btn_lpembarang);
        btnModal = (Button)v.findViewById(R.id.btn_lmodal);
        btnPeng = (Button)v.findViewById(R.id.btn_lpengunjung);
        btnBackOffice = (Button)v.findViewById(R.id.btn_lbo);

        try {
            if (((User)tinyDB.getObject("user_login", User.class)).getTipe().equals("2")){
                btnPembar.setVisibility(View.GONE);
                btnModal.setVisibility(View.GONE);
                btnPeng.setVisibility(View.GONE);
                btnBackOffice.setVisibility(View.GONE);
            }else if (((User)tinyDB.getObject("user_login", User.class)).getTipe().equals("3")){
                btnPembar.setVisibility(View.GONE);
                btnPeng.setVisibility(View.GONE);
                btnBackOffice.setVisibility(View.GONE);
            }
        }catch (Exception e){

        }

        btnUmum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LaporanActivity.class);
                startActivity(intent);
            }
        });
        btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LaporanTransaksiActivity.class);
                startActivity(intent);
            }
        });
        btnPenBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LaporanActivity.class);
                intent.putExtra("tipe","penjualan");
                startActivity(intent);
            }
        });
        btnPembar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LaporanActivity.class);
                intent.putExtra("tipe","pembelian");
                startActivity(intent);
            }
        });
        btnModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LaporanStokActivity.class);
                startActivity(intent);
            }
        });
        btnPeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LaporanActivity.class);
                startActivity(intent);
            }
        });
        btnBackOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LaporanActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }
}
