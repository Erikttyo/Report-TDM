package com.example.reporttdm.model;

import java.util.ArrayList;
import java.util.List;

public class GetKategoriResponse {
    String status_code;
    String status;
    List<KategoriBarang> result_kategori = new ArrayList<KategoriBarang>();

    public GetKategoriResponse(String status_code, String status, List<KategoriBarang> result_kategori) {
        this.status_code = status_code;
        this.status = status;
        this.result_kategori = result_kategori;
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

    public List<KategoriBarang> getResult_kategori() {
        return result_kategori;
    }

    public void setResult_kategori(List<KategoriBarang> result_kategori) {
        this.result_kategori = result_kategori;
    }
}
