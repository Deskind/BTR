package com.deskind.btrade.utils;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.requests.PriceProposalRequest;
import com.deskind.btrade.binary.responses.ProposalResponse;
import com.deskind.btrade.entities.Trader;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

@ClientEndpoint
public class ConnectionPoint{
	private Trader trader;
	
	public ConnectionPoint(Trader trader) {
		super();
		this.trader = trader;
	}

	@OnOpen
	public void connectionOpen() {
		System.out.println(trader.getName() + " Online ...");
	}
	
	@OnClose
	public void connectionClose(CloseReason closeReason) {
		System.out.println(trader.getName() + " Offline ..." + closeReason.getReasonPhrase());
	}
	
	@OnMessage
	public void messageReceived(String message) {
		//getting message type
		Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        String messageType = parser.parse(message).getAsJsonObject().get("msg_type").getAsString();
        
        switch(messageType){
        	case "proposal":{
        		ProposalResponse proposalRsponse = gson.fromJson(message, ProposalResponse.class);
        		
        		float payout = calculatePayout(proposalRsponse);
        		
        		if(payout > ManagerServlet.getPayout()) {
        			System.out.println("Payout is ok");
        		}else {
        			System.out.println("Payout is too low");
        		}
        		return;
        	}
        }
		
		
	}
	
	/**
	 * Calculate payout from Binary based on response to 'price proposal' request
	 * @param askPrice
	 * @param payout
	 * @return
	 */
	private static float calculatePayout(ProposalResponse proposalRsponse) {
		float askPrice = Float.valueOf(proposalRsponse.getProposal().getAsk_price());
		float payout = Float.valueOf(proposalRsponse.getProposal().getPayout());
		
		return payout*100/(askPrice*2);
	}
}
