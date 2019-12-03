package com.example.reporttdm.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reporttdm.R;
import com.example.reporttdm.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.HolderData> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelsup, parent, false);
        HolderData holderData = new HolderData(v);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, int position) {
        User user = userList.get(position);
        holder.txNick.setText(user.getNama().substring(0,1));
        holder.txNama.setText(user.getNama().toString());
        if (user.getTipe().equals("2")){
            holder.txNoTelp.setText("Kasir");
        }else if (user.getTipe().equals("3")){
            holder.txNoTelp.setText("Admin");
        }
        holder.id = user.getId();
        holder.tipe = user.getTipe();
        holder.alamat = user.getAlamat();
        holder.email = user.getEmail();
        holder.notelp = user.getTelephone();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txNama, txNoTelp, txNick;
        String id,tipe,alamat,email,notelp;

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
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View dialogView = inflater.inflate(R.layout.dialog_user, null);
                dialogBuilder.setView(dialogView);

                TextView txTitle = (TextView) dialogView.findViewById(R.id.tx_dialog_title);
                final TextView txNama = (TextView) dialogView.findViewById(R.id.tx_nama_pu);
                final TextView txEmail = (TextView) dialogView.findViewById(R.id.tx_email_pu);
                final TextView txNoTelp = (TextView) dialogView.findViewById(R.id.tx_notelp_pu);
                final TextView txAlamat = (TextView) dialogView.findViewById(R.id.tx_alamat_pu);
                final TextView txTipe = (TextView) dialogView.findViewById(R.id.tx_tipe_pu);
                txNama.setText("Nama : "+this.txNama.getText().toString());
                txEmail.setText("Email : "+this.email);
                txAlamat.setText("Alamat : "+this.alamat);
                txTipe.setText("Tipe : "+this.txNoTelp.getText().toString());
                txNoTelp.setText("No. Telp : "+this.notelp);

                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }

    }

    // Clean all elements of the recycler
    public void clear() {
        userList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        userList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<User> list){
        userList = list;
        notifyDataSetChanged();
    }

}
