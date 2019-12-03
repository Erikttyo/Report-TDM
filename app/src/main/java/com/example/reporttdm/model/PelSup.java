package com.example.reporttdm.model;

public class PelSup {
    private String id;
    private String nama;
    private String telephone;
    private String alamat;
    private String tipe;
    private String id_toko;
    private String id_pelsup;


    // Getter Methods

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getTipe() {
        return tipe;
    }

    public String getId_toko() {
        return id_toko;
    }

    public String getId_pelsup() {
        return id_pelsup;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }

    public void setId_pelsup(String id_pelsup) {
        this.id_pelsup = id_pelsup;
    }
}
