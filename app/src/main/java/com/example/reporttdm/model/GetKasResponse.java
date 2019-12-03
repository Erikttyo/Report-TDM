package com.example.reporttdm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetKasResponse {
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result_kas")
    @Expose
    private List<ResultKas> resultKas = null;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResultKas> getResultKas() {
        return resultKas;
    }

    public void setResultKas(List<ResultKas> resultKas) {
        this.resultKas = resultKas;
    }
}
