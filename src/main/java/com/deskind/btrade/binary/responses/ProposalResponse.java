package com.deskind.btrade.binary.responses;

import com.deskind.btrade.binary.objects.Proposal;
import com.deskind.btrade.binary.passthrough.Passthrough;
import com.deskind.btrade.binary.requests.PriceProposalRequest;
import com.deskind.btrade.binary.objects.Error;


public class ProposalResponse {
	
	private PriceProposalRequest echo_req;
	private String msg_type;
	private Passthrough passthrough;
	private Proposal proposal;
	private Error error;

	public ProposalResponse(PriceProposalRequest echo_req, String msg_type, Passthrough passthrough, Proposal proposal, Error error) {
		this.echo_req = echo_req;
		this.msg_type = msg_type;
		this.passthrough = passthrough;
		this.proposal = proposal;
		this.error = error;
	}

	public PriceProposalRequest getEcho_req() {
		return echo_req;
	}

	public void setEcho_req(PriceProposalRequest echo_req) {
		this.echo_req = echo_req;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public Passthrough getPassthrough() {
		return passthrough;
	}

	public void setPassthrough(Passthrough passthrough) {
		this.passthrough = passthrough;
	}

	public Proposal getProposal() {
		return proposal;
	}

	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}
	
	
	
}