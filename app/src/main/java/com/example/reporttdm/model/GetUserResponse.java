package com.example.reporttdm.model;

import java.util.List;

public class GetUserResponse{
	private List<User> result_user;
	private String status_code;
	private String status;

	public void setResult_user(List<User> result_user){
		this.result_user = result_user;
	}

	public List<User> getResult_user(){
		return result_user;
	}

	public void setStatus_code(String status_code){
		this.status_code = status_code;
	}

	public String getStatus_code(){
		return status_code;
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
			"GetUserResponse{" + 
			"result_user = '" + result_user + '\'' +
			",status_code = '" + status_code + '\'' +
			",status = '" + status + '\'' + 
			"}";
		}
}