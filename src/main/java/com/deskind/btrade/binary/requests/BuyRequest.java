package com.deskind.btrade.binary.requests;

import com.deskind.btrade.binary.passthrough.PassthroughTsName;

public class BuyRequest {
	private String buy;
	private int price;
	
	private PassthroughTsName passthrough;

	public BuyRequest(String buy, int price, PassthroughTsName passthrough) {
		super();
		this.buy = buy;
		this.price = price;
		this.passthrough = passthrough;
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


	public PassthroughTsName getPassthrough() {
		return passthrough;
	}


	public void setPassthrough(PassthroughTsName passthrough) {
		this.passthrough = passthrough;
	}
	
	
}
