package com.deskind.btrade.tasks;

import java.io.IOException;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.utils.ConnectionPoint;

public class ConnectionPointsDestroyer extends Thread{
private List<Trader> traders;
	
	public ConnectionPointsDestroyer(List<Trader> traders) {
		this.traders = traders;
	}

	@Override
	public void run() {
		for(Trader trader : traders) {
			
			List<TradingSystem> systems = trader.getTsList();
			
			for(TradingSystem tradingSystem : systems) {
				tradingSystem.closeSession();
			}
		}
	}
}