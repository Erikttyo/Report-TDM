package com.example.reporttdm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.activity.HistoryTransaksiActivity;
import com.example.reporttdm.model.HistoryTransaksi;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LaporanTransaksiAdapter extends RecyclerView.Adapter<LaporanTransaksiAdapter.HolderData>{

    private ArrayList<HistoryTransaksi> historyTransaksi;
    private Context context;

    public LaporanTransaksiAdapter(ArrayList<HistoryTransaksi> historyTransaksi, Context context) {
        this.historyTransaksi = historyTransaksi;
        this.context = context;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan_transaksi, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        HistoryTransaksi historyTransaksi = this.historyTransaksi.get(position);
        if (historyTransaksi.getHutpit().equals("2")){
            holder.txSJumlah.setText("Tunai : ");
            holder.txJumlah.setText("Rp "+
            doubleToStringNoDecimal(Double.parseDouble(historyTransaksi.getJual())));
        }else if(historyTransaksi.getHutpit().equals("0")){
            holder.txSJumlah.setText("Piutang : ");
            holder.txJumlah.setText("Rp "+
                    doubleToStringNoDecimal(Double.parseDouble(historyTransaksi.getJual())));
        }else if (historyTransaksi.equals("1")){
            holder.txSJumlah.setText("Hutang : ");
            holder.txJumlah.setText("Rp "+
                    doubleToStringNoDecimal(Double.parseDouble(historyTransaksi.getModal())));
        }
        holder.txStatus.setText(
                (historyTransaksi.getStatus().equals("1") || historyTransaksi.getStatus().equals("2")) ? "Lunas":"Belum Lunas");
        holder.txTgl.setText(historyTransaksi.getTanggal());
        holder.id = historyTransaksi.getId();
    }

    @Override
    public int getItemCount() {
        return historyTransaksi.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txSJumlah, txJumlah, txStatus, txTgl;
        public String id;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txSJumlah = (TextView) itemView.findViewById(R.id.tx_sjumlah_laporan);
            txJumlah = (TextView) itemView.findViewById(R.id.tx_jumlah_laporan);
            txStatus = (TextView) itemView.findViewById(R.id.tx_status_laporan);
            txTgl = (TextView) itemView.findViewById(R.id.tx_tgl_laporan);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, HistoryTransaksiActivity.class);
            intent.putExtra("id_transaksi",id);
            intent.putExtra("sjumlah",txSJumlah.getText().toString());
            intent.putExtra("jumlah",txJumlah.getText().toString());
            intent.putExtra("status",txStatus.getText().toString());
            intent.putExtra("tanggal",txTgl.getText().toString());
            context.startActivity(intent);
        }
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
    }

    public void updateList(ArrayList<HistoryTransaksi> list){
        historyTransaksi = list;
        notifyDataSetChanged();
    }

}
