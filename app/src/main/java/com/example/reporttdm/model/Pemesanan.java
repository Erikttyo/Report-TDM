package com.example.reporttdm.model;

import java.util.ArrayList;
import java.util.List;

public class Pemesanan {

    String id_toko;
    String modal;
    String jual;
    String jumlah;
    String tipe;
    String cash;
    String keterangan;
    String nama_pesanan;
    String nama_pemesan;
    String meja;
    String orang;
    String ket;
    List<Barang> barangList = new ArrayList<Barang>();

    public Pemesanan(String id_toko, String modal, String jual, String jumlah, String tipe, String cash, String keterangan, String nama_pesanan, String nama_pemesan, String meja, String orang, String ket, List<Barang> barangList) {
        this.id_toko = id_toko;
        this.modal = modal;
        this.jual = jual;
        this.jumlah = jumlah;
        this.tipe = tipe;
        this.cash = cash;
        this.keterangan = keterangan;
        this.nama_pesanan = nama_pesanan;
        this.nama_pemesan = nama_pemesan;
        this.meja = meja;
        this.orang = orang;
        this.ket = ket;
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

    public String getNama_pesanan() {
        return nama_pesanan;
    }

    public void setNama_pesanan(String nama_pesanan) {
        this.nama_pesanan = nama_pesanan;
    }

    public String getNama_pemesan() {
        return nama_pemesan;
    }

    public void setNama_pemesan(String nama_pemesan) {
        this.nama_pemesan = nama_pemesan;
    }

    public String getMeja() {
        return meja;
    }

    public void setMeja(String meja) {
        this.meja = meja;
    }

    public String getOrang() {
        return orang;
    }

    public void setOrang(String orang) {
        this.orang = orang;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public List<Barang> getBarangList() {
        return barangList;
    }

    public void setBarangList(List<Barang> barangList) {
        this.barangList = barangList;
    }
}
