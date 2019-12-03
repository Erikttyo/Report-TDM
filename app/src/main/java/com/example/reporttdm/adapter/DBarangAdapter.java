package com.example.reporttdm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.reporttdm.R;
import com.example.reporttdm.activity.DBarangActivity;
import com.example.reporttdm.activity.DBarangSelectedActivity;
import com.example.reporttdm.model.Barang;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DBarangAdapter extends RecyclerView.Adapter<DBarangAdapter.HolderData> {
    private List<Barang> barangList;
    private Activity activity;
    private int tipe;

    public DBarangAdapter(List<Barang> barangList, Activity activity, int tipe) {
        this.barangList = barangList;
        this.activity = activity;
        this.tipe = tipe;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v =
                tipe==0 ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dbarang2, parent, false) :
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        Barang barang = barangList.get(position);
        if (tipe!=0){
            holder.imgBarang.setVisibility(View.GONE);
            holder.imgBarang2.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .error(R.drawable.ic_picture)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(activity.getApplicationContext())
                    .load(barang.getImage())
                    .apply(options)
                    .into(holder.imgBarang2);
        }else {
            holder.imgBarang.setVisibility(View.VISIBLE);
            holder.imgBarang2.setVisibility(View.GONE);
        }
        if (tipe==3){
            holder.imgBarang2.setVisibility(View.GONE);
            holder.txNama.setText(barang.getNama());
            holder.txStok.setText(barang.getStok());
            holder.txHarga.setText(barang.getKode());
            holder.txBeli.setText(barang.getSatuan());
        }else if (tipe==4){
            holder.imgBarang2.setVisibility(View.GONE);
            holder.txNama.setText(barang.getNama());
            holder.txStok.setVisibility(View.GONE);
            holder.txBeli.setText(barang.getStok());
            holder.txHarga.setText("Rp " + doubleToStringNoDecimal(Double.parseDouble(barang.getHarga_jual())));
        } else {
            holder.txNama.setText(barang.getNama());
            holder.txStok.setText(barang.getStok());
            holder.txHarga.setText("Rp. " + doubleToStringNoDecimal(Double.parseDouble(barang.getHarga_asli())));
        }
        if (tipe==0){
            holder.txBeli.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .error(R.drawable.ic_picture)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(activity.getApplicationContext())
                    .load(barang.getImage())
                    .apply(options)
                    .into(holder.imgBarang);
        }else if (tipe==1){
            holder.txBeli.setVisibility(View.VISIBLE);
        }
        holder.harga = barang.getHarga_jual();
        holder.pos = position;
        holder.kode = barang.getKode();
        holder.kategori = barang.getId_kategori();
        holder.hd = barang.getHarga();
        holder.diskon = barang.getDiskon();
        holder.id_barang = barang.getId();
        holder.satuan = barang.getSatuan();
        holder.image = barang.getImage();
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        int pos;
        TextView txNama, txHarga, txBeli, txStok;
        ImageView imgBarang, imgBarang2;
        String kode,kategori,hd,diskon,id_barang,harga,satuan,image;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txNama = (TextView)itemView.findViewById(R.id.tx_namatrans);
            txHarga = (TextView)itemView.findViewById(R.id.tx_hargatrans);
            txBeli = (TextView)itemView.findViewById(R.id.tx_belitrans);
            txStok = (TextView)itemView.findViewById(R.id.tx_stoktrans);
            imgBarang = (ImageView) itemView.findViewById(R.id.img_dbarang);
            imgBarang2 = (ImageView) itemView.findViewById(R.id.img_dbarang2);

        }

        @Override
        public void onClick(View v) {
            if (tipe==1){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",String.valueOf(pos));
                returnIntent.putExtra("id_barang",id_barang);
                returnIntent.putExtra("nama",txNama.getText().toString());
                returnIntent.putExtra("kode",kode);
                returnIntent.putExtra("harga",harga);
                ((DBarangActivity)activity).setResult(Activity.RESULT_OK,returnIntent);
                ((DBarangActivity)activity).setFinish();
            }else if (tipe!=4){
                Context context = v.getContext();
                Intent intent = new Intent(context, DBarangSelectedActivity.class);
                String hj = txHarga.getText().toString().replace("Rp. ","");
                intent.putExtra("tipe","1");
                intent.putExtra("nama",txNama.getText().toString());
                intent.putExtra("stok",txStok.getText().toString());
                intent.putExtra("kode",kode);
                intent.putExtra("hd",hd.replace("Rp. ",""));
                intent.putExtra("hj",hj.replace(".",","));
                intent.putExtra("diskon",diskon);
                intent.putExtra("kategori",kategori);
                intent.putExtra("id_barang",id_barang);
                intent.putExtra("satuan",satuan);
                intent.putExtra("image",image);
                context.startActivity(intent);
            }
        }
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###");
        return formatter.format(d).replace(",",".");
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
