package com.example.reporttdm.model;

import java.util.ArrayList;
import java.util.List;

public class GetPenjualanResponse {

    int status_code;
    String status;
    List<Barang> result_tes = new ArrayList<Barang>();

    public GetPenjualanResponse(int status_code, String status, List<Barang> result_tes) {
        this.status_code = status_code;
        this.status = status;
        this.result_tes = result_tes;
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

    public List<Barang> getResult_tes() {
        return result_tes;
    }

    public void setResult_tes(List<Barang> result_tes) {
        this.result_tes = result_tes;
    }
}
