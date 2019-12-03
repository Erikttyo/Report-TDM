package com.example.reporttdm.model;

import java.util.List;

public class Pembelian {
    String id_toko;
    String modal;
    String jual;
    String jumlah;
    String tipe;
    String cash;
    String keterangan;
    String id_pelsup;
    String jatuh_tempo;
    String nama_petugas;
    List<Barang> barangList;

    public Pembelian(String id_toko, String modal, String jual, String jumlah, String tipe, String cash, String keterangan, String id_pelsup, String jatuh_tempo, String nama_petugas, List<Barang> barangList) {
        this.id_toko = id_toko;
        this.modal = modal;
        this.jual = jual;
        this.jumlah = jumlah;
        this.tipe = tipe;
        this.cash = cash;
        this.keterangan = keterangan;
        this.id_pelsup = id_pelsup;
        this.jatuh_tempo = jatuh_tempo;
        this.nama_petugas = nama_petugas;
        this.barangList = barangList;
    }

    public String getId_toko() {
        return id_toko;
    }

    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }

    public String getModal() {
        return modal.replace(",","");
    }

    public void setModal(String modal) {
        this.modal = modal;
    }

    public String getJual() {
        return jual.replace(",","");
    }

    public void setJual(String jual) {
        this.jual = jual;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getId_pelsup() {
        return id_pelsup;
    }

    public void setId_pelsup(String id_pelsup) {
        this.id_pelsup = id_pelsup;
    }

    public String getJatuh_tempo() {
        return jatuh_tempo;
    }

    public void setJatuh_tempo(String jatuh_tempo) {
        this.jatuh_tempo = jatuh_tempo;
    }

    public String getNama_petugas() {
        return nama_petugas;
    }

    public void setNama_petugas(String nama_petugas) {
        this.nama_petugas = nama_petugas;
    }

    public List<Barang> getBarangList() {
        return barangList;
    }

    public void setBarangList(List<Barang> barangList) {
        this.barangList = barangList;
    }
}
