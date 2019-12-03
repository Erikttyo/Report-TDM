package com.example.reporttdm.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.activity.KartuHutPitActivity;
import com.example.reporttdm.model.HutPit;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HutPitAdapter extends RecyclerView.Adapter<HutPitAdapter.HolderData> {

    private List<HutPit> hutPitList;
    private String jenis;

    public HutPitAdapter(List<HutPit> hutPitList, String jenis) {
        this.hutPitList = hutPitList;
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
        HutPit hutPit = hutPitList.get(position);
        holder.txTanggal.setText(hutPit.getTanggal());
        if (jenis.equals("1")) {
            holder.txBayar.setText("Rp " + doubleToStringNoDecimal(Double.parseDouble(hutPit.getModal())));
            holder.total = hutPit.getModal();
            holder.sisa = hutPit.getModal();
        }else{
            holder.txBayar.setText("Rp " + doubleToStringNoDecimal(Double.parseDouble(hutPit.getJual())));
            holder.total = hutPit.getJual();
            holder.sisa = hutPit.getJual();
        }

        if (hutPit.getStatus().equals("1")){
            holder.txKet.setText(hutPit.getNama()+" (LUNAS)");
        }else{
            holder.txKet.setText(hutPit.getNama());
        }

        holder.jenisHutPit = jenis;

        holder.id = hutPit.getId_hutpit();
        holder.jatuhTempo = hutPit.getJatuh_tempo();
        holder.dibayar = "0";

    }

    @Override
    public int getItemCount() {
        return hutPitList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txTanggal, txKet,txBayar;
        public String id, jenisHutPit, jatuhTempo, total, sisa, dibayar;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txTanggal = (TextView) itemView.findViewById(R.id.tx_tglhp);
            txKet = (TextView) itemView.findViewById(R.id.tx_kethp);
            txBayar = (TextView) itemView.findViewById(R.id.tx_bayarhp);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), KartuHutPitActivity.class);
            intent.putExtra("jenis",jenisHutPit);
            intent.putExtra("id_hutpit",id);
            intent.putExtra("jatuh_tempo", jatuhTempo);
            intent.putExtra("total", total);
            intent.putExtra("sisa", sisa);
            intent.putExtra("dibayar", dibayar);
            intent.putExtra("nama", txKet.getText().toString());
            v.getContext().startActivity(intent);
        }
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

    // Clean all elements of the recycler
    public void clear() {
        hutPitList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<HutPit> list) {
        hutPitList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<HutPit> list){
        hutPitList = list;
        notifyDataSetChanged();
    }
}
