package com.example.reporttdm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaksi implements Parcelable {
    String id, nama, jumlah, modal, stok, jual, tipe, asli, image;
    //private static int transaksiIdCounter = 0;

    public Transaksi(String id, String nama, String jumlah, String modal, String stok, String jual, String tipe) {
        //this.id = String.valueOf(transaksiIdCounter++);
        this.id = id;
        this.nama = nama;
        this.jumlah = jumlah;
        this.modal = modal;
        this.stok = stok;
        this.jual = jual;
        this.tipe = tipe;
    }

    public Transaksi(String id, String nama, String jumlah, String modal, String stok, String asli, String jual, String tipe) {
        //this.id = String.valueOf(transaksiIdCounter++);
        this.id = id;
        this.nama = nama;
        this.jumlah = jumlah;
        this.modal = modal;
        this.stok = stok;
        this.jual = jual;
        this.tipe = tipe;
        this.asli = asli;
    }

    public Transaksi(String id, String nama, String jumlah, String modal, String stok, String asli, String jual, String tipe, String image) {
        //this.id = String.valueOf(transaksiIdCounter++);
        this.id = id;
        this.nama = nama;
        this.jumlah = jumlah;
        this.modal = modal;
        this.stok = stok;
        this.jual = jual;
        this.tipe = tipe;
        this.asli = asli;
        this.image = image;
    }

    public Transaksi() {
    }

    public Transaksi(String id) {
        this.id = id;
    }

    public Transaksi(String id, String jumlah) {
        this.id = id;
        this.jumlah = jumlah;
    }

    protected Transaksi(Parcel in) {
        id = in.readString();
        nama = in.readString();
        jumlah = in.readString();
        modal = in.readString();
        stok = in.readString();
        jual = in.readString();
        tipe = in.readString();
        asli = in.readString();
        image = in.readString();
    }

    public static final Creator<Transaksi> CREATOR = new Creator<Transaksi>() {
        @Override
        public Transaksi createFromParcel(Parcel in) {
            return new Transaksi(in);
        }

        @Override
        public Transaksi[] newArray(int size) {
            return new Transaksi[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getModal() {
        return modal.replace(",","");
    }

    public void setModal(String modal) {
        this.modal = modal;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getJual() {
        return jual.replace(",","");
    }

    public void setJual(String jual) {
        this.jual = jual;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getAsli() {
        return asli;
    }

    public void setAsli(String asli) {
        this.asli = asli;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nama);
        parcel.writeString(jumlah);
        parcel.writeString(modal);
        parcel.writeString(stok);
        parcel.writeString(jual);
        parcel.writeString(tipe);
        parcel.writeString(asli);
        parcel.writeString(image);
    }
}
