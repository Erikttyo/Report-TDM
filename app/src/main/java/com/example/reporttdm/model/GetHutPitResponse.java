package com.example.reporttdm.model;

import java.util.ArrayList;
import java.util.List;

public class GetHutPitResponse {
    String status_code;
    String status;
    List<HutPit> result_hutpit = new ArrayList<HutPit>();

    public GetHutPitResponse(String status_code, String status, List<HutPit> result_hutpit) {
        this.status_code = status_code;
        this.status = status;
        this.result_hutpit = result_hutpit;
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

    public List<HutPit> getResult_hutpit() {
        return result_hutpit;
    }

    public void setResult_hutpit(List<HutPit> result_hutpit) {
        this.result_hutpit = result_hutpit;
    }
}
