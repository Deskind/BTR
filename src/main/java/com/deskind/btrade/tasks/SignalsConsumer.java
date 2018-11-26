package com.deskind.btrade.tasks;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.passthrough.PassthroughTsName;
import com.deskind.btrade.binary.requests.PriceProposalRequest;
import com.deskind.btrade.entities.Signal;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.enums.SignalStatus;
import com.google.gson.Gson;

public class SignalsConsumer extends Thread{
	private int DELAY_BETWEAN_SIGNALS = 300;
	
	private String CONTRACT_BASIS = "stake";
	private String CURRENCY = "USD";
	
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
				//at this point thread waiting a signal if queue is empty
				Signal signal = signals.take();
				
				//log to file
				String sgn = signal.toString();
				ManagerServlet.getLogger().log(Level.INFO, sgn);
				System.out.println(sgn);
				
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
												CONTRACT_BASIS,
												signal.getType(),
												CURRENCY,
												signal.getDuration(),
												signal.getDurationUnit(),
												signal.getSymbol(),
												passthroughTsName);

							if(session != null && session.isOpen()) {
						    	try {
						    		//string to send
						    		String requestString = new Gson().toJson(proposalRequest);
						    		
						    		//sending...
									session.getBasicRemote().sendText(requestString);
									
									//saving 'RECEIVED' signal
									//creating signal 'clone'
									Signal s = new Signal(signal.getDate(),
											signal.getType(),
											signal.getDuration(),
											signal.getDurationUnit(),
											signal.getSymbol(),
											signal.getTsName());
									
									trader.addReceivedSignal(s, SignalStatus.RECEIVED);
								} catch (IOException e) {
									e.printStackTrace();
								} catch (IllegalStateException e) {
									try {
										session.close(new CloseReason(CloseCodes.CLOSED_ABNORMALLY, "Text_full_writing error"));
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
					    	}else {//we have 'MISSED' signal. Save it
					    		trader.addReceivedSignal(signal, SignalStatus.MISSED);
					    	}
						}
					}
					
				}
				
				sleep(DELAY_BETWEAN_SIGNALS);
				} catch (InterruptedException e) {
					ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer was interrupted (trading process was stopped)...");
				}
			
			
		}
		
		//log
		ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer STOPPED...");
	}
	
}
