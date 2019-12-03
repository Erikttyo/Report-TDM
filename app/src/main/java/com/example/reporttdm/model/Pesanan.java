package com.example.reporttdm.model;

public class Pesanan {

    private String id;
    private String id_transaksi;
    private String nama_pelanggan;
    private String meja;
    private String jumlah_orang;
    private String keterangan;

    public Pesanan(String id, String id_transaksi, String nama_pelanggan, String meja, String jumlah_orang, String keterangan) {
        this.id = id;
        this.id_transaksi = id_transaksi;
        this.nama_pelanggan = nama_pelanggan;
        this.meja = meja;
        this.jumlah_orang = jumlah_orang;
        this.keterangan = keterangan;
    }

    // Getter Methods

    public String getId() {
        return id;
    }

    public String getId_transaksi() {
        return id_transaksi;
    }

    public String getNama_pelanggan() {
        return nama_pelanggan;
    }

    public String getMeja() {
        return meja;
    }

    public String getJumlah_orang() {
        return jumlah_orang;
    }

    public String getKeterangan() {
        return keterangan;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setId_transaksi(String id_transaksi) {
        this.id_transaksi = id_transaksi;
    }

    public void setNama_pelanggan(String nama_pelanggan) {
        this.nama_pelanggan = nama_pelanggan;
    }

    public void setMeja(String meja) {
        this.meja = meja;
    }

    public void setJumlah_orang(String jumlah_orang) {
        this.jumlah_orang = jumlah_orang;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

}
