package com.example.reporttdm.model;

import java.util.ArrayList;
import java.util.List;

public class GetPelSupResponse {
    String status_code;
    String status;
    List<PelSup> result_pelsup = new ArrayList<PelSup>();

    public GetPelSupResponse(String status_code, String status, List<PelSup> result_pelsup) {
        this.status_code = status_code;
        this.status = status;
        this.result_pelsup = result_pelsup;
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

    public List<PelSup> getResult_pelsup() {
        return result_pelsup;
    }

    public void setResult_pelsup(List<PelSup> result_pelsup) {
        this.result_pelsup = result_pelsup;
    }
}
