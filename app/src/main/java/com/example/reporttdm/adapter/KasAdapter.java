package com.example.reporttdm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.model.ResultKas;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KasAdapter extends RecyclerView.Adapter<KasAdapter.HolderData> {

    private List<ResultKas> resultKasList;
    private Context context;

    public KasAdapter(List<ResultKas> resultKasList, Context context) {
        this.resultKasList = resultKasList;
        this.context = context;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kas, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        ResultKas kas = resultKasList.get(position);
        holder.txNama.setText(kas.getNama());
        holder.txTanggal.setText(kas.getTanggal());
        if (kas.getKeterangan().equals("Mutasi")){
            holder.txKeterangan.setTextColor(kas.getJenis().equals("1") ? Color.GREEN : Color.RED);
        }
        holder.txKeterangan.setText(kas.getKeterangan());
        holder.txNilai.setTextColor(kas.getJenis().equals("0") ? Color.RED : Color.BLACK);
        holder.txNilai.setText("Rp " + doubleToStringNoDecimal(Double.parseDouble(kas.getNominal())));
        holder.id = kas.getId();
    }

    @Override
    public int getItemCount() {
        return resultKasList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txNama, txNilai, txTanggal, txKeterangan;
        public String id;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txNama = (TextView) itemView.findViewById(R.id.tx_nama_kas);
            txNilai = (TextView) itemView.findViewById(R.id.tx_nilai_kas);
            txTanggal = (TextView) itemView.findViewById(R.id.tx_tanggal_kas);
            txKeterangan = (TextView) itemView.findViewById(R.id.tx_ket_kas);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();

        }

    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

    // Clean all elements of the recycler
    public void clear() {
        resultKasList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<ResultKas> list) {
        resultKasList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<ResultKas> list) {
        resultKasList = list;
        notifyDataSetChanged();
    }

}
