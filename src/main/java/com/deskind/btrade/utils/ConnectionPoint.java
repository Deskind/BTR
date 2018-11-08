package com.deskind.btrade.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.objects.Error;
import com.deskind.btrade.binary.objects.ProfitTableEntry;
import com.deskind.btrade.binary.passthrough.PassthroughTsName;
import com.deskind.btrade.binary.requests.BuyRequest;
import com.deskind.btrade.binary.responses.BuyResponse;
import com.deskind.btrade.binary.responses.ProfitTableResponse;
import com.deskind.btrade.binary.responses.ProposalResponse;
import com.deskind.btrade.entities.Trader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        		processBuy(message);
        		return;
        	}
        	
        	case "profit_table": {
        		processProfitTable(message);
        		return;
        	}
        }
		
		
	}
	
	private void processProfitTable(String message) {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(ProfitTableEntry.class, new EntryDeserializer())
				.create();
		
		ProfitTableResponse response = gson.fromJson(message, ProfitTableResponse.class);
		
		if(isThereErrorInResponse(response.getError())) return;
		
		HashMap<String, ContractDetails> contracts = trader.getContracts();
		
		ProfitTableEntry [] entries = response.getProfit_table().getTransactions();
		
		for(int i = 0; i < entries.length; i++) {
			ProfitTableEntry entry = entries[i];
			
			String contractId = entry.getContract_id();
			
			if(contracts.containsKey(contractId)) {
				ContractDetails details = contracts.get(contractId);
				
				entry.setName(trader.getName());
				entry.setToken(trader.getToken());
				entry.setTsName(details.getTsName());
				
				if(entry.getSell_price() > 0) {
					entry.setResult("+");
				}else {
					entry.setResult("-");
				}
				
				HibernateUtil.saveContract(entry);
				
				System.out.println("+++ Entry saved to DB ... ");
				
				trader.removeFromContracts(contractId);
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
		
		//error check
		if(isThereErrorInResponse(buyResponse.getError())) return;
		
		//collect data from buy response 
		//array looks like 'PUT_R_50_1.94_1541584530_1541584590_S0P_0'
		String [] shortCode = buyResponse.getBuy().getShortcode().split("_");
		
		//get fourth and fifth element
		long buyTime = Long.valueOf(shortCode[4]);
		long sellTime = Long.valueOf(shortCode[5]);
		
		//getting trading system name from passthrough
		String tsName = buyResponse.getPassthrough().getTsName();
		
		//contract id for using as a key
		String contractId = buyResponse.getBuy().getContract_id();
		
		//create 'Contract Detail' object for storing in 'Map' collection
		ContractDetails details = new ContractDetails(buyTime, sellTime, tsName);
		
		trader.addNewContract(contractId, details);
		
	}

	/**
	 * Process 'proposal' message
	 * @param message
	 */
	private void processProposal(String message, Session session) {
		Gson gson = new Gson();
		
		ProposalResponse proposalResponse = gson.fromJson(message, ProposalResponse.class);
		
		Error error = proposalResponse.getError();
		
		//error check
		if(isThereErrorInResponse(error)) return;
		
		float payout = calculatePayout(proposalResponse);
		
		//if payout value is sufficient
		if(payout > ManagerServlet.getPayout()) {
			System.out.println("Payout is ok");
			
			//getting passthrough from price proposal response
			PassthroughTsName passthrough = proposalResponse.getPassthroughTsName();
			
			//create buy request
			BuyRequest buyRequest = new BuyRequest(proposalResponse.getProposal().getId(), MAX_PRICE_TO_BUY, passthrough);
			
			if(session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText(gson.toJson(buyRequest));
				} catch (IOException e) {
					e.printStackTrace();
				}
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
	private boolean isThereErrorInResponse(Error error) {
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
