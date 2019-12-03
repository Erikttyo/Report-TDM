package com.example.reporttdm.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.reporttdm.R;
import com.example.reporttdm.fragment.TransaksiFragment;
import com.example.reporttdm.model.Transaksi;

import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.HolderData> {
    private List<Transaksi> transaksiList;
    private Fragment fragment;
    private int mode = 0;
    private int tipe = 0;

    public TransaksiAdapter(List<Transaksi> transaksiList, Fragment fragment) {
        this.transaksiList = transaksiList;
        this.fragment = fragment;
    }

    public TransaksiAdapter(List<Transaksi> transaksiList, Fragment fragment, int tipe) {
        this.transaksiList = transaksiList;
        this.fragment = fragment;
        this.tipe = tipe;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (mode == 0) ?
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false) :
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dbarang2, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        Transaksi transaksi = transaksiList.get(position);
        holder.pos = position;
        holder.id = transaksi.getId();
        holder.txNama.setText(transaksi.getNama());
        holder.txStok.setText(transaksi.getStok());
        holder.txHarga.setText(transaksi.getAsli());
        holder.txBeli.setText(transaksi.getJumlah());
        if (mode==0){
            holder.txBeli.setVisibility(View.VISIBLE);
            holder.imgPhoto.setVisibility(View.GONE);
            holder.imgPhoto2.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .error(R.drawable.ic_picture)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(fragment.getContext())
                    .load(transaksi.getImage())
                    .apply(options)
                    .into(holder.imgPhoto2);
        }else{
            holder.txBeli.setVisibility(View.VISIBLE);
            holder.imgPhoto.setVisibility(View.VISIBLE);
            holder.imgPhoto2.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .error(R.drawable.ic_picture)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(fragment.getContext())
                    .load(transaksi.getImage())
                    .apply(options)
                    .into(holder.imgPhoto);
        }
        if (tipe==1){
            holder.txBeli.setVisibility(View.GONE);
            holder.txStok.setVisibility(View.GONE);
        }else{
            holder.txBeli.setVisibility(View.VISIBLE);
            holder.txStok.setVisibility(View.VISIBLE);
        }
        holder.jum = 0;
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        public  TextView txBeli, txNama, txHarga, txStok;
        public ImageView imgPhoto, imgPhoto2;
        int jum,pos;
        String id;

        public HolderData(View itemView) {
            super(itemView);
            jum = 0;
            txBeli = (TextView)itemView.findViewById(R.id.tx_belitrans);
            txNama = (TextView)itemView.findViewById(R.id.tx_namatrans);
            txHarga = (TextView)itemView.findViewById(R.id.tx_hargatrans);
            txStok = (TextView)itemView.findViewById(R.id.tx_stoktrans);
            imgPhoto = (ImageView) itemView.findViewById(R.id.img_dbarang);
            imgPhoto2 = (ImageView) itemView.findViewById(R.id.img_dbarang2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Context context = v.getContext();
//            Intent intent = new Intent(context, NoInvoiceActivity.class);
//            context.startActivity(intent);
            if(fragment instanceof TransaksiFragment){
                if (!txStok.getText().equals("0")){
                    jum = jum + 1;
                    txBeli.setText(String.valueOf(jum));
                    int stok = Integer.parseInt(txStok.getText().toString()) - 1;
                    txStok.setText(String.valueOf(stok));
                    if (stok>=0) {
                        ((TransaksiFragment) fragment).addTransaksi(pos);
                    }else{
                        Toast.makeText(v.getContext(),"Stok "+txNama.getText().toString()+" habis",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(v.getContext(),"Stok "+txNama.getText().toString()+" habis",Toast.LENGTH_SHORT).show();
                }
            }
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
