package com.example.reporttdm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AkunKas {
	@SerializedName("nama")
	@Expose
	private String nama;
	@SerializedName("id_toko")
	@Expose
	private String idToko;
	@SerializedName("id")
	@Expose
	private String id;

	public void setNama(String nama){
		this.nama = nama;
	}

	public String getNama(){
		return nama;
	}

	public void setIdToko(String idToko){
		this.idToko = idToko;
	}

	public String getIdToko(){
		return idToko;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"AkunKas{" +
			"nama = '" + nama + '\'' + 
			",id_toko = '" + idToko + '\'' + 
			",id = '" + id + '\'' + 
			"}";
		}
}
