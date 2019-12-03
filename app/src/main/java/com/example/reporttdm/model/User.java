package com.example.reporttdm.model;

public class User {
    String id, id_toko, nama, email, telephone, alamat, password, tipe;
    //private static int userIdCounter = 0;


    public User(String id, String id_toko, String nama, String email, String telephone, String alamat, String password, String tipe) {
        this.id = id;
        this.id_toko = id_toko;
        this.nama = nama;
        this.email = email;
        this.telephone = telephone;
        this.alamat = alamat;
        this.password = password;
        this.tipe = tipe;
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

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
}
