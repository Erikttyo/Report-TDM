package com.example.reporttdm.model;

import java.util.List;

public class GetPembelianResponse{
	private String status_code;
	private List<ResultPembelianItem> result_pembelian;
	private String status;

	public void setStatusCode(String statusCode){
		this.status_code = statusCode;
	}

	public String getStatusCode(){
		return status_code;
	}

	public void setResult_pembelian(List<ResultPembelianItem> result_pembelian){
		this.result_pembelian = result_pembelian;
	}

	public List<ResultPembelianItem> getResult_pembelian(){
		return result_pembelian;
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
			"GetPembelianResponse{" + 
			"status_code = '" + status_code + '\'' +
			",result_pembelian = '" + result_pembelian + '\'' +
			",status = '" + status + '\'' + 
			"}";
		}
}