package com.deskind.btrade.utils;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;

import com.deskind.btrade.entities.Trader;
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
        JsonParser parser = new JsonParser();
        String messageType = parser.parse(message).getAsJsonObject().get("msg_type").getAsString();
        
        switch(messageType){
        	case "proposal":{
        		
        		System.out.println(message);
        		
        		return;
        	}
        }
		
		
	}
}
