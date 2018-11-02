package com.deskind.btrade.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;

public class StayAlive extends Thread{
	private List<Trader> traders;

	public StayAlive(List<Trader> traders) {
		this.traders = traders;
	}

	@Override
	public void run() {
		//list for storing trading system
		List<TradingSystem> systems = new ArrayList<>();
		
		//fill 'systems'
		for(Trader trader : traders) {
			systems.addAll(trader.getTsList());
		}
		
		while(ManagerServlet.isWorking()) {
			for(TradingSystem ts : systems) {
				if(ts.getSession() != null) {
					try {
						ts.getSession().getBasicRemote().sendText("{\"balance\": 1}");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
