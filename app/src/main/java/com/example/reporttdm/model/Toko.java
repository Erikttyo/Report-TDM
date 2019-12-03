package com.example.reporttdm.model;

public class Toko {
    private String id;
    private String nama;
    private String nama_pemilik;
    private String lokasi;
    private String telephone;
    private String pajak;
    private String diskon;


    // Getter Methods

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNama_pemilik() {
        return nama_pemilik;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getPajak() {
        return pajak;
    }

    public String getDiskon() {
        return diskon;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setNama_pemilik(String nama_pemilik) {
        this.nama_pemilik = nama_pemilik;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setPajak(String pajak) {
        this.pajak = pajak;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }
}
