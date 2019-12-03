package com.example.reporttdm.model;

import java.util.ArrayList;
import java.util.List;

public class GetProfileResponse {

    int status_code;
    String status;
    List<User> result_user = new ArrayList<User>();

    public GetProfileResponse(int status_code, String status, List<User> result_user) {
        this.status_code = status_code;
        this.status = status;
        this.result_user = result_user;
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

    public List<User> getResult_user() {
        return result_user;
    }

    public void setResult_user(List<User> result_user) {
        this.result_user = result_user;
    }
}
