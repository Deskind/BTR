package com.deskind.btrade.tasks;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import javax.websocket.Session;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.passthrough.PassthroughTsName;
import com.deskind.btrade.binary.requests.PriceProposalRequest;
import com.deskind.btrade.entities.Signal;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.enums.SignalStatus;
import com.deskind.btrade.utils.HibernateUtil;
import com.google.gson.Gson;

public class SignalsConsumer extends Thread{
	private ArrayBlockingQueue<Signal> signals;
	private List<Trader> traders;
	
	public SignalsConsumer(ArrayBlockingQueue<Signal> queue, List<Trader> traders) {
		signals = queue;
		this.traders = traders;
	}

	@Override
	public void run() {
		//log
		ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer STARTED...");
		
		
		PriceProposalRequest proposalRequest = null;
		
		
		
				
		while(ManagerServlet.isWorking()) {

			try {
				//getting signal object from queue
				Signal signal = signals.take();
				
				//getting trading system name
				String tradingSystemName = signal.getTsName();
				
				//iterate over traders to find 'interested' traders
				for(Trader trader : traders) {
					
					
					
					for(TradingSystem tradingSystem : trader.getTsList()) {
						if(tradingSystem.getName().equals(tradingSystemName) && tradingSystem.isActive()) {
							Session session = tradingSystem.getSession();
							
							//create passthrough
							PassthroughTsName passthroughTsName = new PassthroughTsName(tradingSystem.getName());
							
							//create price proposal object
							proposalRequest = new PriceProposalRequest(1,
												String.valueOf(tradingSystem.getLot()),
												"stake",
												signal.getType(),
												"USD",
												signal.getDuration(),
												signal.getDurationUnit(),
												signal.getSymbol(),
												passthroughTsName);
							
							
							
							if(session != null && session.isOpen()) {
						    	try {
									session.getBasicRemote().sendText(new Gson().toJson(proposalRequest));
									trader.addReceivedSignal(signal, SignalStatus.RECEIVED);
								} catch (IOException e) {
									e.printStackTrace();
								}
					    	}else {//we have 'missed' signal. Save it
					    		trader.addReceivedSignal(signal, SignalStatus.MISSED);
					    	}
						}
					}
					
				}
				
				
				System.out.println("Consumed signal. Date: " + new Date().toString() + " " + signal.toString());
				
				sleep(400);
			} catch (InterruptedException e) {
				ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer was interrupted (trading process was stopped)...");
			}
			
			
		}
		
		//log
		ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer STOPPED...");
	}
	
}
