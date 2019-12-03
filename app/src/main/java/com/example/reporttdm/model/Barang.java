package com.example.reporttdm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class Barang implements Parcelable {

    private String id;
    private String id_kategori;
    private String id_toko;
    private String kode;
    private String nama;
    private String harga;
    private String stok;
    private String satuan;
    private String harga_jual;
    private String diskon;
    private String pajak;
    private String harga_asli;
    private String image;
    //private static int barangIdCounter = 0;


    public Barang(String id, String id_kategori, String id_toko, String kode, String nama, String harga, String stok, String satuan, String harga_jual, String diskon, String pajak, String harga_asli, String image) {
        this.id = id;
        this.id_kategori = id_kategori;
        this.id_toko = id_toko;
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.satuan = satuan;
        this.harga_jual = harga_jual;
        this.diskon = diskon;
        this.pajak = pajak;
        this.harga_asli = harga_asli;
        this.image = image;
    }

    public Barang(String id, String id_kategori, String id_toko, String kode, String nama, String harga, String stok, String satuan, String harga_jual, String diskon, String pajak) {
        this.id = id;
        this.id_kategori = id_kategori;
        this.id_toko = id_toko;
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.satuan = satuan;
        this.harga_jual = harga_jual;
        this.diskon = diskon;
        this.pajak = pajak;
    }

    public Barang(String id, String id_kategori, String id_toko, String kode, String nama, String harga, String stok, String harga_jual, String diskon) {
        this.id = id;
        this.id_kategori = id_kategori;
        this.id_toko = id_toko;
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.harga_jual = harga_jual;
        this.diskon = diskon;
    }

    public Barang() {
    }

    public Barang(String id, String harga, String stok, String nama) {
        this.id = id;
        this.harga = harga;
        this.stok = stok;
        this.nama = nama;
    }

    public Barang(String id, String nama, String harga, String stok, String harga_jual) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.harga_jual = harga_jual;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(String id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getId_toko() {
        return id_toko;
    }

    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga.replace(",","");
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getHarga_jual() {
        return harga_jual.replace(",","");
    }

    public void setHarga_jual(String harga_jual) {
        this.harga_jual = harga_jual;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getPajak() {
        return pajak;
    }

    public void setPajak(String pajak) {
        this.pajak = pajak;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getHarga_asli() {
        return harga_asli;
    }

    public void setHarga_asli(String harga_asli) {
        this.harga_asli = harga_asli;
    }

    public static Creator<Barang> getCREATOR() {
        return CREATOR;
    }

    protected Barang(Parcel in) {
        id = in.readString();
        id_kategori = in.readString();
        id_toko = in.readString();
        kode = in.readString();
        nama = in.readString();
        harga = in.readString();
        stok = in.readString();
        harga_jual = in.readString();
        diskon = in.readString() ;
        pajak = in.readString();
        satuan = in.readString();
        harga_asli = in.readString();
    }

    public static final Creator<Barang> CREATOR = new Creator<Barang>() {
        @Override
        public Barang createFromParcel(Parcel in) {
            return new Barang(in);
        }

        @Override
        public Barang[] newArray(int size) {
            return new Barang[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(id_kategori);
        parcel.writeString(id_toko);
        parcel.writeString(kode);
        parcel.writeString(nama);
        parcel.writeString(harga);
        parcel.writeString(stok);
        parcel.writeString(harga_jual);
        parcel.writeString(diskon);
        parcel.writeString(pajak);
        parcel.writeString(satuan);
        parcel.writeString(harga_asli);
    }

    public class SortbyStok implements Comparator<Barang>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(Barang a, Barang b)
        {
            return Integer.parseInt(a.stok) - Integer.parseInt(b.stok);
        }
    }
}
