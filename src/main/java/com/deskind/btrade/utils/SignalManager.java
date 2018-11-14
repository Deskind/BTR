package com.deskind.btrade.utils;

import java.util.concurrent.ArrayBlockingQueue;

import com.deskind.btrade.entities.Signal;

public class SignalManager {
	private static int SIGNALS_INITIAL_CAPACITY = 30;
	
	private static ArrayBlockingQueue<Signal> signalsQueue = new ArrayBlockingQueue<>(SIGNALS_INITIAL_CAPACITY);
	
	
	public static void addNewSignal(Signal signal) {
		try {
			signalsQueue.put(signal);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Signal added to queue " + signal.toString());
	}
	
	public static String printSignalsQueue() {
		return signalsQueue.toString();
	}
	
	public static ArrayBlockingQueue<Signal> getSignalsQueue(){
		return signalsQueue;
	}
}
