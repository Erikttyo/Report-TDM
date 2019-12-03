package com.example.reporttdm.model;

import java.util.ArrayList;
import java.util.List;

public class GetTokoResponse {

    int status_code;
    String status;
    List<Toko> result_toko = new ArrayList<Toko>();

    public GetTokoResponse(int status_code, String status, List<Toko> result_toko) {
        this.status_code = status_code;
        this.status = status;
        this.result_toko = result_toko;
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

    public List<Toko> getResult_toko() {
        return result_toko;
    }

    public void setResult_toko(List<Toko> result_toko) {
        this.result_toko = result_toko;
    }
}
