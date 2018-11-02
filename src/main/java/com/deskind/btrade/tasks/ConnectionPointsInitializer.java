package com.deskind.btrade.tasks;

import java.util.List;

import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.utils.ConnectionPoint;

public class ConnectionPointsInitializer extends Thread{
	
	private List<Trader> traders;
	
	public ConnectionPointsInitializer(List<Trader> traders) {
		this.traders = traders;
	}

	@Override
	public void run() {
		for(Trader trader : traders) {
			
			List<TradingSystem> systems = trader.getTsList();
			
			for(TradingSystem tradingSystem : systems) {
				tradingSystem.setSession(tradingSystem.getLot(), new ConnectionPoint(trader));
			}
		}
	}
	
}
