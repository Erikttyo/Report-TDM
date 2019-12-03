package com.example.reporttdm.model;

import java.util.ArrayList;

public class GetTransaksiResponse {
    private String status_code;
    private String status;
    private ArrayList < HistoryTransaksi > result_transaksi = new ArrayList< HistoryTransaksi >();


    // Getter Methods

    public String getStatus_code() {
        return status_code;
    }

    public String getStatus() {
        return status;
    }

    // Setter Methods

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<HistoryTransaksi> getResult_transaksi() {
        return result_transaksi;
    }

    public void setResult_transaksi(ArrayList<HistoryTransaksi> result_transaksi) {
        this.result_transaksi = result_transaksi;
    }
}
