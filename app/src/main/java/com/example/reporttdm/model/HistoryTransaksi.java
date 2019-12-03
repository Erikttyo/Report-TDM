package com.example.reporttdm.model;

public class HistoryTransaksi {
    private String id;
    private String id_toko;
    private String tanggal;
    private String modal;
    private String jual;
    private String jumlah;
    private String tipe;
    private String hutpit;
    private String status;


    // Getter Methods

    public String getId() {
        return id;
    }

    public String getId_toko() {
        return id_toko;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getModal() {
        return modal;
    }

    public String getJual() {
        return jual;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getTipe() {
        return tipe;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setModal(String modal) {
        this.modal = modal;
    }

    public void setJual(String jual) {
        this.jual = jual;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getHutpit() {
        return hutpit;
    }

    public void setHutpit(String hutpit) {
        this.hutpit = hutpit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
