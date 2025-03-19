package com.moneyTransfer.model;

import java.util.Date;
//結匯資料庫
public class ExchangeTransaction {
	private String name;
	private String idNumber;
	private String birthDate;
	private String nationality;
	private String residentPermitNo;
	private String permitExpiryDate;
	private String phoneNumber;
	private String currency;
	private Double amount;
	private String transactionType;
	private Date updateTime;
	private String transactionId;

// 構造函數  
	public ExchangeTransaction() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getResidentPermitNo() {
		return residentPermitNo;
	}

	public void setResidentPermitNo(String residentPermitNo) {
		this.residentPermitNo = residentPermitNo;
	}

	public String getPermitExpiryDate() {
		return permitExpiryDate;
	}

	public void setPermitExpiryDate(String permitExpiryDate) {
		this.permitExpiryDate = permitExpiryDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}