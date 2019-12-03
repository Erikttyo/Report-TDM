package com.example.reporttdm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reporttdm.R;
import com.example.reporttdm.model.InsertResponse;
import com.example.reporttdm.model.KategoriBarang;
import com.example.reporttdm.service.APIService;
import com.example.reporttdm.service.RetrofitHelper;

import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KBarangAdapter extends RecyclerView.Adapter<KBarangAdapter.HolderData> {

    private List<KategoriBarang> kategoriBarangList;
    private Context context;

    public KBarangAdapter(List<KategoriBarang> kategoriBarangList, Context context) {
        this.kategoriBarangList = kategoriBarangList;
        this.context = context;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kategori, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        KategoriBarang kategoriBarang = kategoriBarangList.get(position);
        holder.txNama.setText(kategoriBarang.getNama());
        holder.id = kategoriBarang.getId();
    }

    @Override
    public int getItemCount() {
        return kategoriBarangList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txNama, btnHapus;
        public String id;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txNama = (TextView) itemView.findViewById(R.id.tx_namakb);
            btnHapus = (TextView) itemView.findViewById(R.id.btn_hapus_kb);
            btnHapus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            switch (v.getId()){
                case R.id.btn_hapus_kb:
                    Toast.makeText(context,"Harap tunggu...",Toast.LENGTH_SHORT).show();
                    deleteKategori(id);
                    break;
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        kategoriBarangList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<KategoriBarang> list) {
        kategoriBarangList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<KategoriBarang> list){
        kategoriBarangList = list;
        notifyDataSetChanged();
    }

    public void deleteKategori(String id) {
        APIService api = RetrofitHelper.getClient().create(APIService.class);
        Call<InsertResponse> call = api.deleteKategori(id);
        System.out.println("masuk");
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                System.out.println("re : " + response.body().getStatus_code());
                if (response.body().getStatus_code().equals("1")) {
                    Toast.makeText(context, "Delete kategori berhasil", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Delete kategori gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(context, "Harap periksa koneksi internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
