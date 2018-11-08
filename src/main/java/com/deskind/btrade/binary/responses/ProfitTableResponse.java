package com.deskind.btrade.binary.responses;

import com.deskind.btrade.binary.objects.Error;
import com.deskind.btrade.binary.objects.ProfitTable;
import com.deskind.btrade.binary.requests.ProfitTableRequest;

public class ProfitTableResponse {
	private ProfitTableRequest echo_req;
	private String msg_type;
	private ProfitTable profit_table;
	private Error error;
	
	

	public ProfitTableResponse(ProfitTableRequest echo_req, String msg_type, ProfitTable profit_table, Error error) {
		super();
		this.echo_req = echo_req;
		this.msg_type = msg_type;
		this.profit_table = profit_table;
		this.error = error;
	}

	public ProfitTableRequest getEcho_req() {
		return echo_req;
	}

	public void setEcho_req(ProfitTableRequest echo_req) {
		this.echo_req = echo_req;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public ProfitTable getProfit_table() {
		return profit_table;
	}

	public void setProfit_table(ProfitTable profit_table) {
		this.profit_table = profit_table;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}
	
	
}
