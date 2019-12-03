package com.example.reporttdm.model;

public class UploadImageResponse {

    private String status_code;
    private String url;
    private String status;


    // Getter Methods

    public String getStatus_code() {
        return status_code;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    // Setter Methods

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
