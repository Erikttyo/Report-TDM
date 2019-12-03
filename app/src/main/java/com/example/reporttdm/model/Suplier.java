package com.example.reporttdm.model;

public class Suplier {
    String id, nama, telephone, alamat;
    //private static int suplierIdCounter = 0;

    public Suplier(String id, String nama, String telephone, String alamat) {
        //this.id = String.valueOf(suplierIdCounter++);
        this.nama = nama;
        this.telephone = telephone;
        this.alamat = alamat;
    }

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
