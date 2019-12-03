package com.example.reporttdm.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.activity.ListTransaksiActivity;
import com.example.reporttdm.model.Transaksi;

import java.util.List;

public class ListTransaksiAdapter extends RecyclerView.Adapter<ListTransaksiAdapter.HolderData> {

    private List<Transaksi> transaksiList;
    private Activity activity;

    public ListTransaksiAdapter(List<Transaksi> transaksiList, Activity activity) {
        this.transaksiList = transaksiList;
        this.activity = activity;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pembelian, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        Transaksi transaksi = transaksiList.get(position);
        int jum = Integer.parseInt(transaksi.getJumlah());
        holder.txJumlah.setText(String.valueOf(jum));
        holder.txNama.setText(transaksi.getNama());
        holder.txHarga.setText(transaksi.getJual());
        holder.posisi = position;
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txJumlah;
        TextView txNama, txHarga, txStok;
        int posisi;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txJumlah = (TextView)itemView.findViewById(R.id.tx_stokdb);
            txNama = (TextView)itemView.findViewById(R.id.tx_namadb);
            txHarga = (TextView)itemView.findViewById(R.id.tx_hargadb);
        }

        @Override
        public void onClick(View v) {
            ((ListTransaksiActivity)activity).showEditTransaksi(posisi, txNama.getText().toString(),
                    txHarga.getText().toString(), txJumlah.getText().toString());
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        transaksiList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Transaksi> list) {
        transaksiList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<Transaksi> list){
        transaksiList = list;
        notifyDataSetChanged();
    }

}
