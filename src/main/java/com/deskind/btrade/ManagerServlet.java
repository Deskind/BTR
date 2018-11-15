package com.deskind.btrade;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCode;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.tasks.ConnectionPointsDestroyer;
import com.deskind.btrade.tasks.ConnectionPointsInitializer;
import com.deskind.btrade.tasks.ContractsResultsSaver;
import com.deskind.btrade.tasks.SignalsConsumer;
import com.deskind.btrade.tasks.StayAlive;
import com.deskind.btrade.utils.HibernateUtil;
import com.deskind.btrade.utils.SignalManager;

import logs.MyFormatter;

/**
 * Servlet manages start/stop trading process
 */
@WebServlet(name = "ManagerServlet", urlPatterns = { "/manager" })
public class ManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private List<Trader> traders;
	
	private static Logger logger;

	private static boolean isWorking = false;
	
	//file handler for logger
	private FileHandler handler = null;
	
	//contains 3 id for different lots
	private static String[] binaryIDs;
	
	//Thread which consumes(process) signals when they come
	private Thread signalsConsumer;
	
	//payout size
	private static float payout = 65;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String answer = "";

		switch (request.getParameter("action")) {

			case "start": {
				answer = processStartTrading(request.getParameter("appIDs"));
				response.getWriter().println(answer);
				return;
			}
			
			case "stop":{
				answer = processStopTrading();
				response.getWriter().println(answer);
				return;
			}
			
			case "setMinimalPayout": {
				float minimalPayout = Float.valueOf(request.getParameter("payoutValue"));
				setPayout(minimalPayout);
				return;
			}
			
			case "savesignal": {
				Trader trader = traders.get(0);
				
				Session session = HibernateUtil.getSession();
				Transaction transaction = session.beginTransaction();
				
				session.saveOrUpdate(trader);
				
				transaction.commit();
				session.close();
				
				
				return;
			}
			
			case "savelogin": {
				Trader trader = traders.get(0);
				
				Session session = HibernateUtil.getSession();
				Transaction transaction = session.beginTransaction();
				
				for(TradingSystem ts: trader.getTsList()) {
					session.saveOrUpdate(ts);
				}
				
				System.out.println("Saving done!");
				
				transaction.commit();
				session.close();
				return;
			}
			
			case "closesession": {
				traders.get(0).getTsByName("t3").getSession().close();
				return;
			}
		}
	}
	
	/**
	 * Method for stopping trading process
	 */
	private String processStopTrading() {
		if(!isWorking) return "Trading process is not started ...";
		
		//flag
		isWorking = false;
		
		signalsConsumer.interrupt();
		
		//closing sessions
		Thread connectionPointsDestroyer = new ConnectionPointsDestroyer(TraderLifecycle.getTraders());
		connectionPointsDestroyer.setName("+++Connection points destroyer thread");
		connectionPointsDestroyer.start();
		
		//Hibernate cleanup
//		HibernateUtil.getSessionFactory().close();
		
		return "Trading process stopped ...";
	}

	/**
	 * Method starts trading process
	 * 
	 * @param parameter
	 */
	private String processStartTrading(String ids) {
		//getting traders
		traders = TraderLifecycle.getTraders();
		
		//if trading process already started
		if(isWorking) return "Trading process already started ...";
		//if there are no traders
		if(traders == null || traders.isEmpty()) return "No traders. (Hint: Reload web browser page)";
		
		//normal flow 
		//getting app id
		binaryIDs = ids.split("-");
		
		//touch flag
		isWorking = true;
		
		//running signals consumer thread
		signalsConsumer = new SignalsConsumer(SignalManager.getSignalsQueue(), traders);
		signalsConsumer.setName("+++SignalsConsumerThread");
		signalsConsumer.start();

		//run ConnectionPointsInitializer thread
		Thread pointsInitializer = new ConnectionPointsInitializer(traders);
		pointsInitializer.setName("Points Initializer");
		pointsInitializer.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//run 'stay alive' thread
		Thread stayAliveThread = new StayAlive(traders);
		stayAliveThread.setName("+++StayAlive");
		stayAliveThread.start();
		
		//run 'ContractsResultsSaver' thread
		Thread contractsResultsSaverThread = new ContractsResultsSaver(traders);
		contractsResultsSaverThread.setName("+++ContractsResultsSaverThread");
		contractsResultsSaverThread.start();
		
		return "Trading process started ...";
	}
	
	@Override
	public void init() {
		
		//logging stuff
		logger = Logger.getLogger(ManagerServlet.class.getName());

		try {
			handler = new FileHandler("c:\\btr_logs\\ManagerLogMessages.txt", false);
			logger.addHandler(handler);
			handler.setFormatter(new MyFormatter());
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void destroy() {
		handler.close();
	}


	
	//GETTERS
	public static boolean isWorking() {
		return isWorking;
	}
	
	public static String[] getBinaryIDs() {
		return binaryIDs;
	}

	
	public static Logger getLogger() {
		return logger;
	}

	public static float getPayout() {
		return payout;
	}
	
	//SETTERS
	
	public static void setPayout(float payout) {
		ManagerServlet.payout = payout;
	}

	
	
	
}
