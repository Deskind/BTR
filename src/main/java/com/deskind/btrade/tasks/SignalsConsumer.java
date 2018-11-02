package com.deskind.btrade.tasks;

import com.deskind.btrade.entities.Signal;
import com.deskind.btrade.entities.SignalManager;

public class SignalsConsumer extends Thread{

	@Override
	public void run() {
		while(true) {
			try {
				Signal s = SignalManager.getSignalsQueue().take();
				System.out.println(s.toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
