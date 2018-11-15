package com.deskind.btrade.utils;

public class ContractDetails {
	private long buyTime;
	private long sellTime;
	private String tsName;
	
	public ContractDetails(long buyTime, long sellTime, String tsName) {
		this.buyTime = buyTime;
		this.sellTime = sellTime;
		this.tsName = tsName;
	}
	public long getBuyTime() {
		return buyTime;
	}
	public void setBuyTime(long buyTime) {
		this.buyTime = buyTime;
	}
	public long getSellTime() {
		return sellTime;
	}
	public void setSellTime(long sellTime) {
		this.sellTime = sellTime;
	}
	public String getTsName() {
		return tsName;
	}
	public void setTsName(String tsName) {
		this.tsName = tsName;
	}
	
	
}
