package com.deskind.btrade.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.objects.Error;
import com.deskind.btrade.binary.objects.ProfitTableEntry;
import com.deskind.btrade.binary.objects.Proposal;
import com.deskind.btrade.binary.passthrough.PassthroughTsName;
import com.deskind.btrade.binary.requests.BuyRequest;
import com.deskind.btrade.binary.responses.BuyResponse;
import com.deskind.btrade.binary.responses.ProfitTableResponse;
import com.deskind.btrade.binary.responses.ProposalResponse;
import com.deskind.btrade.entities.LoginMessage;
import com.deskind.btrade.entities.ProposalResponceLog;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@ClientEndpoint
public class ConnectionPoint{
	private static final int MAX_PRICE_TO_BUY = 1000;
	private Trader trader;
	private TradingSystem tradingSystem;
	
	public ConnectionPoint(Trader trader, TradingSystem tradingSystem) {
		super();
		this.tradingSystem = tradingSystem;
		this.trader = trader;
	}

	@OnOpen
	public void connectionOpen() {
		LoginMessage message = new LoginMessage(new Date());
		tradingSystem.addLoginMessage(message);
		
		System.out.println(trader.getName() + " Online ...");
	}
	
	@OnClose
	public void connectionClose(CloseReason reason) {
		List<LoginMessage> logins = tradingSystem.getLogins();
		
		//last message
		LoginMessage message = logins.get(logins.size()-1);
		
		//set date
		message.setLogout(new Date());
		
		//set code
		message.setCloseCode(reason.getCloseCode().toString());
		
		//set message
		message.setCloseMessage(reason.getReasonPhrase());
		
		System.out.println("Trader: " + trader.getName() + " OFFLINE. Reason: " + reason.getReasonPhrase() +
				" Code: " +reason.getCloseCode());
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
        	
        	case "balance": {
        		processBalance(message, parser);
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
		
		if(isThereErrorInResponse(response.getError(), "Profit table answer ")) return;
		
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
		
		//getting trading system name from passthrough
		String tsName = buyResponse.getPassthrough().getTsName();
		
		Error error = buyResponse.getError();
		
		//error check
		if(error != null) { 
			String errorCode = error.getCode();
			String errorMessage = error.getMessage();
			
			String entryMessage = String.format("Error code: %s and message: %s", errorCode, errorMessage);
			
			ProfitTableEntry entry = new ProfitTableEntry(trader.getName(),
															trader.getToken(),
															tsName,
															new Date(),
															entryMessage
															);
			
			trader.addFailedContract(entry);
			
			//RETURN POINT
			return;
		}
		
		//collect data from buy response 
		//array looks like 'PUT_R_50_1.94_1541584530_1541584590_S0P_0'
		String shortCode = buyResponse.getBuy().getShortcode();
		
		//extract dates from 'shortcode'
		List<String> dates = extractDates(shortCode);
		long buyTime = Long.valueOf(dates.get(0));
		long sellTime = Long.valueOf(dates.get(1));
		
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
		
		TradingSystem ts = trader.getTsByName(proposalResponse.getPassthroughTsName().getTsName());
		
		float payout = calculatePayout(proposalResponse);
		
		//set up 'ProposalResponceLog' object
		ProposalResponceLog log = new ProposalResponceLog(new Date(),
				payout, 
				proposalResponse.getEcho_req().getAmount(),
				proposalResponse.getEcho_req().getContract_type(),
				proposalResponse.getEcho_req().getDuration(),
				proposalResponse.getEcho_req().getDuration_unit(),
				proposalResponse.getPassthroughTsName().getTsName(),
				proposalResponse.getEcho_req().getSymbol());
		
		//error check
		if(error != null) {
			
			//log error fields
			log.setErrorCode(error.getCode());
			log.setErrorMessage(error.getMessage());
			
			//save log with error
			ts.addProposalLog(log);
			
			//return point
			return;
		}
		
		//if payout value is sufficient
		if(payout > ManagerServlet.getPayout()) {
			
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
		}
		
		//add log record to trading system
		ts.addProposalLog(log);
		return;
	}
	
	/**
	 * Process balance message 
	 * @param message
	 */
	public void processBalance(String message, JsonParser parser) {
		float balance = 0;
		
		JsonElement root = parser.parse(message);
		JsonElement balanceElement = root.getAsJsonObject().get("balance");
		if(balanceElement != null) {
			balance = balanceElement.getAsJsonObject().get("balance").getAsFloat();
			trader.setBalance(balance);
		}
	}
	
	/**
	 * Checking error object in response object
	 * @param error
	 * @return
	 */
	private boolean isThereErrorInResponse(Error error, String who) {
		if(error == null) {
			return false;
		}else {
			String code = error.getCode();
			String message = error.getMessage();
			
			System.out.println(who + " contains error with code: "  + code + " and message: " + message);
			
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
		//getting 'Proposal' object
		Proposal proposal = proposalResponse.getProposal();
		
		float askPrice = 0;
		float payout = 0;
		
		if(proposal != null) {
			askPrice = Float.valueOf(proposal.getAsk_price());
			payout = Float.valueOf(proposal.getPayout());
			
			return (payout * 100 / askPrice) - 100;
		}else {
			return 0;
		}
	}
	
	public static List<String> extractDates(String source){
		String [] sourceAsArr = source.split("_");
		List<String> elements = Arrays.asList(sourceAsArr);
		List<String> dates = new ArrayList<>();
		
		String pattern = "\\d{10}";
		
		for(int i = 0; i < elements.size(); i++) {
			String element = elements.get(i);
			
			if(element.matches(pattern)) {
				dates.add(element);
			}
		}
		
		return dates;
	}
}
