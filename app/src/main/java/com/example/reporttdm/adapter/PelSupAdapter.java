package com.example.reporttdm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.activity.BayarPembelianActivity;
import com.example.reporttdm.activity.ListPelSupActivity;
import com.example.reporttdm.activity.PelSupSelectedActivity;
import com.example.reporttdm.model.PelSup;

import java.util.List;

public class PelSupAdapter extends RecyclerView.Adapter<PelSupAdapter.HolderData> {

    private List<PelSup> pelSupList;
    private Activity activity;
    private String jenis;

    public PelSupAdapter(List<PelSup> pelSupList, Activity activity, String jenis) {
        this.pelSupList = pelSupList;
        this.activity = activity;
        this.jenis = jenis;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelsup, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        PelSup pelSup = pelSupList.get(position);
        holder.txNick.setText(pelSup.getNama().substring(0,1));
        holder.txNama.setText(pelSup.getNama().toString());
        holder.txNoTelp.setText(pelSup.getTelephone());
        holder.id = pelSup.getId();
        holder.tipe = pelSup.getTipe();
        holder.alamat = pelSup.getAlamat();
        holder.pos = String.valueOf(position);
    }

    @Override
    public int getItemCount() {
        return pelSupList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txNama, txNoTelp, txNick;
        String id,tipe,alamat,pos;

        public HolderData(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txNama = (TextView)itemView.findViewById(R.id.tx_namaps);
            txNoTelp = (TextView)itemView.findViewById(R.id.tx_notelpps);
            txNick = (TextView)itemView.findViewById(R.id.tx_nickps);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            if (jenis.equals("0")||jenis.equals("1")){
                Intent intent = new Intent(context, PelSupSelectedActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("nama",txNama.getText().toString());
                intent.putExtra("alamat",alamat);
                intent.putExtra("notelp",txNoTelp.getText().toString());
                intent.putExtra("tipe",tipe);
                intent.putExtra("flag","0");
                context.startActivity(intent);
            }else if (jenis.equals("3")){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",String.valueOf(pos));
                returnIntent.putExtra("id_pelsup",id);
                returnIntent.putExtra("nama",txNama.getText().toString());
                ((ListPelSupActivity)activity).setResult(Activity.RESULT_OK,returnIntent);
                ((ListPelSupActivity)activity).setFinish();
            }else if(jenis.equals("4")){
                ((BayarPembelianActivity)activity).setSupplier(Integer.parseInt(pos));
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        pelSupList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<PelSup> list) {
        pelSupList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<PelSup> list){
        pelSupList = list;
        notifyDataSetChanged();
    }

}
