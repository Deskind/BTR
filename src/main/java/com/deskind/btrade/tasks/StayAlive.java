package com.deskind.btrade.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.utils.ConnectionPoint;

public class StayAlive extends Thread {
	private final int SLEEP_TIME = 55555;
	
	private List<Trader> traders;

	public StayAlive(List<Trader> traders) {
		this.traders = traders;
	}

	@Override
	public void run() {

		// do while manager servlet works
		while (ManagerServlet.isWorking()) {
			for (Trader trader : traders) {
				List<TradingSystem> systems = trader.getTsList();
				
				for (TradingSystem ts : systems) {
					
					//in case if session already exists
					if (ts.getSession() != null && ts.getSession().isOpen()) {
						try {
							ts.getSession().getBasicRemote().sendText("{\"balance\": 1}");
						} catch (IOException e) {
//							e.printStackTrace();
							System.out.println("+++ Requesting ballance, but connection closed ...");
						}
					//in case if session 'null'
					}else if(ts.getSession() == null || !ts.getSession().isOpen()){
						//run thread for authorization
						Thread t = new AuthorizationThread(trader, ts);
						t.setName("+++New trader authorization thread");
						t.start();
					}
				}
				
			}
			try {
				sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
