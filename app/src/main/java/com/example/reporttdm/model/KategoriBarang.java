package com.example.reporttdm.model;

public class KategoriBarang {

    String id;
    String id_toko;
    String nama;
    //private static int kategoriIdCounter = 0;


    public KategoriBarang(String nama, String id, String id_toko) {
        this.nama = nama;
        this.id = id;
        this.id_toko = id_toko;
    }

    public KategoriBarang(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_toko() {
        return id_toko;
    }

    public void setId_toko(String id_toko) {
        this.id_toko = id_toko;
    }
}
