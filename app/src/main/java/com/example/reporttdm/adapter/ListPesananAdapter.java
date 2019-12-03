package com.example.reporttdm.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.fragment.TransaksiFragment;
import com.example.reporttdm.model.Pesanan;

import java.util.List;

public class ListPesananAdapter extends RecyclerView.Adapter<ListPesananAdapter.HolderData> {

    private List<Pesanan> pesananList;
    private Fragment fragment;

    public ListPesananAdapter(List<Pesanan> pesananList, Fragment fragment) {
        this.pesananList = pesananList;
        this.fragment = fragment;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kategori, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        Pesanan pesanan = pesananList.get(position);
        holder.txNama.setText(pesanan.getNama_pelanggan());
        holder.pos = position;
        holder.id_transaksi = pesanan.getId_transaksi();
    }

    @Override
    public int getItemCount() {
        return pesananList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txNama, btnHapus;
        public int pos;
        public String id_transaksi;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txNama = (TextView) itemView.findViewById(R.id.tx_namakb);
            btnHapus = (TextView) itemView.findViewById(R.id.btn_hapus_kb);
            btnHapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TransaksiFragment)fragment).showDeleteConfirm(id_transaksi);
                }
            });
        }

        @Override
        public void onClick(View v) {
            ((TransaksiFragment)fragment).addPesanan(pos);
        }
    }

}
