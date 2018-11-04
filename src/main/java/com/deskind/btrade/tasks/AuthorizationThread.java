package com.deskind.btrade.tasks;

import java.io.IOException;

import javax.websocket.Session;

import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.utils.ConnectionPoint;

public class AuthorizationThread extends Thread{
	
	private Trader trader;
	private TradingSystem tradingSystem;
	
	public AuthorizationThread(Trader trader, TradingSystem tradingSystem) {
		this.trader = trader;
		this.tradingSystem = tradingSystem;
	}

	@Override
	public void run() {
		
		//create endpoint
		tradingSystem.setSession(tradingSystem.getLot(), new ConnectionPoint(trader));
		
		
		//wait a little until connection process will be done
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//authorize 
		try {
			Session session = tradingSystem.getSession();
			session.getBasicRemote().sendText("{\"authorize\": \""+trader.getToken()+"\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
