package com.deskind.btrade.utils;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.requests.BuyRequest;
import com.deskind.btrade.binary.requests.PriceProposalRequest;
import com.deskind.btrade.binary.responses.BuyResponse;
import com.deskind.btrade.binary.responses.ProposalResponse;
import com.deskind.btrade.binary.objects.Error;
import com.deskind.btrade.entities.Trader;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

@ClientEndpoint
public class ConnectionPoint{
	private static final int MAX_PRICE_TO_BUY = 1000;
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
	public void messageReceived(String message, Session session) {
		//getting message type
        JsonParser parser = new JsonParser();
        String messageType = parser.parse(message).getAsJsonObject().get("msg_type").getAsString();
        
        switch(messageType){
        	case "proposal":{
        		processProposal(message, session);
        		return;
        	}
        	
        	case "buy": {
        		System.out.println("Buy message response " + message);
        		processBuy(message);
        		return;
        	}
        }
		
		
	}
	
	/**
	 * Process buy message
	 * @param message
	 */
	private void processBuy(String message) {
		Gson gson = new Gson();
		BuyResponse buyResponse = gson.fromJson(message, BuyResponse.class);
		System.out.println("Contract id is: " + buyResponse.getBuy().getContract_id());
		
	}

	/**
	 * Process 'proposal' message
	 * @param message
	 */
	private void processProposal(String message, Session session) {
		Gson gson = new Gson();
		
		ProposalResponse proposalResponse = gson.fromJson(message, ProposalResponse.class);
		
		Error error = proposalResponse.getError();
		
		//if response contains error return execution
		if(isThereErrorInResponce(error)) return;
		
		if(proposalResponse.getError() != null) {
			System.out.println("Price proposal response contains error with code");
		}
		
		float payout = calculatePayout(proposalResponse);
		
		//if payout value is sufficient
		if(payout > ManagerServlet.getPayout()) {
			System.out.println("Payout is ok");
			//create buy request
			BuyRequest buyRequest = new BuyRequest(proposalResponse.getProposal().getId(), MAX_PRICE_TO_BUY);
			
			try {
				session.getBasicRemote().sendText(gson.toJson(buyRequest));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else {
			System.out.println("Payout is too low");
		}
		return;
	}
	
	/**
	 * Checking error object in response object
	 * @param error
	 * @return
	 */
	private boolean isThereErrorInResponce(Error error) {
		if(error == null) {
			return false;
		}else {
			String code = error.getCode();
			String message = error.getMessage();
			
			System.out.println("Price proposal response contains error with code: "  + code + " and message: " + message);
			
			return true;
		}
		
	}

	/**
	 * Calculate payout from Binary based on response to 'price proposal' request
	 * @param askPrice
	 * @param payout
	 * @return
	 */
	private static float calculatePayout(ProposalResponse proposalResponse) {
		float askPrice = Float.valueOf(proposalResponse.getProposal().getAsk_price());
		float payout = Float.valueOf(proposalResponse.getProposal().getPayout());
		
		return payout*100/(askPrice*2);
	}
}
