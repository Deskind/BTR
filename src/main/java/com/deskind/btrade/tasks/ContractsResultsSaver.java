package com.deskind.btrade.tasks;

import java.io.IOException;
import java.util.List;

import javax.websocket.Session;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.requests.ProfitTableRequest;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.google.gson.Gson;

public class ContractsResultsSaver extends Thread{
	private static final int PROFIT_TABLE_LIMIT = 20;
	private List<Trader> traders;

	public ContractsResultsSaver(List<Trader> traders) {
		super();
		this.traders = traders;
	}

	@Override
	public void run() {
		while(ManagerServlet.isWorking()) {
			for(Trader trader : traders) {
				
				if(!trader.getContracts().isEmpty() && !trader.getTsList().isEmpty()) {
					
					System.out.println("Need to save contract to database");
				
					TradingSystem tradingSystem = trader.getTsList().get(0);
					
					Session session = tradingSystem.getSession();
					
					if(session != null && session.isOpen()) {
						ProfitTableRequest profitTableRequest = new ProfitTableRequest(1, 1, PROFIT_TABLE_LIMIT);
						try {
							session.getBasicRemote().sendText(new Gson().toJson(profitTableRequest));
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("+++ Ne ydalos' zaprosit' Profit Table from binary ....");
						}
					}
					
				
				}else {
					System.out.println("+++ Nothing to save to database ... ");
				}
			}
			
			try {
				sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
