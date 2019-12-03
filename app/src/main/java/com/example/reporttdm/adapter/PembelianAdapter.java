package com.example.reporttdm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.model.Barang;

import java.util.List;

public class PembelianAdapter extends RecyclerView.Adapter<PembelianAdapter.HolderData> {

    private List<Barang> barangList;

    public PembelianAdapter(List<Barang> barangList) {
        this.barangList = barangList;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pembelian, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        Barang barang = barangList.get(position);
        holder.txNama.setText(barang.getNama());
        holder.txHarga.setText(barang.getStok()+" x "+barang.getHarga()+" = "+(Integer.parseInt(barang.getStok())*Integer.parseInt(barang.getHarga())));
        holder.txStok.setText(barang.getStok());
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txNama, txHarga, txStok;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txNama = (TextView)itemView.findViewById(R.id.tx_namadb);
            txHarga = (TextView)itemView.findViewById(R.id.tx_hargadb);
            txStok = (TextView)itemView.findViewById(R.id.tx_stokdb);
        }

        @Override
        public void onClick(View v) {
//            Context context = v.getContext();
//            Intent intent = new Intent(context, DBarangSelectedActivity.class);
//            context.startActivity(intent);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        barangList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Barang> list) {
        barangList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<Barang> list){
        barangList = list;
        notifyDataSetChanged();
    }

}
