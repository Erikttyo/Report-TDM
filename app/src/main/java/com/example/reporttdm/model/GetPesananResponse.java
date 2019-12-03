package com.example.reporttdm.model;

import java.util.ArrayList;

public class GetPesananResponse {
    private String status_code;
    private String status;
    ArrayList< ResultPembelianItem > result_pembelian = new ArrayList < ResultPembelianItem > ();
    ArrayList < Pesanan > result_pesanan = new ArrayList < Pesanan > ();

    public GetPesananResponse(String status_code, String status, ArrayList<ResultPembelianItem> result_pembelian, ArrayList<Pesanan> result_pesanan) {
        this.status_code = status_code;
        this.status = status;
        this.result_pembelian = result_pembelian;
        this.result_pesanan = result_pesanan;
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

    public ArrayList<ResultPembelianItem> getResult_pembelian() {
        return result_pembelian;
    }

    public void setResult_pembelian(ArrayList<ResultPembelianItem> result_pembelian) {
        this.result_pembelian = result_pembelian;
    }

    public ArrayList<Pesanan> getResult_pesanan() {
        return result_pesanan;
    }

    public void setResult_pesanan(ArrayList<Pesanan> result_pesanan) {
        this.result_pesanan = result_pesanan;
    }
}
