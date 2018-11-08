package com.deskind.btrade.binary.requests;

public class ProfitTableRequest {
	private int profit_table;
	private int description;
	private long date_from;
	private long date_to;
	
	public ProfitTableRequest(int profit_table, int description, long date_from, long date_to) {
		super();
		this.profit_table = profit_table;
		this.description = description;
		this.date_from = date_from;
		this.date_to = date_to;
	}

	public int getProfit_table() {
		return profit_table;
	}

	public void setProfit_table(int profit_table) {
		this.profit_table = profit_table;
	}

	public int getDescription() {
		return description;
	}

	public void setDescription(int description) {
		this.description = description;
	}

	public long getDate_from() {
		return date_from;
	}

	public void setDate_from(long date_from) {
		this.date_from = date_from;
	}

	public long getDate_to() {
		return date_to;
	}

	public void setDate_to(long date_to) {
		this.date_to = date_to;
	}
	
}
