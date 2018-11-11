package com.deskind.btrade.tasks;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import com.deskind.btrade.ManagerServlet;
import com.deskind.btrade.binary.requests.ProfitTableRequest;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.utils.ContractDetails;
import com.google.gson.Gson;

public class ContractsResultsSaver extends Thread{
	private static final int TIME_OFFSET = 10;
	private List<Trader> traders;

	public ContractsResultsSaver(List<Trader> traders) {
		super();
		this.traders = traders;
	}

	@Override
	public void run() {
		while(ManagerServlet.isWorking()) {
			for(Trader trader : traders) {
				Map<String, ContractDetails> traderContracts = trader.getContracts();
				
				if(!traderContracts.isEmpty() && !trader.getTsList().isEmpty()) {
					
					Session session = trader.getTsList().get(0).getSession();
					
					for(Map.Entry<String, ContractDetails> entry : traderContracts.entrySet()) {
						ContractDetails details = entry.getValue();
						
						long dateFrom = details.getBuyTime() - TIME_OFFSET;
						long dateTo = details.getSellTime() + TIME_OFFSET;
						
						Date currentDate = new Date(System.currentTimeMillis());
						Date sellDate = new Date(dateTo * 1000);
						
						if(currentDate.after(sellDate)) {
							ProfitTableRequest request = new ProfitTableRequest(1, 1, dateFrom, dateTo);
							
							if(session != null && session.isOpen()) {
								try {
									session.getBasicRemote().sendText(new Gson().toJson(request));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				
				}else {
				}
				
								
				try {
					sleep(7777);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
