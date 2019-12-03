package com.example.reporttdm.model;

public class RegisterResponse {

    String status_code;
    String status;
    int result_regis;

    public RegisterResponse(String status_code, String status, int result_regis) {
        this.status_code = status_code;
        this.status = status;
        this.result_regis = result_regis;
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

    public int getResult_regis() {
        return result_regis;
    }

    public void setResult_regis(int result_regis) {
        this.result_regis = result_regis;
    }
}
