package com.example.reporttdm.model;

import java.util.ArrayList;
import java.util.List;

public class GetBarangResponse {

    int status_code;
    String status;
    List<Barang> result_barang = new ArrayList<Barang>();

    public GetBarangResponse(int status_code, String status, List<Barang> result_barang) {
        this.status_code = status_code;
        this.status = status;
        this.result_barang = result_barang;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Barang> getResult_barang() {
        return result_barang;
    }

    public void setResult_barang(List<Barang> result_barang) {
        this.result_barang = result_barang;
    }
}
