package com.deskind.btrade.tasks;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.requests.PriceProposalRequest;
import com.deskind.btrade.entities.Signal;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;

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
		
		Signal signal = null;
		String tradingSystemName = null;
		PriceProposalRequest proposalRequest = null;
				
		while(ManagerServlet.isWorking()) {

			try {
				//getting signal object from queue
				signal = signals.take();
				
				tradingSystemName = signal.getTsName();
				
				for(Trader trader : traders) {
					for(TradingSystem tradingSystem : trader.getTsList()) {
						if(tradingSystem.getName().equals(tradingSystemName)) {
							//create price proposal object
							proposalRequest = new PriceProposalRequest(1,
												String.valueOf(tradingSystem.getLot()),
												"stake",
												signal.getType(),
												"USD",
												signal.getDuration(),
												signal.getDurationUnit(),
												signal.getSymbol());
							
							//sending
							tradingSystem.sendPriceProposalRequest(proposalRequest);
						}
					}
				}
				
				
				
				System.out.println("Consumed signal" + signal.toString());
			} catch (InterruptedException e) {
				ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer was interrupted (trading process was stopped)...");
			}
			
			
		}
		
		//log
		ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer STOPPED...");
	}
	
}
