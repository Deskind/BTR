package com.deskind.btrade.binary.objects;

public class ProfitTable {
	private int count;
	private ProfitTableEntry [] transactions;
	
	public ProfitTable(int count, ProfitTableEntry[] transactions) {
		super();
		this.count = count;
		this.transactions = transactions;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ProfitTableEntry[] getTransactions() {
		return transactions;
	}

	public void setTransactions(ProfitTableEntry[] transactions) {
		this.transactions = transactions;
	}
	
	
}
