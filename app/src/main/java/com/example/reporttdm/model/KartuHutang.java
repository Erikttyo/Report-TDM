package com.example.reporttdm.model;

public class KartuHutang {
    private String id_hutpit;
    private String id_toko;
    private String id_pelsup;
    private String id_transaksi;
    private String jumlah;
    private String keterangan;
    private String jenis;
    private String jatuh_tempo;
    private String status;
    private String id;
    private String tanggal;
    private String nilai;
    private String nama;
    private String jual;
    private String modal;


    // Getter Methods

    public String getId_hutpit() {
        return id_hutpit;
    }

    public String getId_toko() {
        return id_toko;
    }

    public String getId_pelsup() {
        return id_pelsup;
    }

    public String getId_transaksi() {
        return id_transaksi;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getJenis() {
        return jenis;
    }

    public String getJatuh_tempo() {
        return jatuh_tempo;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getNilai() {
        return nilai.replace(",","");
    }

    public String getNama() {
        return nama;
    }

    public String getJual() {
        return jual.replace(",","");
    }

    public String getModal() {
        return modal.replace(",","");
    }

    // Setter Methods

    public void setId_hutpit(String id_hutpit) {
        this.id_hutpit = id_hutpit;
    }

    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }

    public void setId_pelsup(String id_pelsup) {
        this.id_pelsup = id_pelsup;
    }

    public void setId_transaksi(String id_transaksi) {
        this.id_transaksi = id_transaksi;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public void setJatuh_tempo(String jatuh_tempo) {
        this.jatuh_tempo = jatuh_tempo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setJual(String jual) {
        this.jual = jual;
    }
}
