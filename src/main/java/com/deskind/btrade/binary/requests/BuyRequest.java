package com.deskind.btrade.binary.requests;

public class BuyRequest {
	private String buy;
	private int price;
	
	
	public BuyRequest(String buy, int price) {
		this.buy = buy;
		this.price = price;
	}


	public String getBuy() {
		return buy;
	}


	public void setBuy(String buy) {
		this.buy = buy;
	}


	public int getPrice() {
		return price;
	}


	public void setPrice(int price) {
		this.price = price;
	}
	
	
}
