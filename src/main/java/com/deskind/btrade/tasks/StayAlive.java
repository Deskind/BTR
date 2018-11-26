package com.deskind.btrade.tasks;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.utils.HibernateUtil;

public class StayAlive extends TimerTask {
	private static int DB_TOUCH_INTERVAL = 30;
	private static int dbTouchCounter = 0;
	
	private List<Trader> traders;

	public StayAlive(List<Trader> traders) {
		this.traders = traders;
	}

	@Override
	public void run() {

		// do while manager servlet works
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
			
			//touch counter
			dbTouchCounter++;
			
			if(dbTouchCounter == DB_TOUCH_INTERVAL) {
				//reset counter
				dbTouchCounter = 0;
				
				//get all traders from database for just 'waking up' data base (to prevent 'wait_timeout')
				HibernateUtil.getAllTraders();
			}
	}

}
