package com.deskind.btrade.tasks;

import java.util.concurrent.ArrayBlockingQueue;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.entities.Signal;
import com.deskind.btrade.entities.SignalManager;

public class SignalsConsumer extends Thread{
	private ArrayBlockingQueue<Signal> signals;
	private boolean working = false;
	
	public SignalsConsumer(ArrayBlockingQueue<Signal> queue) {
		signals = queue;
	}

	@Override
	public void run() {
		while(ManagerServlet.isWorking()) {
			try {
				Signal signal = signals.take();
				System.out.println("Consumed signal" + signal.toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
