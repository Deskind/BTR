package com.deskind.btrade.binary.responses;

import com.deskind.btrade.binary.objects.Buy;
import com.deskind.btrade.binary.requests.BuyRequest;
import com.deskind.btrade.binary.objects.Error;

public class BuyResponse {
	
	private Buy buy;
	private  BuyRequest echo_req;
	private String msg_type;
	private Error error;
	
	public BuyResponse(Buy buy, BuyRequest echo_req, String msg_type, Error error) {
		super();
		this.buy = buy;
		this.echo_req = echo_req;
		this.msg_type = msg_type;
		this.error = error;
	}

	public Buy getBuy() {
		return buy;
	}

	public void setBuy(Buy buy) {
		this.buy = buy;
	}

	public BuyRequest getEcho_req() {
		return echo_req;
	}

	public void setEcho_req(BuyRequest echo_req) {
		this.echo_req = echo_req;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}
	
	
	
}
