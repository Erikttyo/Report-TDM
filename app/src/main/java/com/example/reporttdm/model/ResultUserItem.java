package com.example.reporttdm.model;

public class ResultUserItem{
	private String password;
	private String nama;
	private String idToko;
	private String telephone;
	private String id;
	private String tipe;
	private String email;
	private String alamat;

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

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

	public void setTelephone(String telephone){
		this.telephone = telephone;
	}

	public String getTelephone(){
		return telephone;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setTipe(String tipe){
		this.tipe = tipe;
	}

	public String getTipe(){
		return tipe;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setAlamat(String alamat){
		this.alamat = alamat;
	}

	public String getAlamat(){
		return alamat;
	}

	@Override
 	public String toString(){
		return 
			"ResultUserItem{" + 
			"password = '" + password + '\'' + 
			",nama = '" + nama + '\'' + 
			",id_toko = '" + idToko + '\'' + 
			",telephone = '" + telephone + '\'' + 
			",id = '" + id + '\'' + 
			",tipe = '" + tipe + '\'' + 
			",email = '" + email + '\'' + 
			",alamat = '" + alamat + '\'' + 
			"}";
		}
}
