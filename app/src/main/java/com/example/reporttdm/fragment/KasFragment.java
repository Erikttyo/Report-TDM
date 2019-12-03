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
import com.example.reporttdm.activity.AddKasActivity;
import com.example.reporttdm.activity.AkunKasActivity;
import com.example.reporttdm.activity.ArusKasActivity;
import com.example.reporttdm.activity.MutasiKasActivity;

public class KasFragment extends Fragment {

    Button btnAkunKas, btnArusKas, btnKasMasuk, btnKasKeluar, btnMutasiKas;

    public KasFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kas, container, false);

        btnAkunKas = v.findViewById(R.id.btn_akun_kas);
        btnArusKas = v.findViewById(R.id.btn_arus_kas);
        btnKasMasuk = v.findViewById(R.id.btn_kas_masuk);
        btnKasKeluar = v.findViewById(R.id.btn_kas_keluar);
        btnMutasiKas = v.findViewById(R.id.btn_kas_mutasi);

        btnAkunKas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AkunKasActivity.class));
            }
        });
        btnArusKas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ArusKasActivity.class));
            }
        });
        btnKasMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddKasActivity.class).putExtra("jenis","1"));
            }
        });
        btnKasKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddKasActivity.class).putExtra("jenis","0"));
            }
        });
        btnMutasiKas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MutasiKasActivity.class));
            }
        });

        return v;
    }
}
