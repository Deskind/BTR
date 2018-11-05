package com.deskind.btrade.binary.objects;

public class Buy {
	private String balance_after;
	private String buy_price;
	private String contract_id;
	private String longcode;
	private float payout;
	private long purchase_time;
	private String shortcode;
	private long start_time;
	private String transaction_id;
	
	public Buy(String balance_after, String buy_price, String contract_id, String longcode, float payout,
			long purchase_time, String shortcode, long start_time, String transaction_id) {
		super();
		this.balance_after = balance_after;
		this.buy_price = buy_price;
		this.contract_id = contract_id;
		this.longcode = longcode;
		this.payout = payout;
		this.purchase_time = purchase_time;
		this.shortcode = shortcode;
		this.start_time = start_time;
		this.transaction_id = transaction_id;
	}

	public String getBalance_after() {
		return balance_after;
	}

	public void setBalance_after(String balance_after) {
		this.balance_after = balance_after;
	}

	public String getBuy_price() {
		return buy_price;
	}

	public void setBuy_price(String buy_price) {
		this.buy_price = buy_price;
	}

	public String getContract_id() {
		return contract_id;
	}

	public void setContract_id(String contract_id) {
		this.contract_id = contract_id;
	}

	public String getLongcode() {
		return longcode;
	}

	public void setLongcode(String longcode) {
		this.longcode = longcode;
	}

	public float getPayout() {
		return payout;
	}

	public void setPayout(float payout) {
		this.payout = payout;
	}

	public long getPurchase_time() {
		return purchase_time;
	}

	public void setPurchase_time(long purchase_time) {
		this.purchase_time = purchase_time;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	
	
}
