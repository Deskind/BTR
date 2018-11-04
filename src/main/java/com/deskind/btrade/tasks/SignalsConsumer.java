package com.deskind.btrade.tasks;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.entities.Signal;

public class SignalsConsumer extends Thread{
	private ArrayBlockingQueue<Signal> signals;
	
	public SignalsConsumer(ArrayBlockingQueue<Signal> queue) {
		signals = queue;
	}

	@Override
	public void run() {
		//log
		ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer STARTED...");
				
		while(ManagerServlet.isWorking()) {
//			try {
//				Signal signal = signals.take();
//				System.out.println("Consumed signal" + signal.toString());
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				//log
				ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer was interrupted (trading process was stopped)...");
			}
		}
		
		//log
		ManagerServlet.getLogger().log(Level.INFO, "Thread signals consumer STOPPED...");
	}
	
}
