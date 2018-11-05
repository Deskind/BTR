package com.deskind.btrade.binary.objects;

public class Proposal {
	private String ask_price;
	private String date_start;
	private String display_value;
	private String id;
	private String longcode;
	private String payout;
	private String spot;
	private String spot_time;
	
	
	public Proposal(String ask_price, String date_start, String display_value, String id, String longcode,
			String payout, String spot, String spot_time) {
		super();
		this.ask_price = ask_price;
		this.date_start = date_start;
		this.display_value = display_value;
		this.id = id;
		this.longcode = longcode;
		this.payout = payout;
		this.spot = spot;
		this.spot_time = spot_time;
	}


	public String getAsk_price() {
		return ask_price;
	}


	public void setAsk_price(String ask_price) {
		this.ask_price = ask_price;
	}


	public String getDate_start() {
		return date_start;
	}


	public void setDate_start(String date_start) {
		this.date_start = date_start;
	}


	public String getDisplay_value() {
		return display_value;
	}


	public void setDisplay_value(String display_value) {
		this.display_value = display_value;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getLongcode() {
		return longcode;
	}


	public void setLongcode(String longcode) {
		this.longcode = longcode;
	}


	public String getPayout() {
		return payout;
	}


	public void setPayout(String payout) {
		this.payout = payout;
	}


	public String getSpot() {
		return spot;
	}


	public void setSpot(String spot) {
		this.spot = spot;
	}


	public String getSpot_time() {
		return spot_time;
	}


	public void setSpot_time(String spot_time) {
		this.spot_time = spot_time;
	}
	
	
}
