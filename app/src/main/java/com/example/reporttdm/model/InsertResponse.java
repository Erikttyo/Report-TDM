package com.example.reporttdm.model;

public class InsertResponse {

    String status_code;
    String status;
    int id_transaksi;

    public InsertResponse(String status_code, String status, int id_transaksi) {
        this.status_code = status_code;
        this.status = status;
        this.id_transaksi = id_transaksi;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId_transaksi() {
        return id_transaksi;
    }

    public void setId_transaksi(int id_transaksi) {
        this.id_transaksi = id_transaksi;
    }
}
