package com.example.reporttdm.model;

import java.util.ArrayList;

public class GetKartuHutangResponse {
    private String status_code;
    private String status;
    ArrayList< KartuHutang > result_hutpit = new ArrayList < KartuHutang > ();


    // Getter Methods

    public String getStatus_code() {
        return status_code;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<KartuHutang> getResult_hutpit() {
        return result_hutpit;
    }

    // Setter Methods

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResult_hutpit(ArrayList<KartuHutang> result_hutpit) {
        this.result_hutpit = result_hutpit;
    }
}
