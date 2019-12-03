package com.example.reporttdm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.model.KartuHutang;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KartuHutangAdapter extends RecyclerView.Adapter<KartuHutangAdapter.HolderData> {

    private List<KartuHutang> kartuHutangList;
    private String jenis;

    public KartuHutangAdapter(List<KartuHutang> kartuHutangList, String jenis) {
        this.kartuHutangList = kartuHutangList;
        this.jenis = jenis;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hutang_piutang, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        KartuHutang hutPit = kartuHutangList.get(position);
        holder.txTanggal.setText(hutPit.getTanggal());
        holder.txBayar.setText("Rp " + doubleToStringNoDecimal(Double.parseDouble(hutPit.getNilai())));
        holder.txKet.setText(hutPit.getNama());
        holder.id = hutPit.getId_hutpit();
    }

    @Override
    public int getItemCount() {
        return kartuHutangList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txTanggal, txKet,txBayar;
        public String id;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txTanggal = (TextView) itemView.findViewById(R.id.tx_tglhp);
            txKet = (TextView) itemView.findViewById(R.id.tx_kethp);
            txBayar = (TextView) itemView.findViewById(R.id.tx_bayarhp);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

    // Clean all elements of the recycler
    public void clear() {
        kartuHutangList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<KartuHutang> list) {
        kartuHutangList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<KartuHutang> list){
        kartuHutangList = list;
        notifyDataSetChanged();
    }

}
