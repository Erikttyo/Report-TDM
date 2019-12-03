package com.example.reporttdm.model;

public class Perangkat {
    String address;
    String nama;

    public Perangkat(String address, String nama) {
        this.address = address;
        this.nama = nama;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
