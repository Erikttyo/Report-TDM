package com.example.reporttdm.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.activity.ListUserActivity;
import com.example.reporttdm.activity.PickDeviceActivity;
import com.example.reporttdm.activity.ProfileActivity;
import com.example.reporttdm.activity.TokoActivity;
import com.example.reporttdm.helper.TinyDB;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.User;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengaturanFragment extends Fragment {

    private Button btnProfile, btnToko, btnManajemen, btnLainnya, btnPrint;
    private TinyDB tinyDB;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pengaturan, container, false);

        tinyDB = new TinyDB(getContext());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Harap tunggu...");

        btnProfile = (Button)v.findViewById(R.id.btn_pprofile);
        btnToko = (Button)v.findViewById(R.id.btn_ptoko);
        btnManajemen = (Button)v.findViewById(R.id.btn_pstaff);
        btnLainnya = (Button)v.findViewById(R.id.btn_plainnya);
        btnPrint = (Button)v.findViewById(R.id.btn_pprint);

        try {
            if (((User)tinyDB.getObject("user_login", User.class)).getTipe().equals("2")){
                btnToko.setVisibility(View.GONE);
                btnManajemen.setVisibility(View.GONE);
                btnLainnya.setVisibility(View.GONE);
            }else if (((User)tinyDB.getObject("user_login", User.class)).getTipe().equals("3")){
                btnToko.setVisibility(View.GONE);
                btnManajemen.setVisibility(View.GONE);
                btnLainnya.setVisibility(View.VISIBLE);
            }else{
                btnToko.setVisibility(View.VISIBLE);
                btnManajemen.setVisibility(View.VISIBLE);
                btnLainnya.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){

        }

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),ProfileActivity.class));
            }
        });
        btnToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),TokoActivity.class));
            }
        });
        btnManajemen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),ListUserActivity.class));
            }
        });
        btnLainnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                progressDialog.show();
                                resetData();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Apakah anda yakin menghapus seluruh data ?").setPositiveButton("Ya", dialogClickListener)
                        .setNegativeButton("Tidak", dialogClickListener).show();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PickDeviceActivity.class));
            }
        });

        return v;
    }

    private void resetData(){
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.resetData(((User)tinyDB.getObject("user_login", User.class)).getId_toko());
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Reset data berhasil",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Reset data gagal",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
