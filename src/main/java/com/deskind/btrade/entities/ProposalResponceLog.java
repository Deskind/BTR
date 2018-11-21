package com.deskind.btrade.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "proposal")
public class ProposalResponceLog {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int proposal_id;
	
	@Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
	
	private float payout;
	
	private String amount;
	private String type;
	private String duration;
	private String duration_unit;
	
	@Column(name = "ts_name")
	private String tsName;
	private String symbol;
	
	@Column(name = "error_code")
	private String errorCode;
	
	@Column(name = "error_message")
	private String errorMessage;
	
	
	
	public ProposalResponceLog() {
		super();
	}

	public ProposalResponceLog(Date date, float payout, String amount, String type, String duration,
			String duration_unit, String tsName, String symbol) {
		super();
		this.date = date;
		this.payout = payout;
		this.amount = amount;
		this.type = type;
		this.duration = duration;
		this.duration_unit = duration_unit;
		this.tsName = tsName;
		this.symbol = symbol;
	}

	public int getProposal_id() {
		return proposal_id;
	}

	public void setProposal_id(int proposal_id) {
		this.proposal_id = proposal_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getPayout() {
		return payout;
	}

	public void setPayout(float payout) {
		this.payout = payout;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getTsName() {
		return tsName;
	}

	public void setTsName(String tsName) {
		this.tsName = tsName;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	
}
