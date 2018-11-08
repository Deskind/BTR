package com.deskind.btrade.binary.requests;

public class ProfitTableRequest {
	private int profit_table;
	private int description;
	private int limit;

	public ProfitTableRequest(int profit_table, int description, int limit) {
		this.profit_table = profit_table;
		this.description = description;
		this.limit = limit;
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

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	
	
	
}
