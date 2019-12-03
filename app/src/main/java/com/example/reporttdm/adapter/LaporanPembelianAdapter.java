package com.example.reporttdm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.model.ResultPembelianItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LaporanPembelianAdapter extends RecyclerView.Adapter<LaporanPembelianAdapter.HolderData> {

    private List<ResultPembelianItem> resultPembelianItemList;

    public LaporanPembelianAdapter(List<ResultPembelianItem> resultPembelianItemList) {
        this.resultPembelianItemList = resultPembelianItemList;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        ResultPembelianItem resultPembelianItem = resultPembelianItemList.get(position);
        holder.txNama.setText("Nama : "+resultPembelianItem.getNama_barang());
        holder.txTgl.setText(resultPembelianItem.getTanggal());
        holder.txKode.setText(resultPembelianItem.getKode_barang());
        holder.txJumlah.setText("Jumlah : "+resultPembelianItem.getJumlah());
        int laba = 0;
        if (!resultPembelianItem.getHarga_jual().equals("")) {
            laba = (Integer.parseInt(resultPembelianItem.getJumlah()) *
                    Integer.parseInt(resultPembelianItem.getHarga_jual())) -
                    (Integer.parseInt(resultPembelianItem.getJumlah()) *
                            Integer.parseInt(resultPembelianItem.getHarga_dasar()));
            holder.txLaba.setText("Laba : "+doubleToStringNoDecimal(laba));
        }else{
            holder.txLaba.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return resultPembelianItemList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txNama, txTgl, txKode, txJumlah, txLaba;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txNama = (TextView) itemView.findViewById(R.id.tx_nama_laporan);
            txTgl = (TextView)itemView.findViewById(R.id.tx_tgl_laporan);
            txKode = (TextView)itemView.findViewById(R.id.tx_kode_laporan);
            txJumlah = (TextView)itemView.findViewById(R.id.tx_jumlah_laporan);
            txLaba = (TextView)itemView.findViewById(R.id.tx_laba_laporan);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            switch (v.getId()){

            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        resultPembelianItemList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<ResultPembelianItem> list) {
        resultPembelianItemList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<ResultPembelianItem> list){
        resultPembelianItemList = list;
        notifyDataSetChanged();
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

}
