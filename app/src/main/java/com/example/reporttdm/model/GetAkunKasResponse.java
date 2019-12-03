package com.example.reporttdm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAkunKasResponse{

	@SerializedName("status_code")
    @Expose
	private String statusCode;
	@SerializedName("result_akun_kas")
    @Expose
	private List<AkunKas> resultAkunKas;
	@SerializedName("status")
    @Expose
	private String status;

	public void setStatusCode(String statusCode){
		this.statusCode = statusCode;
	}

	public String getStatusCode(){
		return statusCode;
	}

	public void setResultAkunKas(List<AkunKas> resultAkunKas){
		this.resultAkunKas = resultAkunKas;
	}

	public List<AkunKas> getResultAkunKas(){
		return resultAkunKas;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"GetAkunKasResponse{" + 
			"status_code = '" + statusCode + '\'' + 
			",result_akun_kas = '" + resultAkunKas + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}