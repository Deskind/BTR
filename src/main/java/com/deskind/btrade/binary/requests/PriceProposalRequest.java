package com.deskind.btrade.binary.requests;

import com.deskind.btrade.binary.passthrough.PassthroughTsName;

public class PriceProposalRequest {
	private int proposal;
	private String amount;
	private String basis;
	private String contract_type;
	private String currency;
	private String duration;
	private String duration_unit;
	private String symbol;
	
	private PassthroughTsName passthrough;

	public PriceProposalRequest(int proposal, String amount, String basis, String contract_type, String currency,
			String duration, String duration_unit, String symbol, PassthroughTsName dateAndTsNamePassthrough) {
		this.proposal = proposal;
		this.amount = amount;
		this.basis = basis;
		this.contract_type = contract_type;
		this.currency = currency;
		this.duration = duration;
		this.duration_unit = duration_unit;
		this.symbol = symbol;
		this.passthrough = dateAndTsNamePassthrough;
	}


	public int getProposal() {
		return proposal;
	}


	public void setProposal(int proposal) {
		this.proposal = proposal;
	}

	public String getAmount() {
		return amount;
	}


	public void setAmount(String amount) {
		this.amount = amount;
	}


	public String getBasis() {
		return basis;
	}


	public void setBasis(String basis) {
		this.basis = basis;
	}


	public String getContract_type() {
		return contract_type;
	}


	public void setContract_type(String contract_type) {
		this.contract_type = contract_type;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getDuration() {
		return duration;
	}


	public void setDuration(String duration) {
		this.duration = duration;
	}


	public String getDuration_unit() {
		return duration_unit;
	}


	public void setDuration_unit(String duration_unit) {
		this.duration_unit = duration_unit;
	}


	public String getSymbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public PassthroughTsName getDateAndTsNamePassthrough() {
		return passthrough;
	}


	public void setDateAndTsNamePassthrough(PassthroughTsName dateAndTsNamePassthrough) {
		this.passthrough = dateAndTsNamePassthrough;
	}
	
	
	
}
